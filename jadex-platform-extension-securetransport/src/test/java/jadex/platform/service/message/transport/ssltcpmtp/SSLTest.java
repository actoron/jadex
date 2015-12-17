package jadex.platform.service.message.transport.ssltcpmtp;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
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
		this(new File("."));
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public SSLTest(File cproot)	throws Exception
	{
		super(cproot, 
			// Exclude failing tests to allow maven build.
			new String[]
		{
//			"Provider",  // Shows junit assert method not found "filename" ???
		});
	}

	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new SSLTest();
	}
	
}
