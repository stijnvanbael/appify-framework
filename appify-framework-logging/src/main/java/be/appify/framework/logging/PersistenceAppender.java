package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
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
        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        String fileName = element.getFileName();
        String stackTrace = buildStackTrace(throwableProxy);
        return new Event.Builder()
                .message(message)
                .level(level)
                .className(className)
                .fileName(fileName)
                .methodName(methodName)
                .lineNumber(lineNumber)
                .threadName(threadName)
                .timestamp(timestamp)
                .stackTrace(stackTrace)
                .build();
    }

    private String buildStackTrace(IThrowableProxy throwableProxy) {
        if(throwableProxy == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appendStackTrace(throwableProxy, sb, new StackTraceElementProxy[0]);
        return sb.toString();
    }

    private void appendStackTrace(IThrowableProxy throwableProxy, StringBuilder sb, StackTraceElementProxy[] parentStackTrace) {
        sb.append(throwableProxy.getClassName())
                .append(": ")
                .append(throwableProxy.getMessage())
                .append("\n");
        int parentIndex = 0;
        for(StackTraceElementProxy stackTraceElement : throwableProxy.getStackTraceElementProxyArray()) {
            if(parentStackTrace.length > parentIndex && stackTraceElement.equals(parentStackTrace[parentIndex])) {
                parentIndex++;
            } else {
                StackTraceElement element = stackTraceElement.getStackTraceElement();
                sb.append("    at ")
                        .append(element.getClassName())
                        .append(".")
                        .append(element.getMethodName())
                        .append("(")
                        .append(element.getFileName())
                        .append(":")
                        .append(element.getLineNumber())
                        .append(")\n");
            }
        }
        if(parentIndex > 0) {
            sb.append("    ... ")
                    .append(parentIndex)
                    .append(" more\n");
        }
        if(throwableProxy.getCause() != null) {
            sb.append("Caused by: ");
            appendStackTrace(throwableProxy.getCause(), sb, throwableProxy.getStackTraceElementProxyArray());
        }
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
