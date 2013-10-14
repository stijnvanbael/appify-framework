package be.appify.framework.logging;

import com.google.common.collect.Lists;

import java.util.List;

public class StackHierarchy {
    private final List<StackTraceElement[]> stacks;

    public StackHierarchy(StackTraceElement[] stack) {
        this(stack, null);
    }

    public StackHierarchy(StackTraceElement[] stack, StackHierarchy previousHierarchy) {
        this.stacks = Lists.newArrayList();
        if(previousHierarchy != null) {
            int level = determineLevel(stack, previousHierarchy);
            stacks.addAll(previousHierarchy.getStacks().subList(0, level));
        }
        stacks.add(stack);
    }

    private List<StackTraceElement[]> buildStacks(StackHierarchy previousHierarchy, int level) {
        return stacks;
    }

    private int determineLevel(StackTraceElement[] stack, StackHierarchy previousHierarchy) {
        List<StackTraceElement[]> previousHierarchyStacks = previousHierarchy.getStacks();
        for(int i = previousHierarchyStacks.size() - 1; i >=0; i--) {
            StackTraceElement[] s = previousHierarchyStacks.get(i);
            if(isParent(s, stack)) {
                return i + 1;
            }
        }
        return 0;
    }

    private boolean isParent(StackTraceElement[] parent, StackTraceElement[] stack) {
        List<StackTraceElement> parentStack = Lists.reverse(Lists.newArrayList(parent));
        List<StackTraceElement> newStack = Lists.reverse(Lists.newArrayList(stack));
        if(parent.length >= stack.length) {
            return false;
        }
        int i = 0;
        for(StackTraceElement element : parentStack) {
            if(!sameMethod(element, newStack.get(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean sameMethod(StackTraceElement element1, StackTraceElement element2) {
        return element1.getClassName().equals(element2.getClassName())
                && element1.getMethodName().equals(element2.getMethodName());
    }

    public List<StackTraceElement[]> getStacks() {
        return stacks;
    }

    public int getLevel() {
        return stacks.size() - 1;
    }

    public boolean isParentOf(StackHierarchy hierarchy) {
        return isParent(this.getTopStack(), hierarchy.getTopStack());
    }

    public StackTraceElement[] getTopStack() {
        return getStacks().get(this.getStacks().size() - 1);
    }
}
