package be.appify.framework.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import com.google.common.collect.Lists;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HierarchicalLoggingEvent implements ILoggingEvent {
    private String id;
	private ILoggingEvent event;
	private CallStack callStack;
	private HierarchicalLoggingEvent parent;
	private List<HierarchicalLoggingEvent> children = Lists.newArrayList();
	private boolean renderHierarchy;

	public HierarchicalLoggingEvent(ILoggingEvent event) {
        this.id = UUID.randomUUID().toString();
        this.event = event;

		// Initialize thread name on the correct thread
		event.getThreadName();
		// Initialize caller data on the correct thread
		event.getCallerData();

		StackTraceElement[] stack = CallerData.extract(new Throwable(), Logger.FQCN, Integer.MAX_VALUE, Collections.<String> emptyList());
		callStack = new CallStack(stack);
	}

	public void setRenderHierarchy(boolean renderHierarchy) {
		this.renderHierarchy = renderHierarchy;
	}

    public String getId() {
        return id;
    }

    @Override
	public String getThreadName() {
		return event.getThreadName();
	}

	@Override
	public Level getLevel() {
		return event.getLevel();
	}

	@Override
	public String getMessage() {
		return event.getMessage();
	}

	@Override
	public Object[] getArgumentArray() {
		return event.getArgumentArray();
	}

	@Override
	public String getFormattedMessage() {
		if (renderHierarchy) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < getHierarchyLevel(); i++) {
				builder.append(" | ");
			}
			builder.append(levelString())
					.append(" ")
					.append(event.getFormattedMessage());
			return builder.toString();
		}
        return event.getFormattedMessage();
	}

	private String levelString() {
		switch (getLevel().toInt()) {
		case Level.ERROR_INT:
			return "<X>";
		case Level.WARN_INT:
			return "<!>";
		}
		return "(.)";
	}

	@Override
	public String getLoggerName() {
		return event.getLoggerName();
	}

	@Override
	public LoggerContextVO getLoggerContextVO() {
		return event.getLoggerContextVO();
	}

	@Override
	public IThrowableProxy getThrowableProxy() {
		return event.getThrowableProxy();
	}

	@Override
	public StackTraceElement[] getCallerData() {
		return event.getCallerData();
	}

	@Override
	public boolean hasCallerData() {
		return event.hasCallerData();
	}

	@Override
	public Marker getMarker() {
		return event.getMarker();
	}

	@Override
	public Map<String, String> getMDCPropertyMap() {
		return event.getMDCPropertyMap();
	}

	@Override
	public Map<String, String> getMdc() {
		return event.getMdc();
	}

	@Override
	public long getTimeStamp() {
		return event.getTimeStamp();
	}

	@Override
	public void prepareForDeferredProcessing() {
		event.prepareForDeferredProcessing();
	}

	public CallStack getCallStack() {
		return callStack;
	}

	public void setParent(HierarchicalLoggingEvent parent) {
		this.parent = parent;
	}

	public HierarchicalLoggingEvent getParent() {
		return parent;
	}

	public void addChild(HierarchicalLoggingEvent event) {
		event.setParent(this);
		children.add(event);
	}

	@Override
	public String toString() {
		return getFormattedMessage();
	}

	public int getHierarchyLevel() {
		return parent != null ? parent.getHierarchyLevel() + 1 : 0;
	}
}
