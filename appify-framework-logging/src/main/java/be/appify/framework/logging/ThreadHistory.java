package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadHistory {

    private String threadName;
    private final Level threshold;
    private CallStack callStack;
    private Queue<HierarchicalLoggingEvent> unloggedEvents = Lists.newLinkedList();
    private java.util.Stack<HierarchicalLoggingEvent> eventStack = new java.util.Stack();
    private Level maxLevel = Level.ALL;

    public ThreadHistory(String threadName, Level threshold) {
        this.threadName = threadName;
        this.threshold = threshold;
    }

    public void addEvent(HierarchicalLoggingEvent event) {
        callStack = event.getCallStack();
        while(!eventStack.isEmpty()) {
            HierarchicalLoggingEvent e = eventStack.peek();
            if(e.getCallStack().isParentOf(callStack)) {
                e.addChild(event);
                break;
            }
            eventStack.pop();
        }
        eventStack.push(event);
        if(event.getHierarchyLevel() == 0 && !getMaxLevel().isGreaterOrEqual(threshold)) {
            popUnloggedEvents();
            maxLevel = Level.ALL;
        }
        unloggedEvents.add(event);
        if(event.getLevel().isGreaterOrEqual(maxLevel)) {
            maxLevel = event.getLevel();
        }
    }

    public String getThreadName() {
        return threadName;
    }

    public CallStack getCallStack() {
        return callStack;
    }

    public List<HierarchicalLoggingEvent> popUnloggedEvents() {
        List<HierarchicalLoggingEvent> events = Lists.newArrayList();
        boolean first = true;
        while(!unloggedEvents.isEmpty()) {
            if(first) {
                first = false;
                events.add(unloggedEvents.poll());
            } else if(unloggedEvents.peek().getHierarchyLevel() > 0) {
                events.add(unloggedEvents.poll());
            } else {
                determineMaxLevel();
                break;
            }
        }
        return events;
    }

    private void determineMaxLevel() {
        maxLevel = Level.ALL;
        boolean first = true;
        for(HierarchicalLoggingEvent event : unloggedEvents) {
            if(first) {
                first = false;
            } else if(event.getHierarchyLevel() == 0) {
                break;
            }
            if(event.getLevel().isGreaterOrEqual(maxLevel)) {
                maxLevel = event.getLevel();
            }
        }
    }

    public Level getMaxLevel() {
        return maxLevel;
    }

    public boolean isEmpty() {
        return unloggedEvents.isEmpty();
    }
}
