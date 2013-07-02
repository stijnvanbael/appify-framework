package be.appify.framework.test.service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.collect.Maps;

public class TestHandler extends AbstractHandler {

	private static final int SC_OK = 200;
	private static final Map<String, String> MIME_TYPES = Maps.newHashMap();
	private static final String DEFAULT_MIME_TYPE = "text/plain";

	static {
		MIME_TYPES.put("json", "application/json");
		MIME_TYPES.put("xml", "application/xml");
		MIME_TYPES.put("html", "text/html");
	}

	private final Map<Pattern, URL> responses = Maps.newHashMap();

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String queryString = request.getQueryString();
		String requestURI = request.getRequestURI() + (StringUtils.isNotBlank(queryString) ? "?" + queryString : "");
		boolean responded = false;
		for (Pattern regex : responses.keySet()) {
			if (regex.matcher(requestURI).matches()) {
				respond(responses.get(regex), response);
				baseRequest.setHandled(true);
				responded = true;
				break;
			}
		}
		if (!responded) {
			throw new AssertionError("No stub response registered for " + requestURI);
		}
	}

	private void respond(URL url, HttpServletResponse response) {
		try {
			String stubData = IOUtils.toString(url);
			response.setStatus(SC_OK);
			String mimeType = deriveMimeType(url);
			response.setContentType(mimeType + ";charset=utf-8");
			IOUtils.write(stubData, response.getOutputStream());
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private String deriveMimeType(URL url) {
		String path = url.getPath();
		String extension = path.substring(path.lastIndexOf('.') + 1);
		String mimeType = MIME_TYPES.get(extension);
		if (mimeType == null) {
			mimeType = DEFAULT_MIME_TYPE;
		}
		return mimeType;
	}

	public void addStubResponse(String requestURIRegex, String responseResourcePath) {
		URL url = TestHandler.class.getResource(responseResourcePath);
		if (url == null) {
			throw new IllegalArgumentException("Classpath resource '" + responseResourcePath + "' does not exist.");
		}
		responses.put(Pattern.compile(requestURIRegex), url);
	}
}