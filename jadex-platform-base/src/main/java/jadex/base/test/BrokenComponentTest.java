package jadex.base.test;


import jadex.bridge.IErrorReport;
import jadex.bridge.modelinfo.IModelInfo;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 *  Test a component.
 */
public class BrokenComponentTest implements	Test
{
	//-------- attributes --------
	
	/** The component. */
	protected IModelInfo	comp;
	
	/** The filename (if model could not be loaded). */
	protected String	filename;
	
	/** The error. */
	protected IErrorReport	error;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public BrokenComponentTest(IModelInfo comp, IErrorReport error)
	{
		this.comp	= comp;
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
	public void run(TestResult result)
	{
		result.startTest(this);
		
		result.addError(this, new RuntimeException(error.getErrorText()));			

		result.endTest(this);
	}
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return comp!=null ? comp.getFullName() : filename;
	}
}
