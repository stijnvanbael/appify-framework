package be.appify.framework.view.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class SimpleRequest implements Request {

    private Map<String, List<String>> parameters;
    private Map<String, String> headers;
    private URI uri;
    private RequestMethod method;

    private SimpleRequest() {
    }

    @Override
    public List<String> parameterNames() {
        return Lists.newArrayList(parameters.keySet());
    }

    @Override
    public List<String> parameter(String name) {
        return parameters.get(name);
    }

    @Override
    public String header(String name) {
        return headers.get(name);
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public RequestMethod method() {
        return method;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, List<String>> parameters = Maps.newHashMap();
        private Map<String, String> headers = Maps.newHashMap();
        private URI uri;
        private RequestMethod method;

        public Builder parameter(String name, List<String> values) {
            parameters.put(name, values);
            return this;
        }

        public Builder parameter(String name, String... values) {
            parameters.put(name, Lists.newArrayList(values));
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder method(RequestMethod method) {
            this.method = method;
            return this;
        }

        public Request build() {
            SimpleRequest request = new SimpleRequest();
            request.headers = headers;
            request.parameters = parameters;
            request.uri = uri;
            request.method = method;
            return request;
        }
    }
}
