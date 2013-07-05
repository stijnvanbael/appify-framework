package be.appify.framework.persistence;


public interface WhereClauseBuilder<T> extends Query<T> {
	QueryConditionBuilder<T> and(String field);
}
