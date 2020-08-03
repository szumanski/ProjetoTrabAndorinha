package runner;


import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestRunnerOpenEjbConfiguration {

    private static Logger logger = LogManager.getRootLogger();


    public static java.util.Properties createInitialConfiguration() {
        java.util.Properties p = new java.util.Properties();
        addDefaultRules(p);
        loadLog4jProperties(p);
        loadTestingProperties(p);
        addIncludeRules(p);
        addExcludeRules(p);
        loadDatasources(p);
        return p;
    }

    private static void addDefaultRules(java.util.Properties p) {
        System.setProperty("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
        System.setProperty("tomee.jpa.cdi", "false");
    }

    private static void addIncludeRules(java.util.Properties p) {
        p.put("openejb.deployments.classpath.include", ".*/target/.*classes.*|.*openejb-core.*");
    }

    private static void addExcludeRules(java.util.Properties p) {
        p.put("openejb.deployments.classpath.exclude", ".*ejb-cdi-unit.*");
    }

    private static void loadDatasources(java.util.Properties p) {
        loadProperties("conf/datasources.properties", p);

        java.util.Properties newProperties = new java.util.Properties();

        logger.debug("Datasources carregados -> criando ALIAS JDNI");

        Set<Object> keys = p.keySet();
        for (Object o : keys) {
            String dsKey = o.toString();
            if (p.getProperty(dsKey).startsWith("new://Resource?type=DataSource")) {
                logger.debug(String.format("Registrando Alias de JNDI para [java:%s]", dsKey));
                p.setProperty(dsKey, p.getProperty(dsKey) + "&aliases=java:" + dsKey);

                boolean isOracle = p.getProperty(dsKey + ".JdbcDriver", "false").contains("Oracle");
                if (isOracle) {
                    String testQuery = p.getProperty(dsKey + ".validationQuery");
                    if (testQuery == null) {
                        logger.debug(String.format("Registrando validationQuery para [java:%s]", dsKey));

                        newProperties.setProperty(dsKey + ".validationQuery", "SELECT 1 FROM dual");
                    }
                }

                boolean isH2 = p.getProperty(dsKey + ".JdbcDriver", "false").equalsIgnoreCase("h2");
                if (isH2) {
                    newProperties.setProperty(dsKey + ".JdbcDriver", "org.h2.jdbcx.JdbcDataSource");
                    newProperties.setProperty(dsKey + ".userName", "sa");
                    newProperties.setProperty(dsKey + ".password", "");

                    String embeeded = p.getProperty(dsKey + ".embeeded");
                    if (embeeded != null) {
                        String dir = System.getProperty("user.dir") + File.separator + "target/database" + File.separator + embeeded;
                        logger.info("Local da base de dados: " + dir);
                        newProperties.setProperty(dsKey + ".JdbcUrl", "jdbc:h2:" + dir + ";DB_CLOSE_DELAY=-1;MODE=ORACLE");
                    } else {
                        newProperties.setProperty(dsKey + ".JdbcUrl", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE");
                    }
                }

                String schema = p.getProperty(dsKey + ".createSchema");
                if (schema != null) {
                    // override
                    newProperties.setProperty(dsKey + ".JdbcUrl",
                            "jdbc:h2:~/test;DB_CLOSE_DELAY=-1;MODE=ORACLE;INIT=CREATE SCHEMA IF NOT EXISTS " + schema);

                    String embeeded = p.getProperty(dsKey + ".embeeded");
                    if (embeeded != null) {
                        String dir = System.getProperty("user.dir") + File.separator + "target/database" + File.separator + embeeded;
                        logger.info("Local da base de dados: " + dir);
                        newProperties.setProperty(dsKey + ".JdbcUrl",
                                "jdbc:h2:" + dir + ";DB_CLOSE_DELAY=-1;MODE=ORACLE;INIT=CREATE SCHEMA IF NOT EXISTS " + schema);
                    } else {
                        newProperties.setProperty(dsKey + ".JdbcUrl",
                                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE;INIT=CREATE SCHEMA IF NOT EXISTS " + schema);
                    }
                }

            }
        }

        if (!newProperties.isEmpty()) {
            p.putAll(newProperties);
        }

    }

    private static void loadTestingProperties(java.util.Properties p) {
        loadProperties("conf/testing.properties", p);
    }

    private static void loadLog4jProperties(java.util.Properties p) {
        loadProperties("conf/log4j.properties", p);
        loadProperties("log4j.properties", p);
    }

    private static void loadProperties(String file, java.util.Properties p) {
        try (InputStream in = AndorinhaTestRunner.class.getClassLoader().getResourceAsStream(file)) {
            java.util.Properties ds = new java.util.Properties();
            ds.load(in);
            p.putAll(ds);
            logger.debug("propriedades carregadas -> [" + file + "] -> " + ds);
        } catch (Exception e) {
            logger.warn(String.format("Erro ao ler propriedades do arquivo [%s]: [%s]", file, e.getMessage()));
        }
    }
}
