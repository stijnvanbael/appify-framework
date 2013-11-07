package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.google.common.collect.Lists;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceAppenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAppenderTest.class);
    public static final String STACK_TRACE = "java\\.lang\\.RuntimeException: Error\\n" +
            "    at be\\.appify\\.framework\\.logging\\.PersistenceAppenderTest\\.shouldSaveToPersistence\\(PersistenceAppenderTest\\.java:%1$s\\)\\n" +
            "(.+\\n)+" +
            "Caused by: java\\.lang\\.IllegalArgumentException: Disaster!\\n" +
            "    at be\\.appify\\.framework\\.logging\\.PersistenceAppenderTest\\.createAnotherException\\(PersistenceAppenderTest\\.java:%2$s\\)\\n" +
            "    \\.\\.\\. \\d+ more\\n";

    @Mock
    private Persistence persistence;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Transaction transaction;

    @Mock
    private Appender<ILoggingEvent> fallbackAppender;

    private List<Integer> lineNumbers;
    private HierarchicalAppenderDecorator hierarchicalAppender;

    @Mock
    private StatusManager statusManager;

    @Before
    public void before() {
        lineNumbers = Lists.newArrayList();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = root.getLoggerContext();
        loggerContext.setStatusManager(statusManager);
        new PersistenceAppender.PersistenceProvider(persistence);
        hierarchicalAppender = (HierarchicalAppenderDecorator) root.getAppender("PERSISTENCE_HIERARCHICAL");
        hierarchicalAppender.setThreshold(Level.DEBUG);
        hierarchicalAppender.setRenderHierarchy(false);

        PersistenceAppender persistenceAppender = (PersistenceAppender) hierarchicalAppender.getDecoratedAppender();
        persistenceAppender.reset();
        persistenceAppender.setFallbackAppender(fallbackAppender);

        Mockito.when(fallbackAppender.getName()).thenReturn("MOCK");

        Mockito.when(persistence.beginTransaction()).thenReturn(transaction);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ILoggingEvent event = (ILoggingEvent) invocationOnMock.getArguments()[0];
                System.out.println(new Date(event.getTimeStamp()) + " " + event.getLevel() + " " + event.getFormattedMessage());
                return null;
            }
        }).when(fallbackAppender).doAppend(Mockito.any(ILoggingEvent.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Status status = (Status) invocationOnMock.getArguments()[0];
                System.err.println(new Date(status.getDate()) + " " + statusLevelToString(status.getLevel()) + " " + status.getMessage());
                if(status.getThrowable() != null) {
                    status.getThrowable().printStackTrace();
                }
                return null;
            }
        }).when(statusManager).add(Mockito.any(Status.class));
    }

    private String statusLevelToString(int level) {
        switch (level) {
            case Status.WARN:
                return "WARN";
            case Status.ERROR:
                return "ERROR";
        }
        return "INFO";
    }

    @Test
    public void shouldSaveToPersistence() throws InterruptedException {
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2"); recordLineNumber();
        LOGGER.error("Disaster strikes!", new RuntimeException("Error", createAnotherException())); recordLineNumber();

        Event expectedEvent1 = new Event.Builder()
                .message("Some message with parameters param1 and param2")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("shouldSaveToPersistence")
                .lineNumber(lineNumbers.get(0))
                .threadName("main")
                .timestamp(new Date())
                .build();
        final Event expectedEvent2 = new Event.Builder()
                .message("Disaster strikes!")
                .level(Event.Level.ERROR)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("shouldSaveToPersistence")
                .lineNumber(lineNumbers.get(2))
                .threadName("main")
                .timestamp(new Date())
                .build();
        hierarchicalAppender.flush();
        Mockito.verify(transaction).save(expectedEvent1);
        Mockito.verify(transaction).save(Mockito.argThat(new BaseMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                Event event = (Event) o;
                return expectedEvent2.equals(event) && event.getStackTrace().matches(String.format(STACK_TRACE, lineNumbers.get(2), lineNumbers.get(1)));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedEvent2.toString() + "\n" +
                        String.format(STACK_TRACE, lineNumbers.get(2), lineNumbers.get(1)));
            }
        }));
        Thread.sleep(300);
        Mockito.verify(transaction).commit();
    }

    @Test
    public void shouldFallBackWhenNoPersistenceAvailable() {
        new PersistenceAppender.PersistenceProvider(null);
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2"); recordLineNumber();
        hierarchicalAppender.flush();

        Mockito.verify(statusManager).add(logStatus("Persistence not set for appender PERSISTENCE, falling back to MOCK."));
        Mockito.verify(fallbackAppender).doAppend(logEvent("Some message with parameters param1 and param2"));
    }

    private Status logStatus(final String message) {
        return Mockito.argThat(new BaseMatcher<Status>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof Status && message.equals(((Status) o).getMessage());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(message);
            }
        });
    }

    private ILoggingEvent logEvent(final String message) {
        return Mockito.argThat(new BaseMatcher<ILoggingEvent>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ILoggingEvent && message.equals(((ILoggingEvent) o).getFormattedMessage());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(message);
            }
        });
    }

    @Test
    public void shouldFallBackWhenInsertThrowsException() throws InterruptedException {
        Mockito.doThrow(new RuntimeException("Dang!")).when(transaction).save(Mockito.any());
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2");
        LOGGER.debug("Another message");
        hierarchicalAppender.flush();

        Thread.sleep(300);
        Mockito.verify(statusManager).add(logStatus("Appender PERSISTENCE threw exception when logging an event, falling back to appender MOCK."));
        Mockito.verify(fallbackAppender).doAppend(logEvent("Some message with parameters param1 and param2"));
        Mockito.verify(fallbackAppender).doAppend(logEvent("Another message"));
    }

    @Test
    public void shouldFallBackWhenCommitThrowsException() throws InterruptedException {
        Mockito.doThrow(new RuntimeException("Dang!")).when(transaction).commit();
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2");
        LOGGER.debug("Another message");
        hierarchicalAppender.flush();

        Thread.sleep(300);
        Mockito.verify(statusManager).add(logStatus("Appender PERSISTENCE threw exception when logging an event, falling back to appender MOCK."));
        Mockito.verify(fallbackAppender).doAppend(logEvent("Some message with parameters param1 and param2"));
        Mockito.verify(fallbackAppender).doAppend(logEvent("Another message"));
    }

    @Test
    public void shouldSetParentForHierarchicalEvents() throws InterruptedException {
        hierarchicalAppender.suspend();
        LOGGER.debug("Top level (1)"); recordLineNumber();
        sublevel1(1);

        Event expectedEvent1 = new Event.Builder()
                .message("Top level (1)")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("shouldSetParentForHierarchicalEvents")
                .lineNumber(lineNumbers.get(0))
                .threadName("main")
                .timestamp(new Date())
                .build();

        final Event expectedEvent2 = new Event.Builder()
                .message("Sublevel 1 (1)")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("sublevel1")
                .lineNumber(lineNumbers.get(1))
                .threadName("main")
                .timestamp(new Date())
                .parent(expectedEvent1)
                .build();

        final Event expectedEvent3 = new Event.Builder()
                .message("Sublevel 2 (1)")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("sublevel2Debug")
                .lineNumber(lineNumbers.get(2))
                .threadName("main")
                .timestamp(new Date())
                .parent(expectedEvent2)
                .build();
        final Event expectedEvent4 = new Event.Builder()
                .message("Sublevel 2 (2)")
                .level(Event.Level.WARN)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("sublevel2Warn")
                .lineNumber(lineNumbers.get(3))
                .threadName("main")
                .timestamp(new Date())
                .parent(expectedEvent2)
                .build();


        Mockito.when(transaction.find(Event.class).where("id").equalTo(Mockito.anyString()).asSingle()).thenReturn(
                expectedEvent1, expectedEvent2, expectedEvent2);
        hierarchicalAppender.resume();
        hierarchicalAppender.flush();
        Thread.sleep(300);
        Mockito.verify(transaction).save(expectedEvent1);
        Mockito.verify(transaction).save(expectedEvent2);
        Mockito.verify(transaction).save(expectedEvent3);
        Mockito.verify(transaction).save(expectedEvent4);
    }

    private void recordLineNumber() {
        int lineNumber = new Exception().getStackTrace()[1].getLineNumber();
        lineNumbers.add(lineNumber);
    }

    private Throwable createAnotherException() {
        recordLineNumber(); return new IllegalArgumentException("Disaster!");
    }

    private void sublevel1(int count) {
        LOGGER.debug("Sublevel 1 (" + count + ")"); recordLineNumber();
        sublevel2Debug(1);
        sublevel2Warn(2);
    }

    private void sublevel2Debug(int count) {
        LOGGER.debug("Sublevel 2 (" + count + ")"); recordLineNumber();
    }

    private void sublevel2Warn(int count) {
        LOGGER.warn("Sublevel 2 (" + count + ")"); recordLineNumber();
    }

}
