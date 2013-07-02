package be.appify.framework.persistence;

public interface QueryConditionBuilder<T> {
	WhereClauseBuilder<T> equalTo(Object value);

	WhereClauseBuilder<T> in(Object... values);

	WhereClauseBuilder<T> like(String value);

	WhereClauseBuilder<T> isNull();

	<V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThanOrEqualTo(V value);

	<V extends Comparable<? super V>> WhereClauseBuilder<T> greaterThan(V value);

	<V extends Comparable<? super V>> WhereClauseBuilder<T> lessThan(V value);

	<V extends Comparable<? super V>> WhereClauseBuilder<T> lessThanOrEqualTo(V value);

}
