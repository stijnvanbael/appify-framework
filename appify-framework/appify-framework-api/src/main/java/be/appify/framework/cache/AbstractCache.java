package be.appify.framework.cache;

import java.util.Collection;

import com.google.common.collect.Lists;

public abstract class AbstractCache implements Cache {

	@Override
	public final <T> T findSingle(CacheKey<T> cacheKey) {
		Collection<T> values = find(cacheKey);
		if (values != null && values.size() != 1) {
			throw new IllegalArgumentException("Expected exactly 1 value but found " + values.size() + " for " + cacheKey);
		}
		if (values != null) {
			return values.iterator().next();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> void put(CacheKey<T> cacheKey, T value) {
		put(cacheKey, Lists.newArrayList(value));
	}

}
