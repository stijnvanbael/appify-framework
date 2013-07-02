package be.appify.framework.google.security.repository;

import javax.inject.Inject;

import be.appify.framework.google.security.domain.GoogleCredential;
import be.appify.framework.persistence.Persistence;
import be.appify.framework.repository.AbstractPersistentRepository;
import be.appify.framework.repository.TransactionalJob;
import be.appify.framework.security.domain.User;
import be.appify.framework.util.TypeBuilder;

public class PersistentGoogleCredentialRepository<U extends User> extends AbstractPersistentRepository<GoogleCredential<U>> implements
		GoogleCredentialRepository<U> {

	@Inject
	public PersistentGoogleCredentialRepository(Persistence persistence) {
		super(persistence, new TypeBuilder<GoogleCredential<U>>() {
		});
	}

	@Override
	public GoogleCredential<U> findByCode(final String code) {
		return doInTransaction(new TransactionalJob<GoogleCredential<U>, GoogleCredential<U>>() {
			@Override
			public GoogleCredential<U> execute() {
				return find().where("code").equalTo(code).asSingle();
			}
		});
	}

}
