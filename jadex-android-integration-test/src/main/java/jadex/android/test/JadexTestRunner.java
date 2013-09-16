package jadex.android.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import android.app.Instrumentation;
import android.content.Context;
import android.test.AndroidTestRunner;

public class JadexTestRunner extends AndroidTestRunner
{

	@Override
	public void setTestClassName(String testClassName, String testMethodName)
	{
		System.out.println("JadexTestRunner: setTestClassName");
		super.setTestClassName(testClassName, testMethodName);
	}

	@Override
	public void setTest(Test test)
	{
		super.setTest(test);
	}

	@Override
	public void clearTestListeners()
	{
		// TODO Auto-generated method stub
		super.clearTestListeners();
	}

	@Override
	public void addTestListener(TestListener testListener)
	{
		// TODO Auto-generated method stub
		System.out.println("JadexTestRunner: addTestListener");
		super.addTestListener(testListener);
	}

	@Override
	protected TestResult createTestResult()
	{
		// TODO Auto-generated method stub
		return super.createTestResult();
	}

	@Override
	public List<TestCase> getTestCases()
	{
		// TODO Auto-generated method stub
		System.out.println("JadexTestRunner: getTestCases");
		return super.getTestCases();
	}

	@Override
	public String getTestClassName()
	{
		// TODO Auto-generated method stub
		return super.getTestClassName();
	}

	@Override
	public TestResult getTestResult()
	{
		// TODO Auto-generated method stub
		return super.getTestResult();
	}

	@Override
	public void runTest()
	{
		System.out.println("JadexTestRunner: runTest");
		super.runTest();
	}

	@Override
	public void runTest(TestResult testResult)
	{
		System.out.println("JadexTestRunner: runTest");
		super.runTest(testResult);
	}

	@Override
	public void setContext(Context context)
	{
		// TODO Auto-generated method stub
		System.out.println("JadexTestRunner: setContext");
		super.setContext(context);
	}

	@Override
	public void setInstrumentation(Instrumentation instrumentation)
	{
		// TODO Auto-generated method stub
		System.out.println("JadexTestRunner: setInstrumentation");
		super.setInstrumentation(instrumentation);
	}

	@Override
	public void setInstrumentaiton(Instrumentation instrumentation)
	{
		// TODO Auto-generated method stub
		System.out.println("JadexTestRunner: setInstrumentaiton");
		super.setInstrumentaiton(instrumentation);
	}

	@Override
	protected Class loadSuiteClass(String suiteClassName) throws ClassNotFoundException
	{
		System.out.println("JadexTestRunner: loadSuiteClass");
		return super.loadSuiteClass(suiteClassName);
	}

	@Override
	public void testStarted(String testName)
	{
		// TODO Auto-generated method stub
		super.testStarted(testName);
	}

	@Override
	public void testEnded(String testName)
	{
		// TODO Auto-generated method stub
		super.testEnded(testName);
	}

	@Override
	public void testFailed(int status, Test test, Throwable t)
	{
		// TODO Auto-generated method stub
		super.testFailed(status, test, t);
	}

	@Override
	protected void runFailed(String message)
	{
		// TODO Auto-generated method stub
		super.runFailed(message);
	}

}
