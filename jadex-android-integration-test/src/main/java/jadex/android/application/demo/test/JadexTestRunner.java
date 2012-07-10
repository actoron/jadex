package jadex.android.application.demo.test;

import junit.framework.TestSuite;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestRunner;

public class JadexTestRunner extends InstrumentationTestRunner
{

	public JadexTestRunner()
	{
		super();
	}

	@Override
	public TestSuite getAllTests()
	{
		TestSuite suite = new TestSuite();
		StartPlatformTest test = new StartPlatformTest();
		suite.addTest(test);
		return suite;
	}
	
	

}
