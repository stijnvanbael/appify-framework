package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.mockito.Mockito.times;

// TODO: log hierarchical events
@RunWith(MockitoJUnitRunner.class)
public class PersistenceAppenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAppenderTest.class);
    public static final String STACK_TRACE = "java\\.lang\\.RuntimeException: Error\\n" +
            "    at be\\.appify\\.framework\\.logging\\.PersistenceAppenderTest\\.shouldSaveToPersistence\\(PersistenceAppenderTest\\.java:%1$s\\)\\n" +
            "(.+\\n)+" +
            "Caused by: java\\.lang\\.IllegalArgumentException: Disaster!\\n" +
            "    at be\\.appify\\.framework\\.logging\\.PersistenceAppenderTest\\.createAnotherException\\(PersistenceAppenderTest\\.java:%2$s\\)\\n" +
            "    \\.\\.\\. \\d+ more\\n";
    private PersistenceAppender persistenceAppender;

    @Mock
    private Persistence persistence;

    @Mock
    private Transaction transaction;
    private int lineNumber1;
    private int lineNumber2;
    private int lineNumber3;

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        new PersistenceAppender.PersistenceProvider(persistence);
        persistenceAppender = (PersistenceAppender) root.getAppender("PERSISTENCE");

        Mockito.when(persistence.beginTransaction()).thenReturn(transaction);
    }

    @Test
    public void shouldSaveToPersistence() {
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2"); lineNumber1 = recordLineNumber();
        LOGGER.error("Disaster strikes!", new RuntimeException("Error", createAnotherException()));  lineNumber2 = recordLineNumber();

        Event expectedEvent1 = new Event.Builder()
                .message("Some message with parameters param1 and param2")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("shouldSaveToPersistence")
                .lineNumber(lineNumber1)
                .threadName("main")
                .timestamp(new Date())
                .build();
        final Event expectedEvent2 = new Event.Builder()
                .message("Disaster strikes!")
                .level(Event.Level.ERROR)
                .className(PersistenceAppenderTest.class.getName())
                .fileName("PersistenceAppenderTest.java")
                .methodName("shouldSaveToPersistence")
                .lineNumber(lineNumber2)
                .threadName("main")
                .timestamp(new Date())
                .build();
        Mockito.verify(transaction).save(expectedEvent1);
        Mockito.verify(transaction).save(Mockito.argThat(new BaseMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                Event event = (Event) o;
                return expectedEvent2.equals(event) && event.getStackTrace().matches(String.format(STACK_TRACE, lineNumber2, lineNumber3));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedEvent2.toString() + "\n" +
                        String.format(STACK_TRACE, lineNumber2, lineNumber3));
            }
        }));
        Mockito.verify(transaction, times(2)).commit();
    }

    private int recordLineNumber() {
        return new Exception().getStackTrace()[1].getLineNumber();
    }

    private Throwable createAnotherException() {
        lineNumber3 = recordLineNumber(); return new IllegalArgumentException("Disaster!");
    }


}
