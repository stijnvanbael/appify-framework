package be.appify.framework.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.*;

import com.google.common.base.Objects;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
	private static final long serialVersionUID = -7988234508873033480L;
	@Id
	@Column(length = 100)
	private String id;

	public AbstractEntity() {
		id = UUID.randomUUID().toString();
	}

	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	@Override
	public final int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		AbstractEntity other = (AbstractEntity) obj;
		return Objects.equal(this.id, other.id);
	}
}
