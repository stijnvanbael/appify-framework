package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Lists;

import java.util.List;

public class ThreadHistory {

    private String threadName;
    private CallStack callStack;
    private List<HierarchicalLoggingEvent> unloggedEvents = Lists.newArrayList();
    private java.util.Stack<HierarchicalLoggingEvent> eventStack = new java.util.Stack();
    private Level maxLevel;

    public ThreadHistory(String threadName) {
        this.threadName = threadName;
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
        if(event.getHierarchyLevel() == 0) {
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
        List<HierarchicalLoggingEvent> events = Lists.newArrayList(unloggedEvents);
        unloggedEvents.clear();
        return events;
    }

    public Level getMaxLevel() {
        return maxLevel;
    }

}
