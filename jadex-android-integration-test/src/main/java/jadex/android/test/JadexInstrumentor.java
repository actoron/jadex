package jadex.android.test;

import jadex.launch.test.BDITest;
import jadex.launch.test.MicroTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
		ClassLoader classLoader = getTargetContext().getClassLoader();
		Class<?> loadClass;
		try
		{
			loadClass = classLoader.loadClass("jadex.android.AndroidContextManager");
			Method getInstance = loadClass.getMethod("getInstance");
			Method setAndroidContext = loadClass.getMethod("setAndroidContext", Context.class);
			
			Object contextManager = getInstance.invoke(null);
			setAndroidContext.invoke(contextManager, getTargetContext());
		}

		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		AndroidContextManager androidContext = AndroidContextManager.getInstance();
//		androidContext.setAndroidContext(getContext());
		
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
		
		String sourceDir = targetContext.getApplicationInfo().sourceDir;
		
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), targetContext.getClassLoader());
//		testSuiteBuilder.includePackages("jadex.android.test");
		
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
			
			// Not working on android right now:
//			suite.addTest(new MicroTest("jadex.micro.testcases.stream.InitiatorAgent", sourceDir));
//			suite.addTest(new MicroTest("jadex.micro.testcases.nfmethodprop", sourceDir));
			
			suite.addTest(new MicroTest("jadex.micro.testcases", sourceDir));
//			suite.addTest(new BDITest("jadex.bdi.testcases", sourceDir));
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
