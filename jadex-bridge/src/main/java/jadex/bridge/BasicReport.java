package jadex.bridge;

import java.util.Map;

/**
 * 
 */
public class BasicReport implements IReport
{
	protected Map documents;
	
	/**
	 * 
	 */
	public BasicReport()
	{
	}

	/**
	 * 
	 */
	public BasicReport(Map documents)
	{
		this.documents = documents;
	}
	
	/**
	 *  Check if this report is empty (i.e. the model is valid).
	 *  @return True, if empty.
	 */
	public boolean	isEmpty()
	{
		return documents==null || documents.isEmpty();
	}

	/**
	 *  Generate an html representation of the report.
	 *  @return The html string.
	 */
	public String toHTMLString()
	{
		// todo:
		return "";
	}

	/**
	 *  Get the external documents.
	 *  (model -> report)
	 *  @return The external documents.
	 */
	public Map	getDocuments()
	{
		return documents;
	}
}
