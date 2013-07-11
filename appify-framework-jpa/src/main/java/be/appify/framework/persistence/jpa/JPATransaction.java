package be.appify.framework.persistence.jpa;

import be.appify.framework.persistence.QueryBuilder;
import be.appify.framework.persistence.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


public class JPATransaction implements Transaction {

    private final EntityManager entityManager;
    private final EntityTransaction transaction;
    private final EntityManagerPool entityManagerPool;

    public JPATransaction(EntityManagerPool entityManagerPool) {
        entityManager = entityManagerPool.borrowEntityManager();
        transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        this.entityManagerPool = entityManagerPool;
    }

    @Override
    public void save(Object entity) {
        entityManager.merge(entity);
    }

    @Override
    public <T> QueryBuilder<T> find(Class<T> entityType) {
        return JPAQueryBuilder.find(entityType, entityManager);
    }

    @Override
    public QueryBuilder<Long> count(Class<?> entityType) {
        return JPAQueryBuilder.count(entityType, entityManager);
    }

    @Override
    public void delete(Object entity) {
        entity = entityManager.merge(entity);
        entityManager.remove(entity);
    }

    @Override
    public void commit() {
        transaction.commit();
        entityManagerPool.returnEntityManager(entityManager);
    }

    @Override
    public void rollback() {
        try {
            transaction.rollback();
        } finally {
            entityManagerPool.returnEntityManager(entityManager);
        }
    }

    @Override
    public boolean isActive() {
        return transaction.isActive();
    }

}
