package jadex.bridge;

import java.util.Map;

/**
 *  Error report that holds information about model errors.
 */
public class ErrorReport implements IErrorReport
{
	//-------- attributes --------
	
	/** The error text. */
	protected String errtext;
	
	/** The error html text. */
	protected String errhtml;
	
	/** The external reports. */
	protected Map<String, String> documents;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public ErrorReport()
	{
	}

	/**
	 * 
	 */
	public ErrorReport(String errtext, String errhtml, Map documents)
	{
		this.errtext = errtext;
		this.errhtml = errhtml;
		this.documents = documents;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the text representation of the report.
	 *  @return The text.
	 */
	public String getErrorText()
	{
		return errtext;
	}
	
	/**
	 *  Set the errtext.
	 *  @param errtext The errtext to set.
	 */
	public void setErrorText(String errtext)
	{
		this.errtext = errtext;
	}
	
	/**
	 *  Get the html representation of the report.
	 *  @return The html string.
	 */
	public String getErrorHTML()
	{
		return errhtml;
	}
	
	/**
	 *  Set the errhtml.
	 *  @param errhtml The errhtml to set.
	 */
	public void setErrorHTML(String errhtml)
	{
		this.errhtml = errhtml;
	}

	/**
	 *  Get the external documents.
	 *  (model -> report)
	 *  @return The external documents.
	 */
	public Map<String, String> getDocuments()
	{
		return documents;
	}

	/**
	 *  Set the documents.
	 *  @param documents The documents to set.
	 */
	public void setDocuments(Map<String, String> documents)
	{
		this.documents = documents;
	}
}
