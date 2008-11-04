package jadex.rules.test;

import jadex.rules.test.rulesystem.BetaTest2;
import jadex.rules.test.rulesystem.CollectNodeTest;
import jadex.rules.test.rulesystem.EqualJoinTest;
import jadex.rules.test.rulesystem.FunctionTest;
import jadex.rules.test.rulesystem.JavaConditionTest;
import jadex.rules.test.rulesystem.MethodCallTest;
import jadex.rules.test.rulesystem.MethodCallTest2;
import jadex.rules.test.rulesystem.MultifieldSplitTest;
import jadex.rules.test.rulesystem.NotNodeInitialfactTest;
import jadex.rules.test.rulesystem.NotNodeJoinTest;
import jadex.rules.test.rulesystem.TestNodeTest;
import jadex.rules.test.state.CreateDropTest;
import jadex.rules.test.state.MultiAttributeTest;
import jadex.rules.test.state.ReferenceManagementTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *  JUnit test suite for all rules tests.
 */
public class MicrokernelTestSuite
{
	/**
	 *  Run a test suite.  
	 *  @return The test.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite("TestSuite for jadex.rules");
		//$JUnit-BEGIN$
		suite.addTestSuite(CreateDropTest.class);
		suite.addTestSuite(MultiAttributeTest.class);
		suite.addTestSuite(ReferenceManagementTest.class);
		
		suite.addTestSuite(MethodCallTest.class);
		suite.addTestSuite(MethodCallTest2.class);
		suite.addTestSuite(NotNodeJoinTest.class);
		suite.addTestSuite(NotNodeInitialfactTest.class);
		suite.addTestSuite(EqualJoinTest.class);
		suite.addTestSuite(FunctionTest.class);
		suite.addTestSuite(MultifieldSplitTest.class);
		suite.addTestSuite(TestNodeTest.class);
		suite.addTestSuite(JavaConditionTest.class);
//		suite.addTestSuite(JavaMultifieldTest.class);
		suite.addTestSuite(CollectNodeTest.class);
		suite.addTestSuite(BetaTest2.class);
		//$JUnit-END$
		return suite;
	}

}
