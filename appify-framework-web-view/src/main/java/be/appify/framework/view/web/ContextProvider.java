package be.appify.framework.view.web;

import java.util.Collection;

public interface ContextProvider {

    <T> Collection<T> findAll(Class<T> type);

    <T> Collection<T> findAll(String name, Class<T> expectedType);
}
