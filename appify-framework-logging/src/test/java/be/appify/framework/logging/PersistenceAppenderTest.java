package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceAppenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAppenderTest.class);
    private PersistenceAppender persistenceAppender;

    @Mock
    private Persistence persistence;

    @Before
    public void before() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        persistenceAppender = (PersistenceAppender) root.getAppender("PERSISTENCE");
        persistenceAppender.setPersistence(persistence);
    }

    @Test
    public void shouldSaveToPersistence() {
        LOGGER.debug("Some message with parameters {} and {}", "param1", "param2");

        Event expectedEvent = new Event("Some message with parameters param1 and param2", Event.Level.DEBUG,
                PersistenceAppenderTest.class.getName() + ".shouldSaveToPersistence(30)");
        Mockito.verify(persistence.beginTransaction()).save(expectedEvent);
    }
    
    
}
