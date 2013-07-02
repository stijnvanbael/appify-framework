package be.appify.framework.persistence.appengine;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

import be.appify.framework.cache.Cache;
import be.appify.framework.persistence.Persistence;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

public class AppEnginePersistence implements Persistence {
	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Cache cache;

	public AppEnginePersistence(Cache cache) {
		this.cache = cache;
	}

	@Override
	public be.appify.framework.persistence.Transaction beginTransaction() {
		Transaction transaction = datastore.beginTransaction(withXG(true));
		return new AppEngineTransaction(transaction, datastore, cache);
	}
}
