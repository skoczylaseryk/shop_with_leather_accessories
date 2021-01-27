package com.database.services.entityManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerService {
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shop-database");
    protected EntityManager entityManager = entityManagerFactory.createEntityManager();

    public void closeSession(){
        entityManager.close();
        entityManagerFactory.close();
    }
}
