package be.appify.framework.persistence.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.appify.framework.persistence.OrderByBuilder;
import be.appify.framework.persistence.Query;
import be.appify.framework.persistence.QueryBuilder;
import be.appify.framework.persistence.QueryConditionBuilder;
import be.appify.framework.persistence.WhereClauseBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class JPAQueryBuilder<T, R> implements QueryBuilder<T>, QueryConditionBuilder<T>, WhereClauseBuilder<T>, OrderByBuilder<T> {

	private final CriteriaBuilder criteriaBuilder;
	private final EntityManager entityManager;
    private final Function<Object, CriteriaQuery<T>> criteriaQueryFactory;
    private final Function<CriteriaQuery<T>, Root<R>> rootFactory;
    private CriteriaQuery<T> query;
	private Root<R> root;
	private String currentConditionField;
	private Expression<Boolean> whereClause;
	private javax.persistence.Query queryToExecute;
	private Function<Object, T> mapper;
	private final List<Order> orderBy;
	private int maxResults;
	private int firstResult;

    public JPAQueryBuilder(EntityManager entityManager, Function<Object, CriteriaQuery<T>> criteriaQueryFactory, Function<CriteriaQuery<T>, Root<R>> rootFactory) {
        this.entityManager = entityManager;
        this.criteriaQueryFactory = criteriaQueryFactory;
        this.rootFactory = rootFactory;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
		this.orderBy = Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
	@Override
	public T asSingle() {
		addWhereClause();
		Object result = null;
		try {
			initializeQuery();
			result = queryToExecute.getSingleResult();

		} catch (NoResultException e) {
			// No problemo
		}
		if (mapper != null) {
			result = mapper.apply(result);
		}
		return (T) result;
	}

	private void initializeQuery() {
		if (queryToExecute == null) {
            CriteriaQuery<T> q = query();
            root();
            queryToExecute = entityManager.createQuery(q);
		}
	}

    private CriteriaQuery<T> query() {
        if(query == null) {
            query = criteriaQueryFactory.apply(null);
        }
        return query;
    }

    private void addWhereClause() {
		if (whereClause != null) {
			query().where(whereClause);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> asList() {
		addWhereClause();
		addOrderByClause();
		initializeQuery();
		if (maxResults > 0) {
			queryToExecute.setMaxResults(maxResults);
		}
		if (firstResult > 0) {
			queryToExecute.setFirstResult(firstResult);
		}
		List<?> results = queryToExecute.getResultList();
		List<T> finalResults = Lists.newArrayList();
		if (mapper != null) {
			for (Object result : results) {
				finalResults.add(mapper.apply(result));
			}
		} else {
			finalResults.addAll((List<T>) results);
		}
		return finalResults;
	}

	private void addOrderByClause() {
		if (!orderBy.isEmpty()) {
			query().orderBy(orderBy);
		}
	}

	@Override
	public OrderByBuilder<T> orderBy(String name) {
		orderBy.add(criteriaBuilder.asc(getPath(root(), name)));
		return this;
	}

    private Root<R> root() {
        if(root == null) {
            root = rootFactory.apply(query());
        }
        return root;
    }

    @Override
	public QueryConditionBuilder<T> where(String field) {
		this.currentConditionField = field;
		return this;
	}

	@Override
	public WhereClauseBuilder<T> equalTo(Object value) {
		Predicate condition = criteriaBuilder.equal(getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

	@Override
	public WhereClauseBuilder<T> like(String value) {
		Predicate condition = criteriaBuilder.like(this.<String> getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

	@Override
	public WhereClauseBuilder<T> isNull() {
		Predicate condition = criteriaBuilder.isNull(getCurrentPath());
		addCondition(condition);
		return this;
	}

	@Override
	public WhereClauseBuilder<T> in(Object... values) {
		Predicate condition = getCurrentPath().in(values);
		addCondition(condition);
		return this;
	}

	private <P> Expression<P> getCurrentPath() {
		return getPath(root(), currentConditionField);
	}

	private <P> Path<P> getPath(Path<?> parent, String property) {
		if (property.contains(".")) {
			Path<?> path = parent.get(property.substring(0, property.indexOf(".")));
			return getPath(path, property.substring(property.indexOf(".") + 1));
		}
		return parent.get(property);
	}

	@Override
	public QueryConditionBuilder<T> and(String field) {
		this.currentConditionField = field;
		return this;
	}

	private void addCondition(Predicate condition) {
		if (whereClause == null) {
			whereClause = condition;
		} else {
			whereClause = criteriaBuilder.and(whereClause, condition);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <QR> Query<QR> byNativeQuery(String nativeQuery, Map<String, Object> parameters, Function<Object, QR> mapper) {
		this.queryToExecute = entityManager.createNativeQuery(nativeQuery);
		for (String parameterName : parameters.keySet()) {
			this.queryToExecute.setParameter(parameterName, parameters.get(parameterName));
		}
		this.mapper = (Function<Object, T>) mapper;
		return (Query<QR>) this;
	}

	@Override
	public Query<T> descending() {
		int index = orderBy.size() - 1;
		orderBy.set(index, orderBy.get(index).reverse());
		return this;
	}

	@Override
	public Query<T> limit(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	@Override
	public Query<T> startAt(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThanOrEqualTo(V value) {
		Predicate condition = criteriaBuilder.greaterThanOrEqualTo(this.<V> getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThan(V value) {
		Predicate condition = criteriaBuilder.greaterThan(this.<V> getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> lessThan(V value) {
		Predicate condition = criteriaBuilder.lessThan(this.<V> getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

	@Override
	public <V extends Comparable<? super V>> WhereClauseBuilder<T> lessThanOrEqualTo(V value) {
		Predicate condition = criteriaBuilder.lessThanOrEqualTo(this.<V> getCurrentPath(), value);
		addCondition(condition);
		return this;
	}

    public static <T> QueryBuilder<T> find(final Class<T> entityType, final EntityManager entityManager) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return new JPAQueryBuilder<>(entityManager, new Function<Object, CriteriaQuery<T>>() {
            @Override
            public CriteriaQuery<T> apply(Object input) {
                return criteriaBuilder.createQuery(entityType);
            }
        }, new Function<CriteriaQuery<T>, Root<T>>() {
            @Override
            public Root<T> apply(CriteriaQuery<T> query) {
                Root<T> root = query.from(entityType);
                query.select(root);
                return root;
            }
        });
    }

    public static <T> QueryBuilder<Long> count(final Class<T> entityType, final EntityManager entityManager) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return new JPAQueryBuilder<>(entityManager, new Function<Object, CriteriaQuery<Long>>() {
            @Override
            public CriteriaQuery<Long> apply(Object input) {
                return criteriaBuilder.createQuery(Long.class);
            }
        }, new Function<CriteriaQuery<Long>, Root<T>>() {
            @Override
            public Root<T> apply(CriteriaQuery<Long> query) {
                Root<T> root = query.from(entityType);
                query.select(criteriaBuilder.count(root));
                return root;
            }
        });
    }

    public static int execute(String nativeCommand, EntityManager entityManager) {
        return entityManager.createNativeQuery(nativeCommand).executeUpdate();
    }
}
