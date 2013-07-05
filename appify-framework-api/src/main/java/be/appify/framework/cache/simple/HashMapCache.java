package be.appify.framework.cache.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import be.appify.framework.cache.AbstractCache;
import be.appify.framework.cache.CacheKey;

public class HashMapCache extends AbstractCache {
	private final Map<CacheKey<?>, Collection<?>> delegateCache;

	public HashMapCache() {
		this(100);
	}

	public HashMapCache(int maxSize) {
		delegateCache = Collections.synchronizedMap(new LimitedMap<CacheKey<?>, Collection<?>>(maxSize));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> find(CacheKey<T> cacheKey) {
		return (Collection<T>) delegateCache.get(cacheKey);
	}

	@Override
	public <T> void put(CacheKey<T> cacheKey, Collection<T> values) {
		delegateCache.put(cacheKey, values);
	}

	@Override
	public void evict(CacheKey<?> cacheKey) {
		delegateCache.remove(cacheKey);
	}

}
