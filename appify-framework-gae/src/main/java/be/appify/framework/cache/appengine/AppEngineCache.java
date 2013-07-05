package be.appify.framework.cache.appengine;

import java.util.Collection;
import java.util.logging.Level;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.appify.framework.cache.*;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class AppEngineCache extends AbstractCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppEngineCache.class);

	private static final int ONE_HOUR = 1000 * 60 * 60;
	private final MemcacheService delegateCache;
	private final Expiration expiration;

	public AppEngineCache() {
		this(ONE_HOUR);
	}

	public AppEngineCache(int cacheExpirationInMillis) {
		delegateCache = MemcacheServiceFactory.getMemcacheService();
		delegateCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		this.expiration = Expiration.byDeltaMillis(cacheExpirationInMillis);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> find(CacheKey<T> cacheKey) {
		Collection<T> values = (Collection<T>) delegateCache.get(cacheKey);
		if (values == null) {
			LOGGER.debug("Cache miss on {}", cacheKey);
		} else {
			LOGGER.debug("Cache hit on {}", cacheKey);
		}
		return values;
	}

	@Override
	public <T> void put(CacheKey<T> cacheKey, Collection<T> values) {
		delegateCache.put(cacheKey, values, expiration);
		LOGGER.debug("Added to cache: {}", cacheKey);
	}

	@Override
	public void evict(CacheKey<?> cacheKey) {
		if (delegateCache.delete(cacheKey)) {
			LOGGER.debug("Evicted from cache: {}", cacheKey);
		}
	}

}
