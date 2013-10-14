package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.Maps;

import java.util.Map;

public class HierarchicalAppenderDecorator extends DecoratingAppender {
    private Map<String, ThreadHistory> threads = Maps.newHashMap();
    private Level threshold = Level.WARN;

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            String threadName = eventObject.getThreadName();
            ThreadHistory history = threads.get(threadName);
            if(history == null) {
                history = new ThreadHistory(threadName);
                threads.put(threadName, history);
            }
            HierarchicalLoggingEvent event = new HierarchicalLoggingEvent(eventObject);
            history.addEvent(event);
            if(history.getMaxLevel().isGreaterOrEqual(threshold)) {
                for(ILoggingEvent e : history.popUnloggedEvents()) {
                    decoratedAppender.doAppend(e);
                }
            }
        } catch(Throwable t) {
            addError(t.getMessage(), t);
        }
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
    }
}
