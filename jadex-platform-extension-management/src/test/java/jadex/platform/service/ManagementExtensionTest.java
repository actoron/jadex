package jadex.platform.service;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SUtil;
import junit.framework.Test;

/**
 *  Run all component tests in test folder.
 */
public class ManagementExtensionTest extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public ManagementExtensionTest()	throws Exception
	{
		this(SUtil.findBuildDir(new File("."), true));
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public ManagementExtensionTest(File cproot)	throws Exception
	{
		super(cproot, cproot,
			// Exclude failing tests to allow maven build.
			new String[]
		{
			"ManualUser",	// extends user to allow manual testing with gui.
			"TestSubprocessStartEvent",	// part of test and sometimes produces exception when started alone.
			"TestIntermediateEvent"	// part of test and sometimes produces exception when started alone.
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
