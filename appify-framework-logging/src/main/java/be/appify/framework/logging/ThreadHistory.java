package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.CallerData;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ThreadHistory {

    private String threadName;
    private StackHierarchy hierarchy;
    private List<HierarchicalLoggingEvent> unloggedEvents = Lists.newArrayList();
    private Stack<HierarchicalLoggingEvent> eventStack = new Stack<>();
    private Level maxLevel;

    public ThreadHistory(String threadName) {
        this.threadName = threadName;
    }

    public void addEvent(HierarchicalLoggingEvent event) {
        StackTraceElement[] stack = CallerData.extract(new Throwable(), Logger.FQCN,
                Integer.MAX_VALUE, Collections.<String>emptyList());
        hierarchy = new StackHierarchy(stack, hierarchy);
        event.setHierarchy(hierarchy);
        while(!eventStack.isEmpty()) {
            HierarchicalLoggingEvent e = eventStack.peek();
            if(e.getHierarchy().isParentOf(hierarchy)) {
                e.addChild(event);
                break;
            }
            eventStack.pop();
        }
        eventStack.push(event);
        if(hierarchy.getLevel() == 0) {
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

    public StackHierarchy getHierarchy() {
        return hierarchy;
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
