package be.appify.framework.logging;

import java.util.Date;
import java.util.Objects;

public class Event {
    private String message;
    private Level level;
    private String className;
    private String methodName;
    private int lineNumber;
    private String threadName;
    private final Date timestamp;

    public Event(String message, Level level, String className, String methodName, int lineNumber, String threadName,
                 Date timestamp) {
        this.message = message;
        this.level = level;
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.threadName = threadName;
        this.timestamp = timestamp;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event other = (Event) o;

        return Objects.equals(this.message, other.message)
                && Objects.equals(this.level, other.level)
                && Objects.equals(this.className, other.className)
                && Objects.equals(this.methodName, other.methodName)
                && Objects.equals(this.lineNumber, other.lineNumber)
                && Objects.equals(this.threadName, other.threadName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, level, className, methodName, lineNumber, threadName);
    }

    @Override
    public String toString() {
        return "[" + threadName + "] " + level + " " + className + "." + methodName + "(" + lineNumber + "): " + message;
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
            return new Event(message, level, className, methodName, lineNumber, threadName, timestamp);
        }
    }
}
