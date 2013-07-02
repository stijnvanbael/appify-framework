package be.appify.framework.job;

public interface JobQueue {
	void enqueue(Runnable job);

	void waitUntilEmpty();
}
