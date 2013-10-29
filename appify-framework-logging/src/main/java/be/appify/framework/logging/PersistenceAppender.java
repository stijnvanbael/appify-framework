package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.Date;

public class PersistenceAppender extends AppenderBase<ILoggingEvent> {
    private Persistence persistence;

    @Override
    protected void append(ILoggingEvent eventObject) {
        Transaction transaction = persistence.beginTransaction();
        Event event = createEvent(eventObject);
        transaction.save(event);
        transaction.commit();
    }

    private Event createEvent(ILoggingEvent eventObject) {
        String message = eventObject.getFormattedMessage();
        Event.Level level = translate(eventObject.getLevel());
        StackTraceElement element = eventObject.getCallerData()[0];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        String threadName = eventObject.getThreadName();
        Date timestamp = new Date(eventObject.getTimeStamp());
        eventObject.getThrowableProxy();
        return new Event.Builder()
                .message(message)
                .level(level)
                .className(className)
                .methodName(methodName)
                .lineNumber(lineNumber)
                .threadName(threadName)
                .timestamp(timestamp)
                .build();
    }

    private Event.Level translate(Level level) {
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return Event.Level.ERROR;
            case Level.WARN_INT:
                return Event.Level.WARN;
            case Level.INFO_INT:
                return Event.Level.INFO;
            case Level.DEBUG_INT:
                return Event.Level.DEBUG;
        }
        return Event.Level.TRACE;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

}
