package jadex.base.test.impl;


import jadex.bridge.IErrorReport;
import jadex.bridge.modelinfo.IModelInfo;
import junit.framework.TestCase;

/**
 *  Store test results of loading a component.
 */
public class ComponentLoadTest extends	TestCase
{
	//-------- attributes --------
	
	/** The component model. */
	protected String	filename;
	
	/** The component full name. */
	protected String	fullname;
	
	/** The component type. */
	protected String	type;
	
	/** The error. */
	protected IErrorReport	error;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public ComponentLoadTest(IModelInfo comp, IErrorReport error)
	{
		this.filename	= comp.getFilename();
		this.fullname	= comp.getFullName();
		this.type	= comp.getType();
		this.error	= error;
	}
	
	/**
	 *  Create a component test.
	 */
	public ComponentLoadTest(String filename, IErrorReport error)
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
		
		if(error!=null)
		{
			throw new RuntimeException(error.getErrorText());
		}
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
		return "load: "+(fullname!=null ? fullname + " (" + type + ")" : filename);
	}
}
