package com.github.gyntools.test;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rafael on 8/15/16.
 */
public class DatabaseIntegrationTesterBuilder<T extends AbstractDatabaseIntegrationTester> {

    private DatabaseIntegrationTesterBuilder builder;
    private T integrationClass;
    protected Logger logger = Logger.getLogger(this.getClass().getName());


    private DatabaseIntegrationTesterBuilder(){
        Logger.getLogger("org.hibernate.level").setLevel(Level.FINEST);
        Logger.getLogger("org.hibernate.type").setLevel(Level.FINEST);
    }

    public static <T extends AbstractDatabaseIntegrationTester> DatabaseIntegrationTesterBuilder of(T integration) {
        DatabaseIntegrationTesterBuilder<T> builder  = new DatabaseIntegrationTesterBuilder<T>();
        builder.builder = builder;
        builder.integrationClass =integration;
        return builder;
    }

    public DatabaseIntegrationTesterBuilder withDB(String persistenceUnitName) {
        this.integrationClass.buildEntityManager(persistenceUnitName);
        return this;
    }

    public DatabaseIntegrationTesterBuilder addSQLScriptFile(String path) {

        logger.info("Executing SQL file "+path);
        Session session = newSession();
        logger.info(session.toString());
        session.setFlushMode(FlushMode.COMMIT);
        logger.fine("Transaction status before open:"+session.getTransaction().getStatus());

        Transaction tx = session.beginTransaction();
        logger.fine("Transaction status on open:"+session.getTransaction().getStatus());

        try {

            if(path!=null && path.length()>0){

                URL resource = this.getClass().getClassLoader().getResource(path);
                logger.info(resource.getFile());

                BufferedReader in = new BufferedReader(new FileReader(resource.getFile()));
                String str;
                final StringBuffer sb = new StringBuffer();
                while ((str = in.readLine()) != null) {
                    sb.append(str + "\n ");
                }

                session.createSQLQuery(sb.toString()).executeUpdate();
                tx.commit();

                logger.fine("Transaction status after execution:"+session.getTransaction().getStatus());
                in.close();
            }else{
                tx.rollback();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE,"ERROR on executing script file "+path,e);
            tx.rollback();
            throw new RuntimeException(e);
        }
        return this;
    }

    public DatabaseIntegrationTesterBuilder addProperties(Properties props) {
        System.getProperties().entrySet().forEach(entry -> props.put(entry.getKey(),entry.getValue()));
        System.setProperties(props);
        return this;
    }


    public DatabaseIntegrationTesterBuilder addProperties(String path) {
        try {
            URL resource = this.getClass().getClassLoader().getResource(path);
            Properties props = new Properties();
            props.load(new FileReader(resource.getFile()));
            addProperties(props);
        }catch (Exception e){
            logger.log(Level.SEVERE,"ERROR on executing loding file "+path,e);
        }
        return this;
    }

    public Session newSession(){
        Session session = this.integrationClass.getEntityManager().unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        return sessionFactory.openSession();
    }
}
