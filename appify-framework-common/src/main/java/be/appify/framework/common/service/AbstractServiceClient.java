package be.appify.framework.common.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.base.Function;
import com.google.common.collect.Sets;

public abstract class AbstractServiceClient {
	protected static final String GET = "GET";
	protected static final String POST = "POST";

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceClient.class);

	private final HttpRequestFactory requestFactory;
	private int connectTimeout = 2500;
	private int readTimeout = 5000;
	private int retryCount = 1;

	private String requestMethod = GET;

	public AbstractServiceClient(HttpTransport transport) {
		final JsonFactory jsonFactory = new GsonFactory();
		this.requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {

			@Override
			public void initialize(HttpRequest request) throws IOException {
				request.setParser(new JsonObjectParser(jsonFactory));
			}
		});
	}

	protected void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	protected final <T> T callService(Class<T> expectedType, String url, Map<String, String> parameters) {
		return callServiceInternal(expectedType, url, parameters, 1);
	}

	protected final <M, R> R callService(Class<M> messageType, String url, Map<String, String> parameters, Function<M, R> converter) {
		M message = callServiceInternal(messageType, url, parameters, 1);
		return converter.apply(message);
	}

	private <T> T callServiceInternal(Class<T> expectedType, String url, Map<String, String> parameters, int attempt) {
		T message = null;
		GenericUrl genericUrl = null;
		try {
			genericUrl = constructURL(url, parameters);
			HttpRequest request = requestFactory.buildGetRequest(genericUrl);
			request.setConnectTimeout(connectTimeout);
			request.setReadTimeout(readTimeout);
			request.setRequestMethod(requestMethod);
			HttpResponse response = request.execute();
			message = response.parseAs(expectedType);
			response.disconnect();
		} catch (IOException e) {
			if (attempt < retryCount) {
				LOGGER.warn("Error calling URL <" + genericUrl + ">\n" + e.getMessage() + "\nRetrying (" + attempt + ")");
				message = callServiceInternal(expectedType, url, parameters, attempt + 1);
			} else {
				throw new RemoteException("Error calling URL <" + genericUrl + ">"
						+ (retryCount > 1 ? " after " + attempt + " retries, giving up." : ""), e);
			}
		}
		return message;
	}

	protected GenericUrl constructURL(String url, Map<String, String> parameters) {
		Set<String> parameterNames = Sets.newHashSet(parameters.keySet());
		Iterator<String> iterator = parameterNames.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			String value = parameters.get(name);
			url = replaceIfExists(url, name, value, iterator);
		}
		GenericUrl genericUrl = new GenericUrl(url);
		for (String name : parameterNames) {
			String value = parameters.get(name);
			genericUrl.put(name, value);
		}
		return genericUrl;
	}

	private String replaceIfExists(String url, String name, String value, Iterator<String> iterator) {
		if (url.contains(":" + name)) {
			try {
				iterator.remove();
				return url.replace(":" + name, URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return url;
	}

}