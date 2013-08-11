package be.appify.framework.security.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import be.appify.framework.domain.AbstractEntity;
import be.appify.framework.domain.ReflectionBuilder;

import com.google.common.collect.Sets;

@Entity
@Table(name = "user")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASIC")
public class User extends AbstractEntity {

	private static final long serialVersionUID = 3232059538067455672L;
	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;
	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;
	@Column(name = "email_address", length = 100, nullable = false)
	private String emailAddress;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@ElementCollection(targetClass = Credential.class)
	private Set<Credential<? extends User>> credentials = Sets.newHashSet();

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setCredentials(Set<Credential<? extends User>> credentials) {
		this.credentials = Sets.newHashSet(credentials);
	}

	@SuppressWarnings("unchecked")
	public <U extends User> void addCredential(Credential<U> credential) {
		this.credentials.add(credential);
		credential.setUser((U) this);
	}

	public Set<Credential<? extends User>> getCredentials() {
		return Collections.unmodifiableSet(credentials);
	}

	public static ConcreteBuilder newBuilder() {
		return new ConcreteBuilder();
	}

	public static class ConcreteBuilder extends Builder<User, ConcreteBuilder> {

		public ConcreteBuilder() {
			super(User.class);
		}

	}

	public static class Builder<U extends User, B extends Builder<U, B>> extends ReflectionBuilder<U, B> {

		public Builder(Class<U> userType) {
			super(userType);
		}

		public B firstName(@NotNull @Size(min = 1, max = 50) String firstName) {
			return set("firstName", firstName);
		}

		public B lastName(@NotNull @Size(min = 1, max = 50) String lastName) {
			return set("lastName", lastName);
		}

		public B emailAddress(@NotNull @Size(min = 1, max = 100) String emailAddress) {
			return set("emailAddress", emailAddress);
		}

		public B credentials(@NotNull Set<Credential<?>> credentials) {
			return set("credentials", credentials);
		}

        @Override
        public U build() {
            U user = super.build();
            for(Credential<? extends User> credential : user.getCredentials()) {
                ((Credential<U>) credential).setUser(user);
            }
            return user;
        }
    }

	public <U extends User, C extends Credential<U>> C getCredential(Class<C> type) {
		for (Credential<?> credential : credentials) {
			if (type.isAssignableFrom(credential.getClass())) {
				return type.cast(credential);
			}
		}
		return null;
	}
}
