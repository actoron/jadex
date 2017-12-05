package jadex.micro.testcases;

import jadex.micro.testcases.semiautomatic.nfproperties.ServiceSearchAgent;
import jadex.micro.testcases.semiautomatic.servicevalue.NewsConsumerAgent;
import jadex.micro.testcases.semiautomatic.servicevalue.NewsProviderAgent;
import jadex.base.test.impl.GenericTestSuite;

/**
 * Starts all those components that are in testcases package, but are not real tests.
 * Maybe sometime change them to real tests?
 */
public class StartComponentSuite extends GenericTestSuite {

    public StartComponentSuite() {
        super(true,
                ServiceSearchAgent.class,
                NewsConsumerAgent.class,
                NewsProviderAgent.class
                );
    }

    public static StartComponentSuite suite() {
        return new StartComponentSuite();
    }
}
