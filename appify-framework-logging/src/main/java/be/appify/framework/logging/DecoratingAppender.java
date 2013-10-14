package be.appify.framework.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

import java.util.Iterator;

public abstract class DecoratingAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {

    private static final String NOT_IMPLEMENTED_EXPLANATION =
            "Not implemented; this class only implements AppenderAttachable to persuade joran to support the appender-ref xml element";

    protected Appender<ILoggingEvent> decoratedAppender;

    public Appender<ILoggingEvent> getDecoratedAppender() {
        return decoratedAppender;
    }

    public void setDecoratedAppender(final Appender<ILoggingEvent> decoratedAppender) {
        this.decoratedAppender = decoratedAppender;
    }

    @Override
    public void start() {
        if (this.decoratedAppender == null) {
            addError("No appender to decorate set for the appender named \"" + name + "\".");
        } else {
            super.start();
        }
    }

    @Override
    public void addAppender(final Appender<ILoggingEvent> newAppender) {
        setDecoratedAppender(newAppender);
    }

    // UNIMPLEMENTED METHODS
    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }

    @Override
    public Appender<ILoggingEvent> getAppender(String name) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }

    @Override
    public boolean isAttached(Appender<ILoggingEvent> iLoggingEventAppender) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }

    @Override
    public void detachAndStopAllAppenders() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }

    @Override
    public boolean detachAppender(Appender<ILoggingEvent> iLoggingEventAppender) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }

    @Override
    public boolean detachAppender(String name) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EXPLANATION);
    }
}