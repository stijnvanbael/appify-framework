package be.appify.framework.security.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import be.appify.framework.domain.AbstractEntity;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Credential<U extends User> extends AbstractEntity {

	private static final long serialVersionUID = -1769445709075667254L;

	@ManyToOne(optional = false, targetEntity = User.class)
	@JoinColumn(name = "user_id")
	private U user;

	public U getUser() {
		return user;
	}

	public void setUser(U user) {
		this.user = user;
	}
}
