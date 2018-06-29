package jadex.rules.examples.helloworld;

/**
 *  Simple java bean for a message.
 */
public class Message
{
	//-------- attributes --------
	
	/** The message text. */
	protected String text;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message.
	 *  @param text The text.
	 */
	public Message()
	{
	}
	
	/**
	 *  Create a new message.
	 *  @param text The text.
	 */
	public Message(String text)
	{
		this.text = text;
	}

	//-------- methods --------
	
	/**
	 *  Get the text.
	 *  @return The text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 *  Set the text.
	 *  @param text The text.
	 */
	public void setText(String text)
	{
		this.text = text;
	}
	
	/**
	 *  Get the text.
	 *  @return The text.
	 */
	public static boolean getText2(String a)
	{
		return false;
//		return "hello";
	}
}
