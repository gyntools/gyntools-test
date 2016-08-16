package it.org.gyntools.test;

import org.gyntools.test.DatabaseIntegrationTestBuilder;
import org.gyntools.test.AbstractDatabaseIntegrationTestClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by rafael on 8/15/16.
 */

@Test
public class DatabaseIntegrationTestBuilderIT extends AbstractDatabaseIntegrationTestClass {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    protected DatabaseIntegrationTestBuilder databaseIntegrationTestBuilder = DatabaseIntegrationTestBuilder.of(this);
    protected String persistenceUnitName="integration-tests-pu";

    @Test(groups={"with-db"},singleThreaded = true)
    public class OnBuildWithDB extends DatabaseIntegrationTestBuilderIT {

        public void shouldBuildWithDB(){
            databaseIntegrationTestBuilder.withDB(persistenceUnitName);
            assertNotNull(this.getEntityManager());
            logger.info(this.getEntityManager().toString());
        }

        public void shouldBuildWithDBAndScriptSQLFile(){
            databaseIntegrationTestBuilder.addSQLScriptFile("import.sql");
        }

    }

    @Test(groups={"with-system-properties"})
    public class OnBuildWithSystemProperties extends DatabaseIntegrationTestBuilderIT {

        public void shouldAddFromProperties(){
            Properties props = new Properties();
            props.put("tested","ok");
            databaseIntegrationTestBuilder.addProperties(props);
            String value = System.getProperty("tested");
            assertNotNull(value);
            logger.info("Value taken from System Properties: "+value);
        }

        public void shouldAddFromPropertiesFile(){
            String fileName="test.properties";
            databaseIntegrationTestBuilder.addProperties(fileName);
            String value = System.getProperty("this.is.another.property.value");
            assertNotNull(value);
            logger.info("Value taken from System Properties: "+value);
        }

    }

}
