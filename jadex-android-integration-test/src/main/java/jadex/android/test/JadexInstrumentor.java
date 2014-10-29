package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.android.commons.Logger;
import jadex.android.service.JadexPlatformManager;
import jadex.base.test.impl.BrokenComponentTest;
import jadex.bridge.ErrorReport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.PathClassLoader;
import dalvik.system.VMRuntime;
import junit.framework.Test;
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
	
	private ClassLoader jadexCl;

	private String	sourceDir;

	private String testClassesArg;

	private boolean logOnly;

	private static final String ARGUMENT_TEST_CLASS = "class";
    private static final String ARGUMENT_LOG_ONLY = "log";
	
	@Override
	public void onCreate(Bundle arguments)
	{
		Log.i(LOG_TAG, "JadexInstrumentor created...");
		
		jadexCl = this.getClass().getClassLoader();
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		long maxHeapKilos = Runtime.getRuntime().maxMemory() / 1024;
		Log.i(LOG_TAG, "Got " + maxHeapKilos + "kB max allowed heap size.");
		if (maxHeapKilos < 32768) {
			Log.i(LOG_TAG, "Max Heap size smaller than 32MB. Trying to increase minimum heapsize...");
			VMRuntime.getRuntime().setMinimumHeapSize(32*1024*1024);
			maxHeapKilos = Runtime.getRuntime().maxMemory() / 1024;
			if (maxHeapKilos < 32768) {
				Log.e(LOG_TAG, "Couldn't increase Heap size."
						+ "Test execution will likely fail with a max heapsize of less than 32MB!");
			}
		}
		
		Context targetContext = getTargetContext();
		sourceDir = targetContext.getApplicationInfo().sourceDir;

		
		// Set Context:
		AndroidContextManager.getInstance().setAndroidContext(targetContext);
		// Set Classloader for app:
		JadexPlatformManager.getInstance().setAppClassLoader(sourceDir, jadexCl);
		
		String testClassesArg = arguments.getString(ARGUMENT_TEST_CLASS);
		if (testClassesArg != null) {
			// super.onCreate would now try to instanciate a single TestCase, but without passing the needed arguments.
			// so we handle this in getTestSuite instead and clear the argument here.
			this.testClassesArg = testClassesArg;
			arguments.remove(ARGUMENT_TEST_CLASS);
		}
		
		logOnly = getBooleanArgument(arguments, ARGUMENT_LOG_ONLY);
		
		super.onCreate(arguments);
	}
	
    private boolean getBooleanArgument(Bundle arguments, String tag) {
        String tagString = arguments.getString(tag);
        return tagString != null && Boolean.parseBoolean(tagString);
    }
	
	@Override
	public TestSuite getTestSuite()
	{
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), jadexCl);
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
//			Thread.sleep(10000);
			
			// To execute a single test:
//			Test singleTest = createTest("jadex.launch.test.MicroTest", "jadex.micro.testcases.stream.InitiatorAgent", sourceDir);
//			suite.addTest(singleTest);
			
			// Make sure that also the dependencies are placed in pom.
			Log.i(LOG_TAG, "JadexInstrumentor creating JUnit ComponentTest classes...");
			
			if (testClassesArg != null) {
				Test requestedTest = createTest(testClassesArg, sourceDir);
				suite.addTest(requestedTest);
			} else {
				Test microTest = createTest("jadex.launch.test.MicroTest", sourceDir);
				Test bdiTest = createTest("jadex.launch.test.BDITest", sourceDir);
				Test bpmnTest = createTest("jadex.launch.test.BPMNTest", sourceDir);
				Test bdibpmnTest = createTest("jadex.launch.test.BDIBPMNTest", sourceDir);
				Test gpmnTest = createTest("jadex.launch.test.GPMNTest", sourceDir);
				Test bdiv3Test = createTest("jadex.launch.test.BDIV3Test", sourceDir);
	
				suite.addTest(microTest);
				suite.addTest(bdiTest);
				suite.addTest(bpmnTest);
				suite.addTest(bdibpmnTest);
				suite.addTest(gpmnTest);
				suite.addTest(bdiv3Test);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ErrorReport errorReport = new ErrorReport();
			errorReport.setErrorText(e.getMessage());
			BrokenComponentTest error = new BrokenComponentTest("creation", errorReport);
			suite.addTest(error);
		}
		
//		if (suite.countTestCases() < 10) {
//			ErrorReport errorReport = new ErrorReport();
//			errorReport.setErrorText("Less than 10 Testcases found - Problem with loading them?");
//			BrokenComponentTest error = new BrokenComponentTest("creation", errorReport);
//			suite.addTest(error);
//		}
		
		return suite;
	}


	private Test createTest(String testClassName, String classRoot) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
//		PathClassLoader newCl = new PathClassLoader("./", jadexCl);
//		Class<?> test = newCl.loadClass(testClassName);
		Class<?> test= jadexCl.loadClass(testClassName);
		Constructor<?> constructor = test.getConstructor(String.class);
		Object testCase = null;
		try {
			testCase = constructor.newInstance(classRoot);
		} catch (Throwable t) {
			t.printStackTrace();
			ErrorReport errorReport = new ErrorReport();
			errorReport.setErrorText(t.getMessage());
			BrokenComponentTest error = new BrokenComponentTest(testClassName, errorReport);
			testCase = error;
		}
		return (Test) testCase;
	}

	@Override
	public ClassLoader getLoader()
	{
		ClassLoader loader = super.getLoader();
		Log.i(LOG_TAG, ": " + loader);
		return loader;
	}

	@Override
	protected AndroidTestRunner getAndroidTestRunner()
	{
		AndroidTestRunner jadexTestRunner = new JadexTestRunner(logOnly);
		return jadexTestRunner;
	}
}
