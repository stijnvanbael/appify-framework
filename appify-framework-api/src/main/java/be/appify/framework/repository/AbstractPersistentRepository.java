package be.appify.framework.repository;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.QueryBuilder;
import be.appify.framework.persistence.Transaction;
import be.appify.framework.util.TypeBuilder;

public abstract class AbstractPersistentRepository<E> {
	private final Persistence persistence;
	private final Class<? extends E> persistentClass;

	public AbstractPersistentRepository(Persistence persistence, Class<? extends E> persistentClass) {
		this.persistence = persistence;
		this.persistentClass = persistentClass;
	}

	public AbstractPersistentRepository(Persistence persistence, TypeBuilder<? extends E> typeBuilder) {
		this(persistence, typeBuilder.build());
	}

	final void store(Transaction transaction, E entity) {
		transaction.save(entity);
		entityChanged(entity);
	}

	@SuppressWarnings("unchecked")
	final QueryBuilder<E> find(Transaction transaction) {
		return (QueryBuilder<E>) transaction.find(persistentClass);
	}

	final void delete(Transaction transaction, E entity) {
		transaction.delete(entity);
		entityChanged(entity);
	}

	protected final <R> R doInTransaction(TransactionalJob<R, E> job) {
		Transaction transaction = persistence.beginTransaction();
		job.setRepository(this);
		job.setTransaction(transaction);
		R result = null;
		try {
			result = job.execute();
			transaction.commit();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
		return result;
	}

	protected void entityChanged(E entity) {
	}

    public QueryBuilder<Long> count(Transaction transaction) {
        return (QueryBuilder<Long>) transaction.count(persistentClass);
    }
}
