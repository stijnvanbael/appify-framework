package be.appify.framework.common.security.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import be.appify.framework.security.domain.Credential;
import be.appify.framework.security.domain.User;

@Entity
@DiscriminatorValue("SIMPLE")
public class SimpleCredential<U extends User> extends Credential<U> {
	private static final long serialVersionUID = 6993995116413330380L;

	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "encrypted_password", length = 50)
	private String encryptedPassword;

	SimpleCredential() {
	}

	public SimpleCredential(String username, String encryptedPassword) {
		this.username = username;
		this.encryptedPassword = encryptedPassword;
	}

	public String getUsername() {
		return username;
	}

	public boolean checkPassword(SimpleCredential<U> credential) {
		return this.encryptedPassword.equals(credential.encryptedPassword);
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

}
