package be.appify.framework.view.web;

import be.appify.framework.annotation.Context;
import be.appify.framework.annotation.Repository;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class DefaultRequestDispatcher implements RequestDispatcher {
    private final String rootPackage;
    private final ContextProvider contextProvider;

    public DefaultRequestDispatcher(String rootPackage, ContextProvider contextProvider) {
        this.rootPackage = rootPackage;
        this.contextProvider = contextProvider;
    }

    @Override
    public Response dispatch(Request request) {
        RequestDispatchment dispatchment = new RequestDispatchment(request, rootPackage, contextProvider);
        return dispatchment.dispatch();
    }
}
