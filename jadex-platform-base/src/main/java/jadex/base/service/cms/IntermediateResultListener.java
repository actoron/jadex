package jadex.base.service.cms;

import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Listener that notifies result listeners of components.
 */
public class IntermediateResultListener implements IIntermediateResultListener<Tuple2<String, Object>>
{
	//-------- attributes --------
	
	/** The listeners. */
	protected List<IResultListener<Collection<Tuple2<String, Object>>>> listeners;
	
	/** The results map. */
	protected Map<String, Object> results;
	
	/** Boolean flag if has initial listener (if yes no exception will be printed in CleanupCommand). */
	protected boolean initial;
	
	//-------- constructors --------
	
	/**
	 *  Create a new result listener.
	 */
	public IntermediateResultListener(IResultListener<Collection<Tuple2<String, Object>>> listener)
	{
		this.initial = listener!=null;
		addListener(listener);
	}
	
	//-------- methods --------
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(Tuple2<String, Object> result)
	{
		IResultListener<Object>[] alisteners = null;
		synchronized(this)
		{
			if(listeners!=null)
			{
				alisteners = (IResultListener<Object>[])listeners.toArray(new IResultListener[listeners.size()]);
			}
		}
		
		addResult(result.getFirstEntity(), result.getSecondEntity());
		
		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				if(alisteners[i] instanceof IIntermediateResultListener)
				{
					IIntermediateResultListener<Object> lis = (IIntermediateResultListener)alisteners[i];
					lis.intermediateResultAvailable(result);
				}
			}
		}
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
	public void finished()
	{
		IResultListener<Object>[] alisteners = null;
		synchronized(this)
		{
			if(listeners!=null)
			{
				alisteners = (IResultListener<Object>[])listeners.toArray(new IResultListener[listeners.size()]);
			}			
		}
			
		Collection<Tuple2<String, Object>> res = new ArrayList<Tuple2<String, Object>>();
		if(results!=null)
		{
			for(Iterator<String> it=results.keySet().iterator(); it.hasNext(); )
			{
				String key = it.next();
				res.add(new Tuple2<String, Object>(key, results.get(key)));
			}
		}
		
		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				if(alisteners[i] instanceof IIntermediateResultListener)
				{
					IIntermediateResultListener<Object> lis = (IIntermediateResultListener)alisteners[i];
					lis.finished();
				}
				else
				{
					alisteners[i].resultAvailable(res);
				}
			}
		}
	}
	
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(Collection<Tuple2<String, Object>> result)
	{
		IResultListener<Object>[] alisteners = null;
		synchronized(this)
		{
			if(listeners!=null)
			{
				alisteners = (IResultListener<Object>[])listeners.toArray(new IResultListener[listeners.size()]);
			}			
		}
		
		if(result!=null)
		{
			for(Iterator<Tuple2<String, Object>> it=result.iterator(); it.hasNext(); )
			{
				Tuple2<String, Object> val = it.next();
				addResult(val.getFirstEntity(), val.getSecondEntity());
			}
		}
		
		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].resultAvailable(result);
//				if(alisteners[i] instanceof IIntermediateResultListener)
//				{
//					IIntermediateResultListener<Object> lis = (IIntermediateResultListener)alisteners[i];
//					
//					lis.finished();
//				}
//				else
//				{
//					alisteners[i].resultAvailable(results);
//				}
			}
		}
	}
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		IResultListener<Object>[] alisteners = null;
		synchronized(this)
		{
			if(listeners!=null)
			{
				alisteners = (IResultListener<Object>[])listeners.toArray(new IResultListener[listeners.size()]);
			}			
		}
		
		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].exceptionOccurred(exception);
			}
		}
	}
	
	/**
	 *  Add a result listener.
	 *  @param listener The result listener.
	 */
	public void addListener(IResultListener<Collection<Tuple2<String, Object>>> listener)
	{
		if(listener!=null)
		{
			if(listeners==null)
			{
				synchronized(this)
				{
					if(listeners==null)
					{
						listeners = Collections.synchronizedList(new ArrayList<IResultListener<Collection<Tuple2<String, Object>>>>());
					}
				}
			}
			listeners.add(listener);
		}
	}
	
	/**
	 *  Remove a result listener.
	 *  @param listener The result listener.
	 */
	public void removeListener(IResultListener<Collection<Tuple2<String, Object>>> listener)
	{
		if(listeners!=null)
		{
			listeners.remove(listener);
		}
	}
	
	/**
	 *  Add a result value.
	 *  @param name The name.
	 *  @param value The value.
	 */
	// Must be synchronized to protected creation and addition of results
	protected synchronized void addResult(String name, Object value)
	{
		if(results==null)
		{
			results = new HashMap<String, Object>();
		}
		results.put(name, value);
	}
	
	/**
	 *  Get the result collection.
	 *  @return A collection with the results.
	 */
	public synchronized Collection<Tuple2<String, Object>> getResultCollection()
	{
		Collection<Tuple2<String, Object>> ret = null;
		if(results!=null)
		{
			ret = new ArrayList<Tuple2<String, Object>>();
			for(Iterator<String> it=results.keySet().iterator(); it.hasNext(); )
			{
				String key = it.next();
				ret.add(new Tuple2<String, Object>(key, results.get(key)));
			}
		}
		return ret;
	}
	
	/**
	 *  Get the results map.
	 *  return the result map.
	 */
	public Map<String, Object> getResultMap()
	{
		return results;
	}

	/**
	 *  Get the initial.
	 *  @return the initial.
	 */
	public boolean isInitial()
	{
		return initial;
	}

	/**
	 *  Set the initial.
	 *  @param initial The initial to set.
	 */
	public void setInitial(boolean initial)
	{
		this.initial = initial;
	}
}

