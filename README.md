#gyntools-test


##Description
Setup tests made easy


##Usage

### Integration Tests

1) Create a new integration tests class extending [AbstractDatabaseIntegrationTester](src/main/java/com/github/gyntools/test/AbstractDatabaseIntegrationTester.java)

Example: 

            @Test
            public class DatabaseIntegrationTesterBuilderIT extends AbstractDatabaseIntegrationTester {
                //ommited
            }
            
2) Use the builder
 
 Example: 
 
            @Test
            public void shouldBuildWithDB(){
                DatabaseIntegrationTesterBuilder helper = DatabaseIntegrationTesterBuilder.of(this).withDB(persistenceUnitName);
                assertNotNull(this.getEntityManager());
                logger.info(this.getEntityManager().toString());
            }
            
### Unit tests

Usage:
Create a new unit tests class extending [AbstractBaseUnitTest](src/main/java/com/github/gyntools/test/AbstractBaseUnitTest.java)

