package be.appify.framework.common.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import be.appify.framework.job.JobQueue;

public class ExecutorJobQueue implements JobQueue {

	private static final int TIMEOUT = 20000;
	private final ExecutorService executor;

	public ExecutorJobQueue(String name) {
		this.executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void enqueue(Runnable job) {
		executor.execute(job);
	}

	@Override
	public void waitUntilEmpty() {
		try {
			executor.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}