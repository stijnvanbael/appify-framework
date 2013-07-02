package be.appify.framework.persistence.jpa;

import be.appify.framework.persistence.Persistence;
import be.appify.framework.persistence.Transaction;

import javax.persistence.EntityManagerFactory;

public class JPAPersistence implements Persistence {

    private final EntityManagerPool entityManagerPool;

    public JPAPersistence(EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory, 1);
    }

    public JPAPersistence(EntityManagerFactory entityManagerFactory, int poolSize) {
        this.entityManagerPool = new EntityManagerPool(entityManagerFactory, poolSize);
    }

    @Override
    public Transaction beginTransaction() {

        return new JPATransaction(entityManagerPool);
    }

}
