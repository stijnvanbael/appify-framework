package be.appify.framework.job;

public interface JobQueueProvider {
	JobQueue getJobQueue(String name);
}
