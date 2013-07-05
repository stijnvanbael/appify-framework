package be.appify.framework.persistence.appengine;

import javax.persistence.*;

import be.appify.framework.persistence.annotation.Ancestor;

@Entity
public class FakeEntity {
	@Id
	private String name;
	private int value;
	private boolean active;

	@Ancestor
	@ManyToOne(cascade = CascadeType.MERGE)
	private FakeEntity parent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public FakeEntity getParent() {
		return parent;
	}

	public void setParent(FakeEntity parent) {
		this.parent = parent;
	}

}
