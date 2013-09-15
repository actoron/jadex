package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.commons.SReflect;
import jadex.launch.test.MicroTest;
import junit.framework.TestSuite;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;

public class JadexInstrumentor extends InstrumentationTestRunner
{

	private static final String LOG_TAG = "JadexTestRunner";
	
    public static final String ARGUMENT_TEST_CLASS = "class";
    public static final String ARGUMENT_TEST_PACKAGE = "package";
    public static final String ARGUMENT_TEST_SIZE_PREDICATE = "size";
    public static final String ARGUMENT_INCLUDE_PERF = "perf";
    public static final String ARGUMENT_DELAY_MSEC = "delay_msec";


    static final String ARGUMENT_ANNOTATION = "annotation";
    static final String ARGUMENT_NOT_ANNOTATION = "notAnnotation";

	@Override
	public void onCreate(Bundle arguments)
	{
//		Log.i(LOG_TAG, "onCreate");
//		
//		Log.i(LOG_TAG, "isAndroid: " + SReflect.isAndroid());
		
		AndroidContextManager androidContext = AndroidContextManager.getInstance();
		androidContext.setAndroidContext(getContext());
		
		super.onCreate(arguments);
	}


	@Override
	public TestSuite getTestSuite()
	{
//		Log.i(LOG_TAG, "getTestSuite sleep");
//		
//		try
//		{
//			Thread.sleep(5000);
//		}
//		catch (InterruptedException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Log.i(LOG_TAG, "getTestSuite sleep end");
		
		Context targetContext = getTargetContext();
		
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), targetContext.getClassLoader());
//		testSuiteBuilder.includePackages("jadex.android.test");
		
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
			
			// Not working on android right now:
//			suite.addTest(new MicroTest("jadex.micro.testcases.stream.InitiatorAgent", getContext().getApplicationInfo().sourceDir));
			
			suite.addTest(new MicroTest("jadex.micro.testcases", getContext().getApplicationInfo().sourceDir));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return suite;
	}

	@Override
	public ClassLoader getLoader()
	{
		Log.i(LOG_TAG, " getLoader");
		ClassLoader loader = super.getLoader();
		Log.i(LOG_TAG, ": " + loader);
		return loader;
	}

	@Override
	protected AndroidTestRunner getAndroidTestRunner()
	{
		Log.i(LOG_TAG, " getAndroidTestRunner");
		// AndroidTestRunner androidTestRunner = super.getAndroidTestRunner();
		//
		// Log.i(TAG, ": " + androidTestRunner);
		AndroidTestRunner jadexTestRunner = new JadexTestRunner();
		return jadexTestRunner;
	}
}
