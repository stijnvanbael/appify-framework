package be.appify.framework.view.web;

import java.net.URI;
import java.util.List;

public interface Request {
    List<String> parameterNames();
    List<String> parameter(String name);
    String header(String name);
    URI uri();
    RequestMethod method();
}
