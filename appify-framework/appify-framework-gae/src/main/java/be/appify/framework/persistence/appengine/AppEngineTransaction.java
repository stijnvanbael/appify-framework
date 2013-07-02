package be.appify.framework.persistence.appengine;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.persistence.Transient;


import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.appify.framework.cache.*;
import be.appify.framework.persistence.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.base.Objects;

public class AppEngineTransaction implements Transaction {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppEngineTransaction.class);

	private final com.google.appengine.api.datastore.Transaction transaction;
	private final DatastoreService datastore;
	private final Cache cache;

	public AppEngineTransaction(com.google.appengine.api.datastore.Transaction transaction, DatastoreService datastore, Cache cache) {
		this.transaction = transaction;
		this.datastore = datastore;
		this.cache = cache;
		LOGGER.debug("[{}] Transaction started.", transaction.getId());
	}

	@Override
	public void save(Object object) {
		saveInternal(object);
	}

	private Key saveInternal(Object object) {
		if (!hasEntityAnnotation(object)) {
			throw new IllegalArgumentException("Missing @Entity annotation on " + object.getClass());
		}
		Entity entity = toEntity(object);
		boolean changed = false;
		try {
			CacheKey<Entity> cacheKey = new CacheKey<Entity>(Entity.class, entity.getKey());
			Entity existingEntity = cache.findSingle(cacheKey);
			if (existingEntity == null) {
				existingEntity = datastore.get(entity.getKey());
			}
			changed = entitiesDifferent(entity, existingEntity);
		} catch (EntityNotFoundException e) {
			changed = true;
		}
		cache(entity);
		if (changed) {
			LOGGER.debug("[{}] Storing entity: {}", transaction.getId(), entity.getKey());
			datastore.put(transaction, entity);
		}
		return entity.getKey();
	}

	private boolean entitiesDifferent(Entity entity1, Entity entity2) {
		if (!entity1.equals(entity2) ||
				entity1.getProperties().size() != entity2.getProperties().size()) {
			return true;
		}
		for (String name : entity1.getProperties().keySet()) {
			Object value1 = entity1.getProperty(name);
			Object value2 = entity2.getProperty(name);
			if (!Objects.equal(value1, value2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void delete(Object object) {
		Key key = KeyBuilder.createKey(object);
		LOGGER.debug("[{}] Deleting entity: {}", transaction.getId(), key);
		cache.evict(new CacheKey<Entity>(Entity.class, key));
		datastore.delete(transaction, key);
	}

	@Override
	public <T> QueryBuilder<T> find(Class<T> entityType) {
		return new AppEngineQueryBuilder<T>(datastore, transaction, cache, entityType);
	}

	private boolean hasEntityAnnotation(Object value) {
		return value != null && value.getClass().isAnnotationPresent(javax.persistence.Entity.class);
	}

	@Override
	public void commit() {
		transaction.commit();
		LOGGER.debug("[{}] Transaction committed.", transaction.getId());
	}

	@Override
	public void rollback() {
		transaction.rollback();
		LOGGER.debug("[{}] Transaction rolled back.", transaction.getId());
	}

	private Entity toEntity(Object object) {
		Key key = KeyBuilder.createKey(object);
		Entity entity;
		Object ancestor = getAncesor(object);
		if (ancestor != null) {
			saveInternal(ancestor);
		}
		entity = new Entity(key);
		for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(object.getClass())) {
			String name = property.getName();
			if (property.getReadMethod() != null && !"class".equals(name)) {
				Method readMethod = property.getReadMethod();
				if (!readMethod.isAnnotationPresent(Transient.class)) {
					try {
						Object value = readMethod.invoke(object);
						if (value == null || value != ancestor) {
							if (hasEntityAnnotation(value)) {
								value = saveInternal(value);
							}
							entity.setProperty(name, value);
						}
					} catch (Exception e) {
						throw new IllegalStateException("Failed to get property '" + name + "' on " + object.getClass()
								+ ". Does a getter exist for the property and is it accessible?", e);
					}
				}
			}
		}
		return entity;
	}

	private Object getAncesor(Object object) {
		Class<? extends Object> type = object.getClass();
		String name = AppEngineUtil.getAncestorFieldName(type);
		if (name != null) {
			try {
				return PropertyUtils.getProperty(object, name);
			} catch (Exception e) {
				throw new IllegalStateException("Failed to get property '" + name + "' on " + object.getClass()
						+ ". Does a getter exist for the property and is it accessible?", e);
			}
		}
		return null;
	}

	private void cache(Entity entity) {
		cache.put(new CacheKey<Entity>(Entity.class, entity.getKey()), entity);
	}

	@Override
	public boolean isActive() {
		return transaction.isActive();
	}

}
