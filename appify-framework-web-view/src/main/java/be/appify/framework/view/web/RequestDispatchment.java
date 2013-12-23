package be.appify.framework.view.web;

import be.appify.framework.annotation.Context;
import be.appify.framework.annotation.Parameter;
import be.appify.framework.annotation.Repository;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class RequestDispatchment {
    private Request request;
    private final String rootPackage;
    private final ContextProvider contextProvider;
    private String finalPackage = null;
    private Class<?> type = null;
    private Object instance = null;

    public RequestDispatchment(Request request, String rootPackage, ContextProvider contextProvider) {
        this.request = request;
        this.rootPackage = rootPackage;
        this.contextProvider = contextProvider;
    }

    public Response dispatch() {
        String path = request.uri().getPath();
        String[] parts = path.split("/");
        StringBuilder packageBuilder = new StringBuilder(rootPackage);
        for (String part : parts) {
            if (StringUtils.isNotBlank(part)) {
                if (finalPackage == null) {
                    determineType(packageBuilder, part);
                } else if (instance == null) {
                    findInRepository(part);
                } else {
                    invokeMethod(part);
                }
            }
        }
        for(String parameterName : request.parameterNames()) {
            List<String> values = request.parameter(parameterName);
            invokeMethod(parameterName, values.toArray(new String[values.size()]));
        }
        return null;
    }

    private void invokeMethod(String part, String... parametersAsString) {
        for (Method method : instance.getClass().getMethods()) {
            if (method.getName().equals(part)) {
                List<Object> parameters = Lists.newArrayList();
                int i = 0;
                for (Class<?> parameterType : method.getParameterTypes()) {
                    Annotation[] annotations = method.getParameterAnnotations()[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Context) {
                            Collection<?> values = contextProvider.findAll(parameterType);
                            if (values.isEmpty()) {
                                throw new RuntimeException("No instance of " + parameterType.getName() + " found in context");
                            }
                            parameters.add(values.iterator().next());
                        } else if(annotation instanceof Parameter) {
                            parameters.add(ConvertUtils.convert(parametersAsString[0], parameterType));
                        }
                    }
                    i++;
                }
                try {
                    Object result = method.invoke(instance, parameters.toArray());
                    if(result != null) {
                        instance = result;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Exception calling " + instance.getClass() + "." + method.getName(), e);
                }
            }
        }
    }

    private void findInRepository(String part) {
        Repository repositoryAnnotation = type.getAnnotation(Repository.class);
        Class<?> repositoryType = repositoryAnnotation.type();
        Collection<?> repositories = contextProvider.findAll(repositoryType);
        if (repositories.isEmpty()) {
            throw new RuntimeException("No instance of " + repositoryType.getName() + " found in context");
        }
        Object repository = repositories.iterator().next();
        try {
            Method findMethod = repositoryType.getMethod(repositoryAnnotation.findMethod(), String.class);
            instance = findMethod.invoke(repository, part);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No such find method: " + repositoryType + "." + repositoryAnnotation.findMethod(), e);
        } catch (Exception e) {
            throw new RuntimeException("Exception calling: " + repositoryType + "." + repositoryAnnotation.findMethod(), e);
        }
    }

    private void determineType(StringBuilder packageBuilder, String part) {
        if (Character.isLowerCase(part.charAt(0))) {
            packageBuilder.append(".").append(part);
        } else {
            finalPackage = packageBuilder.toString();
            String className = finalPackage + "." + part;
            try {
                type = Class.forName(className);
                Collection<?> implementations = contextProvider.findAll(type);
                if (implementations.size() == 1) {
                    instance = implementations.iterator().next();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + className, e);
            }
        }
    }
}
