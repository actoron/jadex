package jadex.commons.future;

import java.util.Collection;

/**
 *  Empty implementation of the intermediate result listener.
 *  Allows for omitting methods if not used.
 *  Consider using instead of new IIntermediateResultListener<>()
 */
public class IntermediateEmptyResultListener<E> implements IIntermediateResultListener<E>
{
	public IntermediateEmptyResultListener() 
	{
	}
	
	@Override
	public void intermediateResultAvailable(E result) 
	{
	}
	
	@Override
	public void resultAvailable(Collection<E> result) 
	{
	}
	
	@Override
	public void exceptionOccurred(Exception exception) 
	{
	}
	
	@Override
	public void finished() 
	{
	}
	
	@Override
	public void maxResultCountAvailable(int max) 
	{
	}
}
