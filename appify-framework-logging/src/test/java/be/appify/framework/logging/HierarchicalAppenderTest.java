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

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HierarchicalAppenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalAppenderTest.class);
    private Appender<ILoggingEvent> consoleAppender;
    private HierarchicalAppenderDecorator hierarchicalAppender;
    private CountDownLatch otherThread1 = new CountDownLatch(1);
    private CountDownLatch otherThread2 = new CountDownLatch(1);
    private CountDownLatch mainThread1 = new CountDownLatch(1);

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        hierarchicalAppender = (HierarchicalAppenderDecorator) root.getAppender("CONSOLE_HIERARCHICAL");
        consoleAppender = Mockito.spy(hierarchicalAppender.getDecoratedAppender());
        hierarchicalAppender.setDecoratedAppender(consoleAppender);
    }

    @Test
    public void shouldLogHierarchy() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                otherThread1.countDown();
                try {
                    mainThread1.await();
                } catch (InterruptedException e) {
                }
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                LOGGER.error("Other thread");
                otherThread2.countDown();
            }
        }, "other-thread").start();

        otherThread1.await();
        LOGGER.debug("Top level (1)");
        sublevel1(1);
        mainThread1.countDown();
        otherThread2.await();
        sublevel1(2);
        sublevel1NoWarn(3);
        LOGGER.debug("Top level (2)");


        Thread.sleep(1000);
        hierarchicalAppender.flush();
        verify(consoleAppender, times(10)).doAppend(log("<X> Other thread"));
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
