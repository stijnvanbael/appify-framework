package be.appify.framework.logging;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.ConsoleAppender;
import com.google.common.collect.Lists;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PersistenceAppender extends AppenderBase<ILoggingEvent> {
    private static final long TRANSACTION_COMMIT_DELAY = 250;
    private Appender<ILoggingEvent> fallbackAppender;
    private ConsoleAppender<ILoggingEvent> systemErrorAppender = new ConsoleAppender<>();
    private AtomicBoolean fallbackReported = new AtomicBoolean(false);
    private Timer transactionTimer;
    private Transaction currentTransaction;
    private List<ILoggingEvent> uncommittedEvents = Lists.newArrayList();

    public PersistenceAppender() {
        systemErrorAppender.setTarget("System.err");
    }

    public void reset() {
        fallbackReported.set(false);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        Persistence persistence = PersistenceProvider.getPersistence();
        if (persistence != null) {
            try {
                synchronized (this) {
                    uncommittedEvents.add(eventObject);
                }
                requestTransaction(persistence);
                Event event = createEvent(eventObject);
                currentTransaction.save(event);
                endTransaction();
            } catch (Throwable t) {
                if(transactionTimer != null) {
                    transactionTimer.cancel();
                }
                fallbackAppendUncommittedEvents(t);
            }
        } else {
            if(!fallbackReported.getAndSet(true)) {
                reportFallback();
            }
            fallbackAppend(eventObject);
        }
    }

    private void fallbackAppend(ILoggingEvent eventObject) {
        if(fallbackAppender != null) {
            fallbackAppender.doAppend(eventObject);
        } else {
            systemErrorAppender.doAppend(eventObject);
        }
    }

    private void reportFallback(Throwable t) {
        if(fallbackAppender != null) {
            addError("Appender " + getName() + " threw exception when logging an event, falling back to appender " + fallbackAppender.getName() + ".", t);
        } else {
            addError("Appender " + getName() + " threw exception when logging an event and no fallback appender is defined, falling back to System.err.", t);
        }
    }

    private synchronized void endTransaction() {
        if(transactionTimer != null) {
            transactionTimer.cancel();
        }
        transactionTimer = new Timer();
        transactionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (PersistenceAppender.this) {
                    try {
                        currentTransaction.commit();
                        uncommittedEvents.clear();
                    } catch (Throwable t) {
                        fallbackAppendUncommittedEvents(t);
                    } finally {
                        currentTransaction = null;
                        transactionTimer = null;
                    }
                }
            }
        }, TRANSACTION_COMMIT_DELAY);
    }

    private synchronized void fallbackAppendUncommittedEvents(Throwable t) {
        try {
            currentTransaction.rollback();
        } catch (Throwable t2) {
            addError("Failed to rollback transaction." , t2);
        }
        currentTransaction = null;
        if(!fallbackReported.getAndSet(true)) {
            reportFallback(t);
        }
        for(ILoggingEvent event : uncommittedEvents) {
            fallbackAppend(event);
        }
        uncommittedEvents.clear();
    }

    private synchronized void requestTransaction(Persistence persistence) {
        if(currentTransaction == null) {
            currentTransaction = persistence.beginTransaction();
        }
    }

    private void reportFallback() {
        if(fallbackAppender != null) {
            addWarn("Persistence not set for appender " + getName() + ", falling back to " + fallbackAppender.getName() + ".");
        } else {
            addWarn("Persistence not set for appender " + getName() + " and no fallback appender is defined, falling back to System.err.");
        }
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
        Event parent = findParent(eventObject);
        String id = idOf(eventObject);
        return new Event.Builder()
                .id(id)
                .message(message)
                .level(level)
                .className(className)
                .fileName(fileName)
                .methodName(methodName)
                .lineNumber(lineNumber)
                .threadName(threadName)
                .timestamp(timestamp)
                .stackTrace(stackTrace)
                .parent(parent)
                .build();
    }

    private String idOf(ILoggingEvent eventObject) {
        if(!(eventObject instanceof HierarchicalLoggingEvent)) {
            return UUID.randomUUID().toString();
        }
        return ((HierarchicalLoggingEvent) eventObject).getId();
    }

    private Event findParent(ILoggingEvent eventObject) {
        if(!(eventObject instanceof HierarchicalLoggingEvent)) {
            return null;
        }
        HierarchicalLoggingEvent hierarchicalLoggingEvent = (HierarchicalLoggingEvent) eventObject;
        HierarchicalLoggingEvent parent = hierarchicalLoggingEvent.getParent();
        if(parent == null) {
            return null;
        }
        return currentTransaction.find(Event.class).where("id").equalTo(parent.getId()).asSingle();
    }

    private String buildStackTrace(IThrowableProxy throwableProxy) {
        if (throwableProxy == null) {
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
        for (StackTraceElementProxy stackTraceElement : throwableProxy.getStackTraceElementProxyArray()) {
            if (parentStackTrace.length > parentIndex && stackTraceElement.equals(parentStackTrace[parentIndex])) {
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
        if (parentIndex > 0) {
            sb.append("    ... ")
                    .append(parentIndex)
                    .append(" more\n");
        }
        if (throwableProxy.getCause() != null) {
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

    public void setFallbackAppender(Appender<ILoggingEvent> fallbackAppender) {
        this.fallbackAppender = fallbackAppender;
    }

    @ApplicationScoped
    public static final class PersistenceProvider {
        private static Persistence persistence;

        @Inject
        public PersistenceProvider(Persistence persistence) {
            PersistenceProvider.persistence = persistence;
        }

        static Persistence getPersistence() {
            return persistence;
        }
    }

}
