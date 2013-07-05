package be.appify.framework.common.job;

import be.appify.framework.cache.Cache;
import be.appify.framework.cache.CacheKey;
import be.appify.framework.cache.simple.HashMapCache;
import be.appify.framework.job.JobQueue;
import be.appify.framework.job.JobQueueProvider;

public class ExecutorJobQueueProvider implements JobQueueProvider {
	private final Cache cache;

	public ExecutorJobQueueProvider() {
		cache = new HashMapCache();
	}

	@Override
	public JobQueue getJobQueue(String name) {
		CacheKey<JobQueue> cacheKey = new CacheKey<JobQueue>(JobQueue.class, name);
		JobQueue jobQueue = null;
		synchronized (cache) {
			jobQueue = cache.findSingle(cacheKey);
			if (jobQueue == null) {
				jobQueue = new ExecutorJobQueue(name);
				cache.put(cacheKey, jobQueue);
			}
		}
		return jobQueue;
	}

}