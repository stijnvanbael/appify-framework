package be.appify.framework.security.repository;

import javax.inject.Inject;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.repository.AbstractPersistentRepository;
import be.appify.framework.repository.TransactionalJob;
import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.User;
import be.appify.framework.util.TypeBuilder;

public class PersistentAuthenticationRepository<U extends User> extends AbstractPersistentRepository<Authentication<U>> implements AuthenticationRepository<U> {

	@Inject
	public PersistentAuthenticationRepository(Persistence persistence) {
		super(persistence, new TypeBuilder<Authentication<U>>() {
		});
	}

	@Override
	public Authentication<U> findByToken(final String authenticationToken) {
		return doInTransaction(new TransactionalJob<Authentication<U>, Authentication<U>>() {
			@Override
			public Authentication<U> execute() {
				return find().where("id").equalTo(authenticationToken).asSingle();
			}
		});
	}

	@Override
	public void store(final Authentication<U> authentication) {
		doInTransaction(new TransactionalJob<Void, Authentication<U>>() {
			@Override
			public Void execute() {
				store(authentication);
				return null;
			}
		});
	}

	@Override
	public void delete(final Authentication<U> authentication) {
		doInTransaction(new TransactionalJob<Void, Authentication<U>>() {
			@Override
			public Void execute() {
				delete(authentication);
				return null;
			}

		});
	}

}
