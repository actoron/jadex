package jadex.platform.service;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;

/**
 *  Run all component tests in test folder.
 */
public class ManagementExtensionTest extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public ManagementExtensionTest() throws Exception
	{
		super(new File("target/test-classes"), new File("target/test-classes"),
			new String[]
			{
				"ManualUser"	// extends user to allow manual testing with gui.
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new ManagementExtensionTest();
	}
}
