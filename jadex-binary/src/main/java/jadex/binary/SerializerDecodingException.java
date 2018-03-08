package jadex.binary;

/** Exception that occured during decoding, preserving context state */
public class SerializerDecodingException extends RuntimeException
{
	/** ID	 */
	private static final long serialVersionUID = -5954592239771886373L;
	
	/** Decoding context in its failure state. */
	protected IDecodingContext context;
	
	/** Empty constructor */
	public SerializerDecodingException()
	{
	}
	
	/**
	 * Creates the Exception.
	 * @param cause Exception cause.
	 * @param context The decoding context.
	 */
	public SerializerDecodingException(Throwable cause, IDecodingContext context)
	{
		super(cause);
		this.context=context;
	}

	/**
	 * @return the context
	 */
	public IDecodingContext getContext()
	{
		return context;
	}

	/**
	 *  Sets the context.
	 *  @param context The context to set
	 */
	public void setContext(IDecodingContext context)
	{
		this.context = context;
	}
	
	
}
