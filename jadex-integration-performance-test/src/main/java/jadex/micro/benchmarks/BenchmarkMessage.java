package jadex.micro.benchmarks;

/**
 * 
 */
public class BenchmarkMessage
{
	//-------- attributes --------
	
	/** The message text. */
	protected String text;

	/** The confidential flag. */
	protected boolean confidential;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message.
	 */
	public BenchmarkMessage()
	{
	}
	
	/**
	 *  Create a new message.
	 */
	public BenchmarkMessage(String text, boolean confidential)
	{
		this.text = text;
		this.confidential = confidential;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the text.
	 *  @return The text.
	 */
	public String getText()
	{
		return this.text;
	}

	/**
	 *  Set the text.
	 *  @param text The text to set.
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 *  Set the confidential flag.
	 *  @return The confidential.
	 */
	public boolean isConfidential()
	{
		return this.confidential;
	}

	/**
	 *  Set the confidential flag.
	 *  @param confidential the confidential to set.
	 */
	public void setConfidential(boolean confidential)
	{
		this.confidential = confidential;
	}	
}
