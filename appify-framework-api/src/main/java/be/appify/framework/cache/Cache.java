package be.appify.framework.cache;

import java.util.Collection;

public interface Cache {
	<T> Collection<T> find(CacheKey<T> cacheKey);

	<T> T findSingle(CacheKey<T> cacheKey);

	<T> void put(CacheKey<T> cacheKey, Collection<T> values);

	<T> void put(CacheKey<T> cacheKey, T value);

	void evict(CacheKey<?> cacheKey);
}
