package be.appify.framework.cache.simple;

import java.util.*;

public class LimitedMap<A, B> extends LinkedHashMap<A, B> {
	private static final long serialVersionUID = 3166698613291255237L;
	private final int maxEntries;

	public LimitedMap(final int maxEntries) {
		super(maxEntries + 1, 1.0f, true);
		this.maxEntries = maxEntries;
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
		return super.size() > maxEntries;
	}
}