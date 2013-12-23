package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class HierarchicalAppenderDecorator extends DecoratingAppender {
    private Map<String, ThreadHistory> threads = Maps.newConcurrentMap();
    private Level threshold = Level.WARN;
    private boolean renderHierarchy = true;
    private CountDownLatch eventsAvailable = new CountDownLatch(1);
    private CountDownLatch flushed;
    private CountDownLatch resumed;

    public HierarchicalAppenderDecorator() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    boolean done;
                    done = threads.isEmpty();
                    if(done) {
                        try {
                            eventsAvailable.await();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    if(resumed != null) {
                        try {
                            resumed.await();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    while(!done) {
                        if(!writeEvents()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                break;
                            }
                            if(!writeEvents()) {
                                done = true;
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

    public void setRenderHierarchy(boolean renderHierarchy) {
        this.renderHierarchy = renderHierarchy;
    }

    private boolean writeEvents() {
        boolean written = false;
        for(ThreadHistory history : threads.values()) {
            if(history.getMaxLevel().isGreaterOrEqual(threshold)) {
                for(ILoggingEvent e : history.popUnloggedEvents()) {
                    decoratedAppender.doAppend(e);
                }
                written = true;
            }
            synchronized (this) {
                if(history.isEmpty()) {
                    threads.remove(history.getThreadName());
                }
            }
        }
        return written;
    }

    @Override
    protected synchronized void append(ILoggingEvent event) {
        String threadName = event.getThreadName();
        ThreadHistory history = threads.get(threadName);
        if(history == null) {
            history = new ThreadHistory(threadName, threshold);
            threads.put(threadName, history);
        }
        HierarchicalLoggingEvent hierarchicalEvent = new HierarchicalLoggingEvent(event);
        hierarchicalEvent.setRenderHierarchy(renderHierarchy);
        history.addEvent(hierarchicalEvent);
        eventsAvailable.countDown();
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
    }

    public void flush() {
        synchronized (this) {
            if(threads.isEmpty()) {
                return;
            }
            flushed = new CountDownLatch(1);
        }
        try {
            flushed.await();
        } catch (InterruptedException e) {
        }
    }

    public void suspend() {
        resumed = new CountDownLatch(1);
    }

    public void resume() {
        resumed.countDown();
    }
}
