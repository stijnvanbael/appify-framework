package be.appify.framework.persistence.appengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.*;

import be.appify.framework.cache.appengine.AppEngineCache;
import be.appify.framework.persistence.*;
import be.appify.framework.persistence.appengine.AppEnginePersistence;

import com.google.appengine.tools.development.testing.*;

public class AppEnginePersistenceTest {
	private Persistence persistence;
	private LocalServiceTestHelper helper;
	private final LocalDatastoreServiceTestConfig testConfig = new LocalDatastoreServiceTestConfig();

	@After
	public void tearDown() {
		helper.tearDown();
	}

	private void setUpAlwaysInSyncHighReplication() {
		testConfig.setAlternateHighRepJobPolicyClass(AlwaysSucceedHighRepJobPolicy.class);
		setUpPersistence();
	}

	private void setUpHighReplication() {
		testConfig.setDefaultHighRepJobPolicyUnappliedJobPercentage(100);
		setUpPersistence();
	}

	private void setUpPersistence() {
		helper = new LocalServiceTestHelper(testConfig);
		helper.setUp();
		persistence = new AppEnginePersistence(new AppEngineCache());
	}

	@Test
	public void saveShouldStoreEntity() {
		setUpAlwaysInSyncHighReplication();
		FakeEntity entity = new FakeEntity();
		entity.setName("test1");
		entity.setValue(1);
		Transaction transaction = persistence.beginTransaction();
		transaction.save(entity);
		transaction.commit();

		transaction = persistence.beginTransaction();
		FakeEntity entityFound = transaction.find(FakeEntity.class)
				.where("name").equalTo("test1")
				.asSingle();
		transaction.rollback();
		assertNotNull(entityFound);
		assertEquals("test1", entityFound.getName());
		assertEquals(1, entityFound.getValue());
	}

	@Test
	public void deleteShouldRemoveEntity() {
		setUpAlwaysInSyncHighReplication();
		FakeEntity entity = new FakeEntity();
		entity.setName("test1");
		entity.setValue(1);
		Transaction transaction = persistence.beginTransaction();
		transaction.save(entity);
		transaction.commit();

		transaction = persistence.beginTransaction();
		transaction.delete(entity);
		transaction.commit();

		transaction = persistence.beginTransaction();
		FakeEntity entityFound = transaction.find(FakeEntity.class)
				.where("name").equalTo("test1")
				.asSingle();
		transaction.rollback();
		assertNull(entityFound);
	}

	@Test
	public void saveShouldStoreRelations() {
		setUpAlwaysInSyncHighReplication();
		FakeEntity parent = new FakeEntity();
		parent.setName("parent");
		parent.setValue(2);

		FakeEntity entity = new FakeEntity();
		entity.setName("test1");
		entity.setValue(1);
		entity.setParent(parent);
		Transaction transaction = persistence.beginTransaction();
		transaction.save(entity);
		transaction.commit();

		transaction = persistence.beginTransaction();
		FakeEntity entityFound = transaction.find(FakeEntity.class)
				.where("parent").equalTo(parent)
				.asSingle();
		transaction.rollback();
		assertNotNull(entityFound);
		assertNotNull(entityFound.getParent());
		assertEquals("parent", entityFound.getParent().getName());
	}

	@Test
	public void multiConditionQueryShouldOnlyReturnMatchingEntities() {
		setUpAlwaysInSyncHighReplication();
		Transaction transaction = persistence.beginTransaction();
		FakeEntity entity = new FakeEntity();
		entity.setName("test1");
		entity.setValue(1);
		entity.setActive(true);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test2");
		entity.setValue(1);
		entity.setActive(false);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test3");
		entity.setValue(2);
		entity.setActive(true);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test4");
		entity.setValue(3);
		entity.setActive(true);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test5");
		entity.setValue(1);
		entity.setActive(true);
		transaction.save(entity);
		transaction.commit();

		transaction = persistence.beginTransaction();
		List<FakeEntity> entitiesFound = transaction.find(FakeEntity.class)
				.where("value").equalTo(1)
				.and("active").equalTo(true)
				.asList();
		transaction.rollback();
		assertNotNull(entitiesFound);
		assertEquals(2, entitiesFound.size());
		for (FakeEntity e : entitiesFound) {
			assertTrue("test1".equals(e.getName()) || "test5".equals(e.getName()));
		}
	}

	@Test
	public void ancestorQueryShouldReturnConsistentResults() {
		setUpHighReplication();
		Transaction transaction = persistence.beginTransaction();
		FakeEntity parent = new FakeEntity();
		parent.setName("parent");
		parent.setValue(10);
		parent.setActive(true);
		transaction.save(parent);

		FakeEntity entity = new FakeEntity();
		entity.setName("test1");
		entity.setValue(11);
		entity.setActive(true);
		entity.setParent(parent);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test2");
		entity.setValue(12);
		entity.setActive(false);
		entity.setParent(parent);
		transaction.save(entity);

		entity = new FakeEntity();
		entity.setName("test3");
		entity.setValue(13);
		entity.setActive(true);
		entity.setParent(parent);
		transaction.save(entity);

		transaction.commit();

		transaction = persistence.beginTransaction();
		List<FakeEntity> entitiesFound = transaction.find(FakeEntity.class)
				.where("parent").equalTo(parent)
				.and("active").equalTo(true)
				.asList();
		transaction.rollback();
		assertNotNull(entitiesFound);
		assertEquals(2, entitiesFound.size());
		for (FakeEntity e : entitiesFound) {
			assertTrue("test1".equals(e.getName()) || "test3".equals(e.getName()));
		}

		transaction = persistence.beginTransaction();
		for (FakeEntity e : entitiesFound) {
			transaction.delete(e);
		}
		transaction.commit();

		transaction = persistence.beginTransaction();
		entitiesFound = transaction.find(FakeEntity.class)
				.where("parent").equalTo(parent)
				.and("active").equalTo(true)
				.asList();
		transaction.rollback();
		assertNotNull(entitiesFound);
		assertEquals(0, entitiesFound.size());
	}

}
