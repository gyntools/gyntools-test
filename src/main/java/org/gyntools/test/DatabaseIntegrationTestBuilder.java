package org.gyntools.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rafael on 8/15/16.
 */
public class DatabaseIntegrationTestBuilder<T extends AbstractDatabaseIntegrationTestClass> {

    private DatabaseIntegrationTestBuilder builder;
    private T integrationClass;
    protected Logger logger = Logger.getLogger(this.getClass().getName());


    private DatabaseIntegrationTestBuilder(){

    }

    public static <T extends AbstractDatabaseIntegrationTestClass> DatabaseIntegrationTestBuilder of(T integration) {
        DatabaseIntegrationTestBuilder<T> builder  = new DatabaseIntegrationTestBuilder<T>();
        builder.builder = builder;
        builder.integrationClass =integration;
        return builder;
    }

    public DatabaseIntegrationTestBuilder withDB(String persistenceUnitName) {
        this.integrationClass.buildEntityManager(persistenceUnitName);
        return this;
    }

    public DatabaseIntegrationTestBuilder addSQLScriptFile(String path) {

        logger.info("Executing SQL file "+path);
        Session session = (Session) this.integrationClass.getEntityManager().getDelegate();
        Transaction tx = session.beginTransaction();
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

                session.doWork(new Work() {
                    public void execute(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sb.toString());
                        ps.executeBatch();
                        ps.close();
                    }
                });

                tx.commit();
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

    public DatabaseIntegrationTestBuilder addProperties(Properties props) {
        System.getProperties().entrySet().forEach(entry -> props.put(entry.getKey(),entry.getValue()));
        System.setProperties(props);
        return this;
    }


    public DatabaseIntegrationTestBuilder addProperties(String path) {
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
}
