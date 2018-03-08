package jadex.commons.transformation.traverser;

/**
 * Reporter receiving errors encountered during decoding.
 */
public interface IErrorReporter
{
	/** A default error reporter that ignores errors. */
	public static IErrorReporter	IGNORE	= new IErrorReporter()
	{
		public void exceptionOccurred(Exception e)
		{
			// ignore.
		}
	};
	
	/**
	 *  Method called when a decoding error occurs.
	 *  
	 *  @param e The exception occurred during decoding.
	 */
	public void exceptionOccurred(Exception e);
}
