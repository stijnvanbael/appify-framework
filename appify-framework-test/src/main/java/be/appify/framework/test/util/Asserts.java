package be.appify.framework.test.util;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

public final class Asserts {
	private Asserts() {
	}

	public static void assertContains(Collection<?> collection, Object element) {
		assertTrue("Expected " + collection + " to contain " + element, collection.contains(element));
	}
}
