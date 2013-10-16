package be.appify.framework.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// TODO: log async
// TODO: prevent other threads from cutting through a tree
public class HierarchicalLoggingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalLoggingTest.class);
    private Appender<ILoggingEvent> consoleAppender;

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        HierarchicalAppenderDecorator hierarchicalAppender = (HierarchicalAppenderDecorator) root.getAppender("STDOUT");
        consoleAppender = Mockito.spy(hierarchicalAppender.getDecoratedAppender());
        hierarchicalAppender.setDecoratedAppender(consoleAppender);
    }

    @Test
    public void shouldLogHierarchy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Other thread (1)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
                LOGGER.error("Other thread (2)");
            }
        }, "other-thread").start();
        LOGGER.debug("Top level (1)");
        sublevel1(1);
        sublevel1(2);
        sublevel1NoWarn(3);
        LOGGER.debug("Top level (2)");
        verify(consoleAppender).doAppend(log("<X> Other thread (2)"));
        verify(consoleAppender).doAppend(log("(.) Top level (1)"));
        verify(consoleAppender).doAppend(log(" | (.) Sublevel 1 (1)"));
        verify(consoleAppender, times(3)).doAppend(log(" |  | (.) Sublevel 2 (1)"));
        verify(consoleAppender, times(2)).doAppend(log(" |  | <!> Sublevel 2 (2)"));
        verify(consoleAppender).doAppend(log(" | (.) Sublevel 1 (2)"));
        verify(consoleAppender).doAppend(log(" | (.) Sublevel 1 (3)"));
        verify(consoleAppender).doAppend(log(" |  | (.) Sublevel 2 (2)"));
        verify(consoleAppender, never()).doAppend(log("(.) Top level (2)"));
    }

    private ILoggingEvent log(final String message) {
        return Mockito.argThat(new BaseMatcher<ILoggingEvent>() {
            @Override
            public boolean matches(Object o) {
                return message.equals(((ILoggingEvent) o).getFormattedMessage());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(message);
            }
        });
    }

    private void sublevel1(int count) {
        LOGGER.debug("Sublevel 1 (" + count + ")");
        sublevel2Debug(1);
        sublevel2Warn(2);
    }

    private void sublevel1NoWarn(int count) {
        LOGGER.debug("Sublevel 1 (" + count + ")");
        sublevel2Debug(1);
        sublevel2Debug(2);
    }

    private void sublevel2Debug(int count) {
        LOGGER.debug("Sublevel 2 (" + count + ")");
    }

    private void sublevel2Warn(int count) {
        LOGGER.warn("Sublevel 2 (" + count + ")");
    }
}
