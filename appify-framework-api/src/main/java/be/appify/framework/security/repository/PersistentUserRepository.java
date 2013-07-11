package be.appify.framework.security.repository;

import javax.inject.Inject;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.repository.AbstractPersistentRepository;
import be.appify.framework.repository.TransactionalJob;
import be.appify.framework.security.domain.User;

public class PersistentUserRepository<U extends User> extends AbstractPersistentRepository<U> implements UserRepository<U> {
	@Inject
	public PersistentUserRepository(Persistence persistence, Class<? extends U> type) {
		super(persistence, type);
	}

	@Override
	public U findByEmailAddress(final String emailAddress) {
		return doInTransaction(new TransactionalJob<U, U>() {

			@Override
			public U execute() {
				return find().where("emailAddress").equalTo(emailAddress).asSingle();
			}
		});
	}

	@Override
	public void store(final U user) {
		doInTransaction(new TransactionalJob<Void, U>() {
			@Override
			public Void execute() {
				store(user);
				return null;
			}
		});
	}

    @Override
    public long count() {
        return doInTransaction(new TransactionalJob<Long, U>() {
            @Override
            public Long execute() {
                return count().asSingle();
            }
        });
    }

}
