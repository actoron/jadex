package jadex.base.test.impl;


import jadex.bridge.IErrorReport;
import jadex.bridge.modelinfo.IModelInfo;
import junit.framework.TestCase;

/**
 *  Test a component.
 */
public class BrokenComponentTest extends	TestCase
{
	//-------- attributes --------
	
	/** The component model. */
	protected String	filename;
	
	/** The component full name. */
	protected String	fullname;
	
	/** The error. */
	protected IErrorReport	error;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public BrokenComponentTest(IModelInfo comp, IErrorReport error)
	{
		this.filename	= comp.getFilename();
		this.fullname	= comp.getFullName();
		this.error	= error;
	}
	
	/**
	 *  Create a component test.
	 */
	public BrokenComponentTest(String filename, IErrorReport error)
	{
		this.filename	= filename;
		this.error	= error;
	}
	
	//-------- methods --------
	
	/**
	 *  The number of test cases.
	 */
	public int countTestCases()
	{
		return 1;
	}
	
	/**
	 *  Test the component.
	 */
	public void runBare()
	{
//		try
//		{
//			result.startTest(this);
//		}
//		catch(IllegalStateException e)
//		{
			// Hack: Android test runner tries to do getClass().getMethod(...) for test name, grrr.
			// See: http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.2.1_r1/android/test/InstrumentationTestRunner.java#767
//		}
		
		throw new RuntimeException(error.getErrorText());			
	}
	
	public String getName()
	{
		return this.toString();
	}
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return "broken: "+(fullname!=null ? fullname : filename);
	}
}
