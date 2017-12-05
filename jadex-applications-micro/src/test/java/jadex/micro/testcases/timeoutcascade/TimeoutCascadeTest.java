package jadex.micro.testcases.timeoutcascade;

import jadex.base.test.impl.JunitAgentTest;

public class TimeoutCascadeTest extends JunitAgentTest {

    public TimeoutCascadeTest() {
        super(UserAgent.class.getName());
//        getConfig().setDefaultTimeout(60000);
    }

}
