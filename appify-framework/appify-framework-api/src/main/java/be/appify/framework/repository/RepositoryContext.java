package be.appify.framework.repository;

import be.appify.framework.domain.AbstractEntity;
import be.appify.framework.persistence.Transaction;

class RepositoryContext<T extends AbstractEntity> {
	private final Transaction transaction;
	private final AbstractPersistentRepository<T> repository;

	RepositoryContext(Transaction transaction, AbstractPersistentRepository<T> repository) {
		this.transaction = transaction;
		this.repository = repository;
	}

	Transaction getTransaction() {
		return transaction;
	}

	AbstractPersistentRepository<T> getRepository() {
		return repository;
	}
}
