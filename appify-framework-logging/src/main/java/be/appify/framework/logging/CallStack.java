package be.appify.framework.logging;

import com.google.common.collect.Lists;

import java.util.List;

public class CallStack {
    private final StackTraceElement[] stack;

    public CallStack(StackTraceElement[] stack) {
        this.stack = stack;
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

    public boolean isParentOf(CallStack callStack) {
        return isParent(this.stack, callStack.stack);
    }

}
