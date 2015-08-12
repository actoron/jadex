package jadex.platform.service.message.transport.ssltcpmtp;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;

/**
 *  Test suite for ssl agent tests.
 */
public class SSLTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public SSLTest()	throws Exception
	{
		// Use micro application classes directory as classpath root,
		super(new File("target/test-classes/"),
//		super(new File("../jadex-applications-micro/target/classes/jadex/micro/testcases/intermediate/InvokerAgent.class"),
			new File("target/test-classes"),
			// Exclude failing tests to allow maven build.
			new String[]
		{
//			"Provider",  // Shows junit assert method not found "filename" ???
//		});
		}, true, false, false);
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new SSLTest();
	}
	
}
