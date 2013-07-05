package be.appify.framework.persistence.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.junit.*;

import be.appify.framework.persistence.*;
import be.appify.framework.persistence.Persistence;

public class JPAPersistenceTest {
	private Persistence persistence;

	@Before
	public void before() {
		EntityManagerFactory entityManagerFactory = javax.persistence.Persistence.createEntityManagerFactory("appify");
		persistence = new JPAPersistence(entityManagerFactory);

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FakeEntity> query = criteriaBuilder.createQuery(FakeEntity.class);
		Root<FakeEntity> root = query.from(FakeEntity.class);
		query.select(root);
		List<FakeEntity> entities = entityManager.createQuery(query).getResultList();
		for (FakeEntity entity : entities) {
			entityManager.remove(entity);
		}
		transaction.commit();
	}

	@Test
	public void saveShouldStoreEntity() {
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
	public void saveShouldStoreRelations() {
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
}
