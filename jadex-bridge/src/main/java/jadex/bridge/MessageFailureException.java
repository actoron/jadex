package jadex.bridge;

/**
 *  An exception indicating a message failure (e.g. unknown receiver).
 */
public class MessageFailureException	extends RuntimeException//BDIFailureException
{
	//-------- attributes --------
	
	/** The failed message (native). */
	// Hack!!! Should be IMessageEvent???
	protected Object	message;
	
	//----- constructors --------
	
	/**
	 *  Create a new MessageFailureException.
	 *  @param message	The failed message.
	 *  @param cause	The parent exception.
	 */
	public MessageFailureException(Object message, Throwable cause)
	{
		super(null, cause);
		this.message	= message;
	}

	/**
	 *  Create a new MessageFailureException.
	 *  @param message	The failed message.
	 *  @param text	The failure text.
	 */
	public MessageFailureException(Object message, String text)
	{
		super(text, null);
		this.message	= message;
	}
}
