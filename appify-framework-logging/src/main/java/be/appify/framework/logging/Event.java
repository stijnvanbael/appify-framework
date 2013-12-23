package be.appify.framework.logging;

import java.util.Date;
import java.util.Objects;

public class Event {
    private String id;
    private String message;
    private Level level;
    private String className;
    private String fileName;
    private String methodName;
    private int lineNumber;
    private String threadName;
    private final Date timestamp;
    private String stackTrace;
    private Event parent;

    private Event(String id, String message, Level level, String className, String fileName, String methodName, int lineNumber, String threadName,
                  Date timestamp, String stackTrace, Event parent) {
        this.message = message;
        this.level = level;
        this.className = className;
        this.fileName = fileName;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.threadName = threadName;
        this.timestamp = timestamp;
        this.stackTrace = stackTrace;
        this.parent = parent;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getThreadName() {
        return threadName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getFileName() {
        return fileName;
    }

    public Event getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event other = (Event) o;

        return Objects.equals(this.message, other.message)
                && Objects.equals(this.level, other.level)
                && Objects.equals(this.className, other.className)
                && Objects.equals(this.fileName, other.fileName)
                && Objects.equals(this.methodName, other.methodName)
                && Objects.equals(this.lineNumber, other.lineNumber)
                && Objects.equals(this.threadName, other.threadName)
                && Objects.equals(this.parent, other.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, level, className, fileName, methodName, lineNumber, threadName, parent);
    }

    @Override
    public String toString() {
        return timestamp + " [" + threadName + "] " + level + " " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ") - " 
                + message + (stackTrace != null ? "\n" + stackTrace : ""
                + (parent != null ? " (parent: " + parent.getMessage() + ")" : ""));
    }

    public static enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    public static class Builder {
        private String message;
        private Event.Level level;
        private String className;
        private String methodName;
        private int lineNumber;
        private String threadName;
        private Date timestamp;
        private String stackTrace;
        private String fileName;
        private Event parent;
        private String id;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder level(Event.Level level) {
            this.level = level;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }


        public Builder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Event build() {
            return new Event(id, message, level, className, fileName, methodName, lineNumber, threadName, timestamp, stackTrace, parent);
        }

        public Builder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder parent(Event parent) {
            this.parent = parent;
            return this;
        }
    }
}
