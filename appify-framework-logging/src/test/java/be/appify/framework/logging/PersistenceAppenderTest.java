package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceAppenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAppenderTest.class);
    private PersistenceAppender persistenceAppender;

    @Mock
    private Persistence persistence;

    @Mock
    private Transaction transaction;

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        persistenceAppender = (PersistenceAppender) root.getAppender("PERSISTENCE");
        persistenceAppender.setPersistence(persistence);

        Mockito.when(persistence.beginTransaction()).thenReturn(transaction);
    }

    @Test
    public void shouldSaveToPersistence() {
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2");
        LOGGER.error("Disaster strikes!", new IllegalArgumentException("Disaster!"));

        Event expectedEvent1 = new Event.Builder()
                .message("Some message with parameters param1 and param2")
                .level(Event.Level.DEBUG)
                .className(PersistenceAppenderTest.class.getName())
                .methodName("shouldSaveToPersistence")
                .lineNumber(38)
                .threadName("main")
                .build();
        Event expectedEvent2 = new Event.Builder()
                .message("Disaster strikes!")
                .level(Event.Level.ERROR)
                .className(PersistenceAppenderTest.class.getName())
                .methodName("shouldSaveToPersistence")
                .lineNumber(39)
                .threadName("main")
                .build();
        Mockito.verify(transaction).save(expectedEvent1);
        Mockito.verify(transaction).save(expectedEvent2);
        Mockito.verify(transaction, times(2)).commit();
    }
    
    
}
