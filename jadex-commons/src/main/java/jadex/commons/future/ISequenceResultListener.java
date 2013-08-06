package jadex.commons.future;


/**
 * 
 */
public interface ISequenceResultListener<E, F> extends IIntermediateResultListener<E>
{
	/**
	 * 
	 */
	public void resultAvailable1(E result);
	
	/**
	 * 
	 */
	public void resultAvailable2(F result);
}
