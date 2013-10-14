package be.appify.framework.logging;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchicalLoggingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalLoggingTest.class);

    @Test
    public void shouldLogHierarchy() {
        LOGGER.debug("Top level (1/2)");
        sublevel1_1();
        sublevel1_2();
        LOGGER.debug("Top level (2/2)");
    }

    private void sublevel1_1() {
        LOGGER.debug("Sublevel 1 (1/2)");
        sublevel2_1();
        sublevel2_2();
    }

    private void sublevel2_1() {
        LOGGER.debug("Sublevel 2 (1/2)");
    }

    private void sublevel2_2() {
        LOGGER.warn("Sublevel 2 (2/2)");
    }

    private void sublevel1_2() {
        LOGGER.debug("Sublevel 1 (2/2)");
    }
}
