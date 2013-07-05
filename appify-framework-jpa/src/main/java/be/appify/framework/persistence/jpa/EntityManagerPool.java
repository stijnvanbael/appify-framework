package be.appify.framework.persistence.jpa;

import com.google.common.collect.Sets;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

class EntityManagerPool {
    private Set<EntityManager> availableEntityManagers = Sets.newHashSet();
    private Set<EntityManager> entityManagersInUse = Sets.newHashSet();
    private Set<CountDownLatch> waitingLatches = Sets.newHashSet();
    private final EntityManagerFactory entityManagerFactory;
    private final int poolSize;

    public EntityManagerPool(EntityManagerFactory entityManagerFactory, int poolSize) {
        this.entityManagerFactory = entityManagerFactory;
        this.poolSize = poolSize;
    }

    public EntityManager borrowEntityManager() {
        EntityManager entityManager;
        synchronized (availableEntityManagers) {
            entityManager = borrowEntityManagerFromPool();
        }
        if (entityManager == null) {
            CountDownLatch waitingLatch = new CountDownLatch(1);
            synchronized (waitingLatches) {
                waitingLatches.add(waitingLatch);
            }
            try {
                waitingLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return borrowEntityManager();
        }
        return entityManager;
    }

    public void returnEntityManager(EntityManager entityManager) {
        synchronized (availableEntityManagers) {
            if (entityManagersInUse.contains(entityManager)) {
                entityManagersInUse.remove(entityManager);
                availableEntityManagers.add(entityManager);
            }
        }
        synchronized (waitingLatches) {
            if(!waitingLatches.isEmpty()) {
                waitingLatches.iterator().next().countDown();
            }
        }
    }

    private EntityManager borrowEntityManagerFromPool() {
        EntityManager entityManager = null;
        if (!availableEntityManagers.isEmpty()) {
            entityManager = availableEntityManagers.iterator().next();
            availableEntityManagers.remove(entityManager);
            entityManagersInUse.add(entityManager);
        } else if (availableEntityManagers.size() + entityManagersInUse.size() < poolSize) {
            entityManager = entityManagerFactory.createEntityManager();
            entityManagersInUse.add(entityManager);
        }
        return entityManager;
    }
}
