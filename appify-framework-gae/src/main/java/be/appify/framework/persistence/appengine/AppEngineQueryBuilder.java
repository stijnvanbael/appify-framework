package be.appify.framework.persistence.appengine;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.appify.framework.cache.Cache;
import be.appify.framework.cache.CacheKey;
import be.appify.framework.persistence.OrderByBuilder;
import be.appify.framework.persistence.QueryBuilder;
import be.appify.framework.persistence.QueryConditionBuilder;
import be.appify.framework.persistence.WhereClauseBuilder;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class AppEngineQueryBuilder<T> implements QueryBuilder<T>, QueryConditionBuilder<T>, WhereClauseBuilder<T>, OrderByBuilder<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppEngineQueryBuilder.class);

	private final DatastoreService datastore;
	private final Class<T> entityType;
	private Query query;
	private final Cache cache;
	private Filter whereClause;
	private String currentConditionField;
	private final String ancestorField;

	private Transaction transaction;

	public AppEngineQueryBuilder(DatastoreService datastore, Transaction transaction, Cache cache, Class<T> entityType) {
		this.datastore = datastore;
		this.cache = cache;
		this.entityType = entityType;
		this.ancestorField = AppEngineUtil.getAncestorFieldName(entityType);
	}

	@Override
	public T asSingle() {
		List<T> results = asList();
		if (results.size() > 1) {
			throw new IllegalStateException("Expected a single result, found " + results.size());
		}
		return results.isEmpty() ? null : results.get(0);
	}

	@SuppressWarnings("unchecked")
	private T toExpectedType(Entity entity, Class<?> expectedType) {
		T object;
		try {
			object = (T) ConstructorUtils.invokeConstructor(expectedType, new Object[0]);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to invoke constructor on " + entityType
					+ ". Does a no-args constructor exist and is it accessible?", e);
		}
		Map<String, Object> properties = entity.getProperties();
		for (String name : properties.keySet()) {
			Object value = properties.get(name);
			PropertyDescriptor property;
			try {
				property = PropertyUtils.getPropertyDescriptor(object, name);
				Method writeMethod = property.getWriteMethod();
				Class<?> expectedPropertyType = writeMethod.getParameterTypes()[0];
				writeMethod.invoke(object, convert(value, expectedPropertyType));
			} catch (Exception e) {
				throw new IllegalStateException("Failed to set property '" + name + "' on " + entityType
						+ ". Does a setter exist for the property and is it accessible?", e);
			}
		}
		Key parentKey = entity.getParent();
		if (parentKey != null) {
			try {
				Field field = AppEngineUtil.getField(expectedType, ancestorField);
				Class<?> expectedPropertyType = field.getType();
				PropertyUtils.setProperty(object, ancestorField, convert(parentKey, expectedPropertyType));
			} catch (Exception e) {
				throw new IllegalStateException("Failed to set property '" + ancestorField + "' on " + entityType
						+ ". Does a setter exist for the property and is it accessible?", e);
			}
		}
		return object;
	}

	private Object convert(Object value, Class<?> expectedType) {
		if (value == null) {
			return null;
		}
		if (expectedType.isAnnotationPresent(javax.persistence.Entity.class)) {
			Key key = (Key) value;
			try {
				LOGGER.debug("Finding entity with key: {}", key);
				CacheKey<Entity> cacheKey = new CacheKey<Entity>(Entity.class, key);
				Entity entity = cache.findSingle(cacheKey);
				if (entity == null) {
					entity = datastore.get(transaction, key);
					cache.put(cacheKey, entity);
				}
				return toExpectedType(entity, expectedType);
			} catch (EntityNotFoundException e) {
				throw new IllegalStateException("No entity found for key " + key, e);
			}
		}
		return ConvertUtils.convert(value, expectedType);
	}

	private void addWhereClause() {
		if (query == null) {
			query = new Query(KeyBuilder.getKind(entityType));
		}
		if (whereClause != null) {
			query.setFilter(whereClause);
		}
	}

	@Override
	public List<T> asList() {
		addWhereClause();
		LOGGER.debug("Executing query: {}", query);
		PreparedQuery pq = datastore.prepare(transaction, query);

		List<T> results = Lists.newArrayList();
		for (Entity entity : pq.asIterable()) {
			if (!entity.getKey().equals(query.getAncestor())) {
				results.add(toExpectedType(entity, entityType));
			}
		}
		return results;
	}

    @Override
	public OrderByBuilder<T> orderBy(String name) {
		// TODO: order by
		return this;
	}

	@Override
	public QueryConditionBuilder<T> where(String field) {
		this.currentConditionField = field;
		return this;
	}

	@Override
	public WhereClauseBuilder<T> equalTo(Object value) {
		value = toArgument(value);
		if (currentConditionField.equals(ancestorField)) {
			Preconditions.checkNotNull(value, "Cannot query on ancestor null.");
			if (!(value instanceof Key)) {
				throw new IllegalStateException("Expected @Entity annotated type, found " + value.getClass() + ".");
			}
			query = new Query(KeyBuilder.getKind(entityType), (Key) value);
		} else {
			Filter condition = new FilterPredicate(currentConditionField, FilterOperator.EQUAL, value);
			addCondition(condition);
		}
		return this;
	}

	@Override
	public WhereClauseBuilder<T> isNull() {
		Filter condition = new FilterPredicate(currentConditionField, FilterOperator.EQUAL, null);
		addCondition(condition);
		return this;
	}

	private void addCondition(Filter condition) {
		if (whereClause == null) {
			whereClause = condition;
		} else {
			whereClause = CompositeFilterOperator.and(whereClause, condition);
		}
	}

	@Override
	public WhereClauseBuilder<T> in(Object... values) {
		checkNotAncestor();
		List<Object> argumentValues = Lists.newArrayList();
		for (Object value : values) {
			argumentValues.add(toArgument(value));
		}
		Filter condition = new FilterPredicate(currentConditionField, FilterOperator.IN, argumentValues);
		addCondition(condition);
		return this;
	}

	private void checkNotAncestor() {
		if (currentConditionField.equals(ancestorField)) {
			throw new IllegalArgumentException("Can only query equalTo on @Ancestor annotated field.");
		}
	}

	private Object toArgument(Object value) {
		if (hasEntityAnnotation(value)) {
			return KeyBuilder.createKey(value);
		}
		return value;
	}

	@Override
	public QueryConditionBuilder<T> and(String field) {
		this.currentConditionField = field;
		return this;
	}

	private boolean hasEntityAnnotation(Object value) {
		return value != null && value.getClass().isAnnotationPresent(javax.persistence.Entity.class);
	}

	@Override
	public WhereClauseBuilder<T> like(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <QR> be.appify.framework.persistence.Query<QR> byNativeQuery(String nativeQuery, Map<String, Object> parameters, Function<Object, QR> mapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public be.appify.framework.persistence.Query<T> descending() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public be.appify.framework.persistence.Query<T> limit(int maxResults) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public be.appify.framework.persistence.Query<T> startAt(int firstResult) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThanOrEqualTo(V value) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThan(V value) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> lessThan(V value) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> lessThanOrEqualTo(V value) {
		// TODO Auto-generated method stub
		return this;
	}

}
