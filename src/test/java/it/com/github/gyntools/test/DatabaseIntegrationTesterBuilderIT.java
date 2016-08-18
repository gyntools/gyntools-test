package it.com.github.gyntools.test;

import com.github.gyntools.test.DatabaseIntegrationTesterBuilder;
import com.github.gyntools.test.AbstractDatabaseIntegrationTester;
import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by rafael on 8/15/16.
 */

@Test
public class DatabaseIntegrationTesterBuilderIT extends AbstractDatabaseIntegrationTester {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    protected DatabaseIntegrationTesterBuilder databaseIntegrationTesterBuilder = DatabaseIntegrationTesterBuilder.of(this);
    protected String persistenceUnitName="integration-tests-pu";

    @Test(groups={"with-db"},singleThreaded = true)
    public class OnBuildWithDB extends DatabaseIntegrationTesterBuilderIT {

        public void shouldBuildWithDB(){
            databaseIntegrationTesterBuilder.withDB(persistenceUnitName);
            assertNotNull(this.getEntityManager());
            logger.info(this.getEntityManager().toString());
        }

        public void shouldBuildWithDBAndScriptSQLFile(){
            databaseIntegrationTesterBuilder.addSQLScriptFile("sql/import1.sql");
        }

        public void shouldBuildWithDBAndAnotherScriptSQLFile(){
            databaseIntegrationTesterBuilder.addSQLScriptFile("sql/import2.sql");
        }

        public void shouldGetlCountOfPersonsRight(){
            Session session = getEntityManager().unwrap(Session.class).getSessionFactory().openSession();
            int size = session.createQuery("select p from Person p").list().size();
            logger.info(String.format("GOT %s Persons", size ));
            assertEquals(size,7);
        }

    }

    @Test(groups={"with-system-properties"})
    public class OnBuildWithSystemProperties extends DatabaseIntegrationTesterBuilderIT {

        public void shouldAddFromProperties(){
            Properties props = new Properties();
            props.put("tested","ok");
            databaseIntegrationTesterBuilder.addProperties(props);
            String value = System.getProperty("tested");
            assertNotNull(value);
            logger.info("Value taken from System Properties: "+value);
        }

        public void shouldAddFromPropertiesFile(){
            String fileName="test.properties";
            databaseIntegrationTesterBuilder.addProperties(fileName);
            String value = System.getProperty("this.is.another.property.value");
            assertNotNull(value);
            logger.info("Value taken from System Properties: "+value);
        }

    }

}
