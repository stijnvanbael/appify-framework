package be.appify.framework.repository;

import be.appify.framework.persistence.QueryBuilder;
import be.appify.framework.persistence.Transaction;

public abstract class TransactionalJob<R, E> {
	private AbstractPersistentRepository<E> repository;
	private Transaction transaction;

	public abstract R execute();

	public void setRepository(AbstractPersistentRepository<E> repository) {
		this.repository = repository;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public final QueryBuilder<E> find() {
		return repository.find(transaction);
	}

    public final QueryBuilder<Long> count() {
        return repository.count(transaction);
    }

	public final void store(E entity) {
		repository.store(transaction, entity);
	}

	public final void delete(E entity) {
		repository.delete(transaction, entity);
	}

}
