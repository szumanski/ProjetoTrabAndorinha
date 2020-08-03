package runner;

import org.apache.openejb.junit.jee.EJBContainerRunner;
import org.apache.openejb.junit.jee.statement.ShutingDownStatement;
import org.apache.openejb.junit.jee.statement.StartingStatement;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class TestRunner  extends EJBContainerRunner {

    private StartingStatement startingStatement;

    public TestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        startingStatement = new TestRunnerStartingDecorator(super.withBeforeClasses(statement), getTestClass().getJavaClass());
        return startingStatement;
    }

    @Override
    protected Statement withAfterClasses(final Statement statement) {
        return new ShutingDownStatement(super.withAfterClasses(statement), startingStatement);
    }
}
