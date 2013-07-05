package be.appify.framework.persistence;

public interface OrderByBuilder<T> extends Query<T> {
	Query<T> descending();
}
