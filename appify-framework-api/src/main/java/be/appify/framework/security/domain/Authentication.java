package be.appify.framework.security.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.joda.time.Instant;

import be.appify.framework.domain.AbstractEntity;

@Entity
public class Authentication<U extends User> extends AbstractEntity {

	private static final long serialVersionUID = -6829015132317104602L;

	@ManyToOne(optional = false, targetEntity = User.class)
	@JoinColumn(name = "user_id")
	private U user;

	@Column(name = "expires_on")
	private Date expiresOn;

	@SuppressWarnings("unused")
	private Authentication() {
	}

	public Authentication(U user, Instant expiresOn) {
		this.user = user;
		this.expiresOn = expiresOn.toDate();
	}

	public U getUser() {
		return user;
	}

	public boolean isExpired() {
		return Instant.now().isAfter(getExpiresOn());
	}

	public Instant getExpiresOn() {
		return new Instant(expiresOn);
	}
}
