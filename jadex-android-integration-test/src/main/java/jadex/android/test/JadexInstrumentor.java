package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.android.service.JadexPlatformManager;
import jadex.base.test.impl.BrokenComponentTest;
import jadex.bridge.ErrorReport;

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

public class JadexInstrumentor extends InstrumentationTestRunner
{

	private static final String LOG_TAG = "JadexTestRunner";
	
	private ClassLoader jadexCl;

	private String	sourceDir;

	@Override
	public void onCreate(Bundle arguments)
	{
		Context targetContext = getTargetContext();
		sourceDir = targetContext.getApplicationInfo().sourceDir;
		jadexCl = this.getClass().getClassLoader();
		
		// Set Context:
		AndroidContextManager.getInstance().setAndroidContext(targetContext);
		// Set Classloader for app:
		JadexPlatformManager.getInstance().setAppClassLoader(sourceDir, jadexCl);
		
		super.onCreate(arguments);
	}
	
	@Override
	public TestSuite getTestSuite()
	{
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), jadexCl);
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
			// To execute a single test:
//			Test singleTest = createTest("jadex.launch.test.MicroTest", "jadex.micro.testcases.stream.InitiatorAgent", sourceDir);
//			suite.addTest(singleTest);
			
			
			Test bdiTest = createTest("jadex.launch.test.BDIV3Test", "jadex.bdiv3.testcases", sourceDir);
			Test microTest = createTest("jadex.launch.test.MicroTest", "jadex.micro.testcases", sourceDir);

			suite.addTest(bdiTest);
			suite.addTest(microTest);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ErrorReport errorReport = new ErrorReport();
			errorReport.setErrorText(e.getMessage());
			BrokenComponentTest error = new BrokenComponentTest("creation", errorReport);
			suite.addTest(error);
		}
		
		if (suite.countTestCases() < 10) {
			ErrorReport errorReport = new ErrorReport();
			errorReport.setErrorText("Less than 10 Testcases found - Problem with loading them?");
			BrokenComponentTest error = new BrokenComponentTest("creation", errorReport);
			suite.addTest(error);
		}
		
		return suite;
	}


	private Test createTest(String testClassName, String testsPackage, String classRoot) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Class<?> test = jadexCl.loadClass(testClassName);
		Constructor<?> constructor = test.getConstructor(String.class, String.class);
		Object testCase = null;
		try {
			testCase = constructor.newInstance(testsPackage, classRoot);
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
		AndroidTestRunner jadexTestRunner = new JadexTestRunner();
		return jadexTestRunner;
	}
}
