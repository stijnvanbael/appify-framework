package be.appify.framework.persistence.appengine;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;

public class AlwaysSucceedHighRepJobPolicy implements HighRepJobPolicy {

	@Override
	public boolean shouldRollForwardExistingJob(Key entityGroup) {
		return true;
	}

	@Override
	public boolean shouldApplyNewJob(Key entityGroup) {
		return true;
	}

}
