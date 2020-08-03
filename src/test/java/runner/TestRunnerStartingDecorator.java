package runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.junit.jee.config.Properties;
import org.apache.openejb.junit.jee.config.Property;
import org.apache.openejb.junit.jee.config.PropertyFile;
import org.apache.openejb.junit.jee.statement.StartingStatement;
import org.apache.openejb.util.Classes;
import org.junit.runners.model.Statement;

public class TestRunnerStartingDecorator extends StartingStatement {

    private final Class<?> clazz;
    private java.util.Properties properties;
    private EJBContainer container;

    public TestRunnerStartingDecorator (Statement statement, Class<?> clazz) {
        super(statement, clazz);
        this.clazz = clazz;
    }

    @Override
    protected void before() throws Exception {

        //aqui, delega pra a classe de inicio do OpenEJB a criação das propriedades de execução
        properties = TestRunnerOpenEjbConfiguration.createInitialConfiguration();

        { // set caller first to let it be overridable by @Property
            final StringBuilder b = new StringBuilder();
            for (final Class<?> c : Classes.ancestors(clazz)) {
                if (c != Object.class) {
                    b.append(c.getName()).append(",");
                }
            }
            b.setLength(b.length() - 1);
            properties.put(OpenEjbContainer.Provider.OPENEJB_ADDITIONNAL_CALLERS_KEY, b.toString());
        }

        // default implicit config
        {
            final InputStream is = clazz.getClassLoader().getResourceAsStream("openejb-junit.properties");
            if (is != null) {
                properties.load(is);
            }
        }

        final PropertyFile propertyFile = clazz.getAnnotation(PropertyFile.class);
        if (propertyFile != null) {
            final String path = propertyFile.value();
            if (!path.isEmpty()) {
                InputStream is = clazz.getClassLoader().getResourceAsStream(path);
                if (is == null) {
                    final File file = new File(path);
                    if (file.exists()) {
                        is = new FileInputStream(file);
                    } else {
                        throw new OpenEJBException("properties resource '" + path + "' not found");
                    }
                }

                properties.load(is);
            }
        }

        final Properties annotationConfig = clazz.getAnnotation(Properties.class);
        if (annotationConfig != null) {
            for (final Property property : annotationConfig.value()) {
                properties.put(property.key(), property.value());
            }
        }

        if (!properties.containsKey(Context.INITIAL_CONTEXT_FACTORY)) {
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
        }

        container = EJBContainer.createEJBContainer(properties);
    }

    public java.util.Properties getProperties() {
        return properties;
    }

    public EJBContainer getContainer() {
        return container;
    }

}
