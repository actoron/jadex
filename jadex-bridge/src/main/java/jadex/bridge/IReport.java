package jadex.bridge;

import java.io.Serializable;
import java.util.Map;

/**
 *  The interface for an error report of loaded models.
 */
public interface IReport extends Serializable
{
	/**
	 *  Check if this report is empty (i.e. the model is valid).
	 *  @return True, if empty.
	 */
	public boolean	isEmpty();

	/**
	 *  Generate an html representation of the report.
	 *  @return The html string.
	 */
	public String	toHTMLString();

	/**
	 *  Get the external documents.
	 *  (model -> report)
	 *  @return The external documents.
	 */
	public Map	getDocuments();
}
