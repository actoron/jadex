package jadex.bridge;

/**
 *  A content exception occurs, if the content cannot be properly
 *  extracted from a message with language and ontology set.
 */
public class ContentException extends RuntimeException
{
	//-------- constructors --------

	/**
	 *  Create a new content exception.
	 */
	public ContentException(String msg)
	{
		super(msg);
//		System.out.println(this);
	}
	
	/**
	 *  Create a new content exception.
	 */
	public ContentException(String msg, Throwable cause)
	{
		super(msg, cause);
//		System.out.println(this);
	}
}
