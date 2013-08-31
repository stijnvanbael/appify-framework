package be.appify.framework.persistence;

public interface Transaction {
	void save(Object entity);

	<T> QueryBuilder<T> find(Class<T> entityType);

	void delete(Object entity);

	void commit();

	void rollback();

	boolean isActive();

    QueryBuilder<Long> count(Class<?> persistentClass);

    int execute(String nativeCommand);
}
