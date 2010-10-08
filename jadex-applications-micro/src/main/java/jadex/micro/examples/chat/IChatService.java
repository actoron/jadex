package jadex.micro.examples.chat;


/**
 *  
 */
public interface IChatService
{
	/**
	 *  Tell something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void tell(String name, String text);
	
	/**
	 *  Hear something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void hear(String name, String text);
		
}
