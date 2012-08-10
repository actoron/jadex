package jadex.commons.transformation.binaryserializer;

/**
 * Reporter receiving errors encountered during decoding.
 */
public interface IErrorReporter
{
	/**
	 *  Method called when a decoding error occurs.
	 *  
	 *  @param e The exception occurred during decoding.
	 */
	public void exceptionOccurred(Exception e);
}
