package jadex.base.test;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  A test report captures the description and results of a test.
 */
public class TestReport
{
	//-------- attributes --------

	/** The test name .*/
	protected String name;

	/** The test description. */
	protected String description;

	/** The test success. */
	protected boolean succeeded;

	/** The failure reason. */
	protected String reason;

	//-------- constructors --------

	/**
	 *  Create a new test report.
	 */
	public TestReport()
	{
	}

	/**
	 *  Create a new test report.
	 */
	public TestReport(String name, String description)
	{
		this(name, description, false, null);
	}

	/**
	 *  Create a new test report.
	 */
	public TestReport(String name, String description, boolean succeded, String reason)
	{
		this.name = name;
		this.description = description;
		this.succeeded = succeded;
		this.reason = reason;
	}

	/**
	 *  Create a new test report.
	 */
	public TestReport(String name, String description, Exception reason)
	{
		this.name = name;
		this.description = description;
		this.succeeded = false;
		setFailed(reason);
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Has the test succeeded.
	 *  @return True, if test was successful.
	 */
	public boolean isSucceeded()
	{
		return succeeded;
	}

	/**
	 *  Is the test finished, i.e. failed or succeeded?
	 */
	public boolean isFinished()
	{
		return succeeded || reason!=null;
	}

	/**
	 *  Set the test success.
	 *  @param succeded True on success.
	 */
	public void setSucceeded(boolean succeded)
	{
		this.succeeded = succeded;
	}

	/**
	 *  Get the failure reason.
	 *  @return The failure reason.
	 */
	public String getReason()
	{
		return reason;
	}

	/**
	 *  Set the failure reason.
	 *  @param reason The failure reason.
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}

	/**
	 *  Set the report result to failed and set the failure reason.
	 *  @param reason The failure reason.
	 */
	public void setFailed(Exception reason)
	{
		StringWriter	sw	= new StringWriter();
		reason.printStackTrace(new PrintWriter(sw));
		setFailed(sw.toString());
	}

	/**
	 *  Set the report result to failed and set the failure reason.
	 *  @param reason The failure reason.
	 */
	public void setFailed(String reason)
	{
		this.succeeded	= false;
		this.reason = reason;
	}

	/**
	 *  Returns a string representation of the object. In general, the
	 *  <code>toString</code> method returns a string that
	 *  "textually represents" this object. The result should
	 *  be a concise but informative representation that is easy for a
	 *  person to read.
	 *
	 *  @return a string representation of the object.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append("Test name: ");
		ret.append(name);
		ret.append(", description: ");
		ret.append(description);
		if(succeeded)
		{
			ret.append(", result: succeeded.");
		}
		else
		{
			ret.append(", result: failed, reason: ");
			ret.append(reason);
		}
		//ret.append("\n");

		return ret.toString();
	}

	/**
	 *  Create an HTML representation of this element that can be
	 *  included in an HTML document.
	 * /
	public String	getHTMLFragment()
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<h4> Test: ");
		ret.append(name);
		ret.append("</h4>");
		ret.append("<strong>Description: </strong>");
		ret.append(description);
		ret.append("<br>");
		if(succeeded)
		{
			ret.append("<strong>Result: </strong>Succeeded<br>");
		}
		else
		{
			ret.append("<strong>Result: </strong>Failed<br><strong>Reason: </strong>");
			ret.append(reason);
		}
		ret.append("<br>");

		return ret.toString();
	}*/

	/*public static void main(String[] args)
	{
		try
		{
			ExpressionEvaluator ee = new ExpressionEvaluator(
				"String[].class", // expression
				Object.class,     // optionalExpressionType
				new String[0],    // parameterNames,
				new Class[0],  // parameterTypes
				new Class[0],                  // thrownExceptions
				null                           // optionalClassLoader
			);
			System.out.println("Result: "+ee.evaluate(new Object[0]));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		try
		{
			String script = (
				"public static Class getC() {\n" +
				"    return String[].class;\n" +
				"}\n"
			);

			Class c = new ClassBodyEvaluator(script).evaluate();
			Method m = c.getMethod("getC", new Class[0]);
			Object res = m.invoke(null, new Object[0]);
			System.out.println("Result: "+res);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}*/

}
