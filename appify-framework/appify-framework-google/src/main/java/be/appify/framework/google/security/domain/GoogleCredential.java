package be.appify.framework.google.security.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import be.appify.framework.security.domain.Credential;
import be.appify.framework.security.domain.User;

@Entity
@DiscriminatorValue("GOOGLE")
public class GoogleCredential<U extends User> extends Credential<U> {
	private static final long serialVersionUID = 3713460948397104915L;

	@Column(length = 50, nullable = false)
	private final String code;

	public GoogleCredential(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
