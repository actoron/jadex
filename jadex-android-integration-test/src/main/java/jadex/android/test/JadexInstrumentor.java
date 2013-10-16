package jadex.android.test;

import jadex.android.AndroidContextManager;
import jadex.android.commons.JadexDexClassLoader;
import jadex.bdiv3.AsmDexBdiClassGenerator;
import jadex.commons.SUtil;
import jadex.launch.test.MicroTest;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;
import dalvik.system.PathClassLoader;

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

	private ClassLoader jadexCl;

	private String targetAppDir;

	private File optimizePath;

	@Override
	public void onCreate(Bundle arguments)
	{
//		Log.i(LOG_TAG, "getTestSuite sleep");
//		try
//		{
//			Thread.sleep(5000);
//		}
//		catch (InterruptedException e1)
//		{
//			e1.printStackTrace();
//		}
//		Log.i(LOG_TAG, "getTestSuite sleep end");
		
		Context targetContext = getTargetContext();
		targetAppDir = targetContext.getApplicationInfo().sourceDir;
		optimizePath = targetContext.getDir("outdex", Context.MODE_PRIVATE);
		jadexCl = createJadexClassLoader(targetContext);
		
		try
		{
			// Set Context:
			Class<?> contextMgrClazz =
//			jadexCl.loadClass("jadex.android.AndroidContextManager");
			jadexCl.loadClass(AndroidContextManager.class.getCanonicalName());
			Method getInstance = contextMgrClazz.getMethod("getInstance");
			Method setAndroidContext = contextMgrClazz.getMethod("setAndroidContext", Context.class);
			Object contextManager = getInstance.invoke(null);
			setAndroidContext.invoke(contextManager, targetContext);
			
			Class<?> classGeneratorClazz= jadexCl.loadClass(AsmDexBdiClassGenerator.class.getCanonicalName());
			Field outField = classGeneratorClazz.getDeclaredField("OUTPATH");
			outField.set(null, optimizePath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
//		AndroidContextManager androidContext = AndroidContextManager.getInstance();
//		androidContext.setAndroidContext(getContext());
		
		super.onCreate(arguments);
	}


	private ClassLoader createJadexClassLoader(Context targetContext)
	{
//		ClassLoader targetClassLoader = targetContext.getClassLoader();

		Context myContext = getContext();
		ClassLoader myCl = myContext.getClassLoader();
		ClassLoader systemCl = myCl.getParent();
		
		Collection<? extends URL> collectDexPathUrls = SUtil.androidUtils().collectDexPathUrls(myCl);

		// The original classpath contains /system/framework/android.test.runner.jar.
		// we need to extract that for our own classloader, because it contains classes
		// needed for unit testing.
		String testlib = null;
		for (URL url : collectDexPathUrls)
		{
			if (!url.toString().contains(".apk")) {
				testlib = url.getPath();
			}
		}
		
		PathClassLoader testlibsloader = new PathClassLoader(testlib, systemCl);
		PathClassLoader integrationLoader = new PathClassLoader(myContext.getApplicationInfo().sourceDir, testlibsloader);
		
		
//		try
//		{
//			integrationLoader.loadClass("jadex.android.commons.JadexDexClassLoader");
//			System.out.println("JadexDexClassLoader loaded");
//		}
//		catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
		
		JadexDexClassLoader jadexCl = new JadexDexClassLoader(targetAppDir, optimizePath.getAbsolutePath(), null, integrationLoader);
		jadexCl.defineClass("jadex.android.commons.JadexDexClassLoader", JadexDexClassLoader.class);
		return jadexCl;
	}


	@Override
	public TestSuite getTestSuite()
	{
		TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), jadexCl);
		TestSuite suite = testSuiteBuilder.build();
		
		try
		{
			// Not working on android right now:
//			suite.addTest(new MicroTest("jadex.micro.testcases.stream.InitiatorAgent", targetAppDir));
//			suite.addTest(new MicroTest("jadex.micro.testcases.nfmethodprop", targetAppDir));
			
			Class<?> test = jadexCl.loadClass("jadex.launch.test.MicroTest");
			Constructor<?> constructor = test.getConstructor(String.class, String.class);
			Object testCase = constructor.newInstance("jadex.micro.testcases", targetAppDir);
			suite.addTest((Test) testCase);
			
//			Class<?> test = jadexCl.loadClass("jadex.launch.test.BDIV3Test");
//			Constructor<?> constructor = test.getConstructor(String.class, String.class);
//			Object bdiTest = constructor.newInstance("jadex.bdiv3.testcases", targetAppDir);
//			suite.addTest((Test) bdiTest);
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
