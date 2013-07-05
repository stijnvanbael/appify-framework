package be.appify.framework.google.security.repository;

import be.appify.framework.google.security.domain.GoogleCredential;
import be.appify.framework.security.domain.User;

public interface GoogleCredentialRepository<U extends User> {
	GoogleCredential<U> findByCode(String code);
}
