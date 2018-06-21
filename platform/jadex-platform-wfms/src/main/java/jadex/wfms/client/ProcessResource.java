package jadex.wfms.client;

public class ProcessResource
{
	/** File name of the resource */
	protected String fileName;
	
	/** File content */
	protected byte[] content;
	
	/**
	 *  Creates a new model resource.
	 *  @param fileName file name
	 *  @param content file content
	 */
	public ProcessResource(String fileName, byte[] content)
	{
		this.fileName = fileName;
		this.content = content;
	}

	/**
	 *  Gets the fileName.
	 *  @return The fileName.
	 */
	public String getFileName()
	{
		return fileName;
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
	 *  Sets the fileName.
	 *
	 *  @param fileName The fileName.
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 *  Sets the content.
	 *
	 *  @param content The content.
	 */
	public void setContent(byte[] content)
	{
		this.content = content;
	}
	
	
}
