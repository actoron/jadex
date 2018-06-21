package jadex.platform.service.security;

/**
 *  Message class representing a request to reencrypt a message with the current
 *  cryptosuite.
 *
 */
public class ReencryptionRequest
{
	/** Original encrypted content. */
	protected byte[] content;
	
	/**
	 *  Creates the request.
	 */
	public ReencryptionRequest()
	{
	}
	
	/**
	 *  Gets the content.
	 *  @return The content.
	 */
	public byte[] getContent()
	{
		return content;
	}
	
	/**
	 *  Sets the content.
	 *  @param content The content.
	 */
	public void setContent(byte[] content)
	{
		this.content = content;
	}
}
