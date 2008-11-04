package jadex.rules.state.io.xml;

/**
 *  A struct to represent an element on the stack while parsing.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The current XML path. */
	public String	path;
	
	/** The OAV object id for the current XML element (or null if none). */
	public Object	object;
	
	/** The content of the current XML element, if any. */
	public StringBuffer	content;
	
	//-------- methods --------
	
	/**
	 *  Create a string representation of the stack element.
	 */
	public String	toString()
	{
		return path+":"+object+":"+(content!=null ? content.toString().trim() : "");
	}
}