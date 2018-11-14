package jadex.bridge;

import java.io.Serializable;
import java.util.Map;

/**
 *  The interface for an error report of loaded models.
 */
public interface IErrorReport extends Serializable
{
	/**
	 *  Get the text representation of the report.
	 *  @return The text.
	 */
	public String getErrorText();
	
	/**
	 *  Get the html representation of the report.
	 *  @return The html string.
	 */
	public String getErrorHTML();

	/**
	 *  Get the external documents.
	 *  (model -> report)
	 *  @return The external documents.
	 */
	public Map<String, String> getDocuments();
}
