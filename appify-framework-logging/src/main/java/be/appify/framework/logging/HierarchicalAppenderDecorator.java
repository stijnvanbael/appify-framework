package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class HierarchicalAppenderDecorator extends DecoratingAppender {
    private Map<String, ThreadHistory> threads = Maps.newHashMap();
    private Level threshold = Level.WARN;
    private Queue<HierarchicalLoggingEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private CountDownLatch eventsAvailable = new CountDownLatch(1);
    private CountDownLatch flushed;

    public HierarchicalAppenderDecorator() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    boolean empty;
                    synchronized (HierarchicalAppenderDecorator.this) {
                        empty = eventQueue.isEmpty();
                    }
                    if(empty) {
                        try {
                            eventsAvailable.await();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    while(!empty) {
                        HierarchicalLoggingEvent event;
                        synchronized (HierarchicalAppenderDecorator.this) {
                            event = eventQueue.peek();
                        }
                        try {
                            String threadName = event.getThreadName();
                            ThreadHistory history = threads.get(threadName);
                            if(history == null) {
                                history = new ThreadHistory(threadName);
                                threads.put(threadName, history);
                            }
                            history.addEvent(event);
                            if(history.getMaxLevel().isGreaterOrEqual(threshold)) {
                                for(ILoggingEvent e : history.popUnloggedEvents()) {
                                    decoratedAppender.doAppend(e);
                                }
                            }
                        } catch(Throwable t) {
                            addError(t.getMessage(), t);
                        }
                        synchronized (HierarchicalAppenderDecorator.this) {
                            eventQueue.remove();
                            empty = eventQueue.isEmpty();
                            if(empty) {
                                if(flushed != null) {
                                    flushed.countDown();
                                }
                                eventsAvailable = new CountDownLatch(1);
                            }
                        }
                    }
                }
            }
        }, "Hierarchical appender").start();

    }

    @Override
    protected synchronized void append(ILoggingEvent event) {
        eventQueue.offer(new HierarchicalLoggingEvent(event));
        eventsAvailable.countDown();
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
    }

    public void flush() {
        synchronized (this) {
            if(eventQueue.isEmpty()) {
                return;
            }
            flushed = new CountDownLatch(1);
        }
        try {
            flushed.await();
        } catch (InterruptedException e) {
        }
    }
}
