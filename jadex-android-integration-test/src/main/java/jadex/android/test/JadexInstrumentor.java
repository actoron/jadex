package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.commons.SReflect;
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
		Log.i(LOG_TAG, "onCreate");
		
		Log.i(LOG_TAG, "isAndroid: " + SReflect.isAndroid());
		
		Context context = getContext();
		AndroidContextManager androidContext = AndroidContextManager.getInstance();
		androidContext.setAndroidContext(getContext());
		
//		ClassLoader classLoader = getTargetContext().getClassLoader();
//		try
//		{
//			Class<?> clazz = classLoader.loadClass("jadex.android.AndroidContextManager");
//			clazz.
//		}
//		catch (ClassNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		if (arguments != null)
//		{
//			// Test class name passed as an argument should override any
//			// meta-data declaration.
//			String testClassesArg = arguments.getString(ARGUMENT_TEST_CLASS);
//			boolean mDebug = getBooleanArgument(arguments, "debug");
//			boolean mJustCount = getBooleanArgument(arguments, "count");
//			boolean mSuiteAssignmentMode = getBooleanArgument(arguments, "suiteAssignment");
//			String mPackageOfTests = arguments.getString(ARGUMENT_TEST_PACKAGE);
//
//			boolean includePerformance = getBooleanArgument(arguments, ARGUMENT_INCLUDE_PERF);
//			boolean logOnly = getBooleanArgument(arguments, ARGUMENT_LOG_ONLY);
//			boolean mCoverage = getBooleanArgument(arguments, "coverage");
//			String mCoverageFilePath = arguments.getString("coverageFile");
//
//			
//			Log.i(LOG_TAG, "debug: " + mDebug);
//			Log.i(LOG_TAG, "mJustCount: " + mJustCount);
//			Log.i(LOG_TAG, "mSuiteAssignmentMode: " + mSuiteAssignmentMode);
//			Log.i(LOG_TAG, "mPackageOfTests: " + mPackageOfTests);
//		}
		super.onCreate(arguments);
	}

	@Override
	public void onStart()
	{
		Log.i(LOG_TAG, " onStart");

		super.onStart();
	}

	@Override
	public TestSuite getAllTests()
	{
		Log.i(LOG_TAG, " getAllTests");
		return super.getAllTests();
	}

	@Override
	public TestSuite getTestSuite()
	{
		Log.i(LOG_TAG, "getTestSuite sleep");
		
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.i(LOG_TAG, "getTestSuite sleep end");
		
		Context targetContext = getTargetContext();
		
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), targetContext.getClassLoader());
//		testSuiteBuilder.includePackages("jadex.android.test");
		
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
			suite.addTest(new MicroTest("jadex.micro.testcases", getContext().getApplicationInfo().sourceDir));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		suite.addTest(new TestSuite() {
//
//			@Override
//			public void run(TestResult result)
//			{
//				try
//				{
//					MicroTest.suite().run(result);
//				}
//				catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void addTest(Test test)
//			{
//				// TODO Auto-generated method stub
//				super.addTest(test);
//			}
//
//			@Override
//			public void addTestSuite(Class testClass)
//			{
//				// TODO Auto-generated method stub
//				super.addTestSuite(testClass);
//			}
//
//			@Override
//			public int countTestCases()
//			{
//				Log.i(LOG_TAG, "countTestCases");
//				return super.countTestCases();
//			}
//
//			@Override
//			public String getName()
//			{
//				// TODO Auto-generated method stub
//				return super.getName();
//			}
//
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				// TODO Auto-generated method stub
//				super.runTest(test, result);
//			}
//
//			@Override
//			public void setName(String name)
//			{
//				// TODO Auto-generated method stub
//				super.setName(name);
//			}
//
//			@Override
//			public Test testAt(int index)
//			{
//				// TODO Auto-generated method stub
//				Log.i(LOG_TAG, "testAt");
//				return super.testAt(index);
//			}
//
//			@Override
//			public int testCount()
//			{
//				// TODO Auto-generated method stub
//				Log.i(LOG_TAG, "testCount()");
//				return super.testCount();
//			}
//
//			@Override
//			public Enumeration tests()
//			{
//				// TODO Auto-generated method stub
//				Log.i(LOG_TAG, "tests()");
//				return super.tests();
//			}
//
//		});
		
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

	// @Override
	// public TestSuite getAllTests()
	// {
	// TestSuite suite = new TestSuite();
	// // MicroCreationTest test = new MicroCreationTest();
	// // suite.addTest(test);
	// return suite;
	//
	// }
	//
	// @Override
	// protected AndroidTestRunner getAndroidTestRunner()
	// {
	// return super.getAndroidTestRunner();
	// }
	
	private boolean getBooleanArgument(Bundle arguments, String tag) {
        String tagString = arguments.getString(tag);
        return tagString != null && Boolean.parseBoolean(tagString);
    }
	



}
