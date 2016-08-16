package org.gyntools.test;

import org.testng.annotations.AfterClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rafael on 8/15/16.
 */
public abstract class AbstractDatabaseIntegrationTestClass {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    public EntityManager getEntityManager(){
        return entityManager;
    }

    void buildEntityManager(String persistenceUnitName){
        logger.info("Building EntityManager");
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
            entityManager = entityManagerFactory.createEntityManager();
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Erro ao inicializaro JPA",ex);
            throw new RuntimeException(ex);
        }
    }

    protected void closeJPA(){
        logger.info("Closing JPA");
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @AfterClass
    public void afterClass(){
        closeJPA();
    }

}
