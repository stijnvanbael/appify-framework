package be.appify.framework.cache;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class CacheKey<T> implements Serializable {
	private static final long serialVersionUID = 3837071875449248317L;
	private final Class<T> type;
	private final Collection<?> criteria;

	public CacheKey(Class<T> type, Object... criteria) {
		this.type = type;
		this.criteria = Lists.newArrayList(criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type, criteria);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || !(object instanceof CacheKey)) {
			return false;
		}
		CacheKey<?> other = (CacheKey<?>) object;
		return Objects.equal(this.type, other.type) && Objects.equal(this.criteria, other.criteria);
	}

	@Override
	public String toString() {
		return type.getName() + "#" + criteria;
	}

}
