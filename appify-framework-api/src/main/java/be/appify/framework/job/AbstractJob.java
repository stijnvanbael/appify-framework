package be.appify.framework.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJob implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJob.class);

	protected final void tryTimes(Runnable runnable, String description, int times) {
		tryTimes(runnable, description, times, 1);
	}

	private final void tryTimes(Runnable runnable, String description, int times, int attempt) {
		try {
			runnable.run();
		} catch (Exception e) {
			if (attempt < times) {
				LOGGER.warn("Failed to run <" + description + "> (" + e.getMessage() + "), retrying (" + attempt + ")");
				tryTimes(runnable, description, times, attempt + 1);
			} else {
				LOGGER.error("Failed to run <" + description + "> (" + e.getMessage() + "), giving up", e);
			}
		}
	}
}
