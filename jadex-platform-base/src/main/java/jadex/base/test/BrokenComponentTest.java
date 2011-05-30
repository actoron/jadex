package jadex.base.test;


import jadex.bridge.IErrorReport;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 *  Test a component.
 */
public class BrokenComponentTest implements	Test
{
	//-------- attributes --------
	
	/** The component. */
	protected String	comp;
	
	/** The error. */
	protected IErrorReport	error;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public BrokenComponentTest(String comp, IErrorReport error)
	{
		this.comp	= comp;
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
		return comp;
	}
}
