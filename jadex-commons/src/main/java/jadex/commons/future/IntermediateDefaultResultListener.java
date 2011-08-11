package jadex.commons.future;


import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 *  The default listener for just printing out result information.
 *  Is used as fallback when no other listener is available.
 */
public abstract class IntermediateDefaultResultListener	extends DefaultResultListener	implements IIntermediateResultListener
{
	//-------- attributes --------
	
//	/** The static instance. */
//	private static IIntermediateResultListener instance;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public IntermediateDefaultResultListener()
	{
		super();
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public IntermediateDefaultResultListener(Logger logger)
	{
		super(logger);
	}
	
//	/**
//	 *  Get the listener instance.
//	 *  @return The listener.
//	 */
//	public static IResultListener getInstance()
//	{
//		// Hack! Implement that logger can be passed
//		if(instance==null)
//		{
//			instance = new IntermediateDefaultResultListener();
//		}
//		return instance;
//	}
	
	//-------- methods --------
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public abstract void intermediateResultAvailable(Object result);
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished()
    {
		//logger.info("finished");
    }
    
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(Object result)
	{
		if(result instanceof Collection)
		{
			Collection	coll	= (Collection)result;
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				intermediateResultAvailable(it.next());
			}
		}
		else
		{
			exceptionOccurred(new RuntimeException("Result not java.util.Collection: "+result));
		}
	}
}
