package be.appify.framework.persistence;

import java.util.List;

public interface Query<T> {
	T asSingle();

	List<T> asList();

	OrderByBuilder<T> orderBy(String name);

	Query<T> limit(int maxResults);

	Query<T> startAt(int firstResult);
}
