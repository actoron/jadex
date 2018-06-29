package jadex.base.test;

import jadex.commons.SUtil;

/**
 *  A testcase consists of an component type to test and the result reports.
 */
public class Testcase
{
	/** The name of the property for defining a test timeout. */
	public static final String	PROPERTY_TEST_TIMEOUT	= "test.timeout";
	
	//-------- attributes --------

	/** The number of tests to be performed. */
	protected int	cnt;
	
	/** The test reports. */
	protected TestReport[] reports;
	
	/** The time needed to perform the test. */
	protected long	duration;

	//-------- constructors --------

	/**
	 *  Create a new testcase.
	 */
	public Testcase()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new testcase.
	 */
	public Testcase(int cnt)
	{
		this.cnt = cnt;
	}
	
	/**
	 *  Create a testcase which is already performed.
	 */
	public Testcase(int cnt, TestReport[] reports)
	{
		this.cnt	= cnt;
		this.reports	= reports;
	}

	//-------- methods --------

	/**
	 *  Get the test count.
	 *  @return The test count.
	 */
	public int getTestCount()
	{
		return cnt;
	}

	/**
	 *  Set the test count.
	 *  @param cnt The test count.
	 */
	public void setTestCount(int cnt)
	{
		this.cnt = cnt;
	}

	/**
	 *  Get the test duration.
	 *  @return The test duration.
	 */
	public long getDuration()
	{
		return duration;
	}
	
	/**
	 *  Get the test duration.
	 */
	public void setDuration(long duration)
	{
		this.duration	= duration;
	}

	/**
	 *  Get the timeout.
	 *  @return The timeout.
	 * /
	public long getTimeout()
	{
		return timeout;
	}*/

	/**
	 *  Set the timeout.
	 *  @param timeout The timeout.
	 * /
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}*/

	/**
	 *  Get the reports.
	 *  @return The reports.
	 */
	public TestReport[] getReports()
	{
		return reports;
	}

	/**
	 *  Add a report.
	 *  @param report The report.
	 */
	public void addReport(TestReport report)
	{
		if(reports==null)
		{
			reports	= new TestReport[]{report};
		}
		else
		{
			TestReport[]	tmp	= new TestReport[reports.length+1];
			System.arraycopy(reports, 0, tmp, 0, reports.length);
			tmp[reports.length]	= report;
			reports	= tmp;
		}
	}

	/**
	 *  Set the reports.
	 *  @param reports The reports.
	 */
	public void setReports(TestReport[] reports)
	{
		this.reports = reports;
	}

	/**
	 *  Have all component tests succeeded.
	 */
	public boolean isSucceeded()
	{
		boolean ret = reports!=null && cnt==reports.length;
		for(int i = 0; ret && i < reports.length; i++)
		{
			ret = reports[i].isSucceeded();
		}
		return ret;
	}
	
	/**
	 *  Check if this test case has been performed and is finished.
	 */
	public boolean	isPerformed()
	{
		return reports!=null;	// Hack???
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
		ret.append("Testcase result: ");
		if(isSucceeded())
		{
			ret.append("succeeded.");
		}
		else
		{
			ret.append("failed, reports: ");
			ret.append(SUtil.arrayToString(reports));
		}

		return ret.toString();
	}

	/**
	 *  Create an HTML representation of this element that can be
	 *  included in an HTML document.
	 *  @param number The number of the testcase (when displayed in a longer list), -1 for no number.
	 *  @param name The name of the testcase (when displayed in a longer list), null for no name.
	 */
	public String	getHTMLFragment(int number, String name)
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<h3>");
		if(isSucceeded())
		{
			ret.append("<strong style=\"color: #00FF00\">O</strong>");
		}
		else
		{
			ret.append("<strong style=\"color: #FF0000\">X</strong>");
		}
		ret.append(" Testcase");
		if(number!=-1)
		{
			ret.append(" ");
			ret.append(number);
		}
		ret.append(": ");
		if(name!=null)
			ret.append(name);
		ret.append("</h3>\n");
		
		// Add list of reports.
		if(reports.length>0)
		{
			for(int i=0; i<reports.length; i++)
			{
				if(reports[i].isSucceeded())
				{
					ret.append("<strong style=\"color: #00FF00\">O</strong> Test: ");
					ret.append(reports[i].getName());
					ret.append(", ");
					ret.append(reports[i].getDescription());
				}
				else
				{
					ret.append("<strong style=\"color: #FF0000\">X</strong> Test: ");
					ret.append(reports[i].getName());
					ret.append(", ");
					ret.append(reports[i].getDescription());
					ret.append("<br>\n&nbsp;&nbsp;&nbsp;&nbsp;Failure reason: ");
					ret.append(reports[i].getReason());
				}
				ret.append("<br>\n");
			}
			
			// Add entry when number of reports does not match.
			if(cnt!=-1 && cnt!=reports.length)
			{
				ret.append("<strong style=\"color: #FF0000\">X</strong> Problem: Number of tests does not match. Expected ");
				ret.append(cnt);
				if(cnt == 1)
				{
					ret.append(" report but got ");
				}
				else
				{
					ret.append(" reports but got ");
				}
				ret.append(reports.length);
				ret.append(".<br>\n");
			}
			
			ret.append("Took ");
			ret.append(duration);
			ret.append(" ms.<br>\n");
		}
		

		return ret.toString();
	}
}
