package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.android.service.JadexPlatformManager;
import jadex.base.test.impl.ComponentLoadTest;
import jadex.bridge.ErrorReport;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;
import dalvik.system.VMRuntime;

public class JadexInstrumentor extends InstrumentationTestRunner
{

	private static final String LOG_TAG = "JadexTestRunner";
	
	private ClassLoader jadexCl;

	private String	sourceDir;

	private String testClassesArg;

	private boolean logOnly;

	private static final String ARGUMENT_TEST_CLASS = "class";
    private static final String ARGUMENT_LOG_ONLY = "log";
    
    /**
     * Defines which TestSuites are included in the instrumentation run.
     * Make sure that the corresponding dependencies are defined in pom.xml,
     * especially the jadex-applications-xyz modules.
     */
    private static final String[] ACTIVATED_TESTS = new String[]{
    	"jadex.launch.test.MicroTest",
//    	"jadex.launch.test.BDITest",
//    	"jadex.launch.test.BPMNTest",
//    	"jadex.launch.test.BDIBPMNTest",
//    	"jadex.launch.test.GPMNTest",
    	"jadex.launch.test.BDIV3Test"
    };
	
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
						+ "Test execution will likely fail with a max heapsize of less than 32MB!"
						+ "\nNote: It seems to be impossible to increase heapsize on x86 images.");
				
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
			
			Log.i(LOG_TAG, "JadexInstrumentor creating JUnit ComponentTest classes...");
			
			if (testClassesArg != null) {
				if (testClassesArg.contains("#")) {
					testClassesArg = testClassesArg.split("#")[1];
					
					try {
						jadexCl.loadClass(testClassesArg);
					} catch (Exception e) {
						try {
							jadexCl.loadClass(testClassesArg + "Agent");
							testClassesArg = testClassesArg + "Agent";
						} catch (Exception e2) {
							
						}
					}
				}
				System.out.println("Creating testsuite just for: " + testClassesArg);
//				Test requestedTest = createTest(testClassesArg, sourceDir, jadexCl);
				IndividualTest test = new IndividualTest(testClassesArg, sourceDir);
				suite.addTest(test);
			} else {
				for (String testSuiteName : ACTIVATED_TESTS) {
					suite.addTest(createTest(testSuiteName, sourceDir, jadexCl));
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ErrorReport errorReport = new ErrorReport();
			errorReport.setErrorText(e.getMessage());
			ComponentLoadTest error = new ComponentLoadTest("creation", errorReport);
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


	public static Test createTest(String testClassName, String classRoot, ClassLoader loader) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Class<?> test= loader.loadClass(testClassName);
		Constructor<?> constructor = test.getConstructor(String.class);
		Object testCase = null;
		try {
			testCase = constructor.newInstance(classRoot);
		} catch (Throwable t) {
			t.printStackTrace();
			ErrorReport errorReport = new ErrorReport();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(baos));
			try {
				errorReport.setErrorText("Error constructing testcase: \n" + baos.toString("UTF8"));
			} catch (UnsupportedEncodingException e) {
				errorReport.setErrorText("Error constructing testcase: \n" + t.getMessage());
			}
			ComponentLoadTest error = new ComponentLoadTest(testClassName, errorReport);
			testCase = error;
		}
		Test testCase2 = (Test) testCase;
		return testCase2;
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
