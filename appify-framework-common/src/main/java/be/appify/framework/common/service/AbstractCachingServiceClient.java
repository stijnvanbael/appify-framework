package be.appify.framework.common.service;

import java.util.Map;

import be.appify.framework.cache.Cache;
import be.appify.framework.cache.CacheKey;

import com.google.api.client.http.HttpTransport;
import com.google.common.base.Function;

public abstract class AbstractCachingServiceClient extends AbstractServiceClient {

	private final Cache cache;

	public AbstractCachingServiceClient(HttpTransport transport, Cache cache) {
		super(transport);
		this.cache = cache;
	}

	protected final <M, R> R callServiceCacheResult(Class<M> messageType, Class<R> resultType, String url, Map<String, String> parameters,
			Function<M, R> converter) {
		CacheKey<R> key = new CacheKey<R>(resultType, parameters);
		R result = cache.findSingle(key);
		if (result == null) {
			result = callService(messageType, url, parameters, converter);
			if (result != null) {
				cache.put(key, result);
			}
		}
		return result;
	}
}
