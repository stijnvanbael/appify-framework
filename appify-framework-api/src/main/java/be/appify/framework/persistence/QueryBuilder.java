package be.appify.framework.persistence;

import java.util.Map;

import com.google.common.base.Function;

public interface QueryBuilder<T> extends Query<T> {
	QueryConditionBuilder<T> where(String field);

	<QR> Query<QR> byNativeQuery(String nativeQuery, Map<String, Object> parameters, Function<Object, QR> mapper);

}