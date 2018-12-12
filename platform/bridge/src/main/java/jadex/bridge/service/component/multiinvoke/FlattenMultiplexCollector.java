package jadex.bridge.service.component.multiinvoke;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 * 
 */
public class FlattenMultiplexCollector implements IMultiplexCollector
{
	/** The future. */
	protected Future<Object> fut;
	
	/** Flag if flatten. */
	protected boolean flatten;
	
	/** The list of calls. */
	protected List<Future<Void>> calls;
	
	/** The list of results (if ret is not intermediate future). */
	protected List<Object> callresults;

	//-------- constructors --------
	
	/**
	 *  Init to share code.
	 */
	public void init(Future<Object> fut, Method method, Object[] args, Method muxmethod)
	{
		this.fut = fut;
		this.calls = new ArrayList<Future<Void>>();
		
		// determine flattening
		this.flatten = true;
		Type motype = muxmethod.getGenericReturnType();
		if(SReflect.isSupertype(IIntermediateFuture.class, SReflect.getClass(motype)))
		{
			Type mitype = SReflect.getInnerGenericType(motype);
			this.flatten = !SReflect.isSupertype(IFuture.class, SReflect.getClass(mitype));
		}
		else if(SReflect.isSupertype(IFuture.class, SReflect.getClass(motype)))
		{
			Type mitype = SReflect.getInnerGenericType(motype);
			if(SReflect.isSupertype(Collection.class, SReflect.getClass(mitype)))
			{
				Type miitype = SReflect.getInnerGenericType(mitype);
				this.flatten = !SReflect.isSupertype(IFuture.class, SReflect.getClass(miitype));
			}
		}
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public void intermediateResultAvailable(Object result)
	{
		if(flatten)
		{
			if(result instanceof IIntermediateFuture)
			{
				final Future<Void> call = new Future<Void>();
				IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
				{
					public void intermediateResultAvailable(Object result)
					{
//						if(!agent.isComponentThread())
//							Thread.dumpStack();
						
//						System.out.println("ser iresult: "+agent.isComponentThread());
						addResult(result);
					}
					public void finished()
					{
//						System.out.println("ser fini: "+agent.isComponentThread());
						call.setResult(null);
//						opencalls.remove(call);
					}
					public void resultAvailable(Collection<Object> result)
					{
//						System.out.println("ser resulta: "+agent.isComponentThread());

						for(Iterator<Object> it=result.iterator(); it.hasNext(); )
						{
							addResult(result);
						}
						call.setResult(null);
//						opencalls.remove(call);
					}
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("ex: "+exception);
						call.setResult(null);
//						opencalls.remove(call);
					}
				};
				calls.add(call);
				((IIntermediateFuture<Object>)result).addResultListener(lis);
			}
			else if(result instanceof IFuture)
			{
				final Future<Void> call = new Future<Void>();
				IResultListener<Object> lis = new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
//						System.out.println("ser resultb: "+agent.isComponentThread());
						
						addResult(result);
						call.setResult(null);
//						opencalls.remove(this);
					}
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("ex: "+exception);
						call.setResult(null);
//						opencalls.remove(this);
					}
				};
				calls.add(call);
				((IFuture<Object>)result).addResultListener(lis);
			}
			else
			{
				addResult(result);
			}
		}
		else
		{
			addResult(result);
		}
	}
	
	/**
	 * 
	 */
	public void finished()
	{
//		System.out.println("fin: "+Thread.currentThread()+" "+agent.isComponentThread());
		if(calls.size()>0)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(calls.size(), true, new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
//						System.out.println("countlis1: "+agent.isComponentThread());

					setFinished();
				}

				public void exceptionOccurred(Exception exception)
				{
				}
			});
			for(int i=0; i<calls.size(); i++)
			{
				Future<Void> fut = calls.get(i);
				fut.addResultListener(lis);
			}
		}
		else
		{
			setFinished();
		}
	}
	
	/**
	 * 
	 */
	public void resultAvailable(Collection<Object> result)
	{
		for(Iterator<Object> it=result.iterator(); it.hasNext(); )
		{
			intermediateResultAvailable(it.next());
		}
		finished();
	}
	
	/**
	 * 
	 */
	public void exceptionOccurred(Exception exception)
	{
		setException(exception);
	}
	
	//-------- methods called to delegate results to the future --------
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(Object result)
	{
		if(fut instanceof IntermediateFuture)
		{
			((IntermediateFuture)fut).addIntermediateResult(result);
		}
		else
		{
			if(callresults==null)
				callresults = new ArrayList<Object>();
			callresults.add(result);
		}
	}
	
	/**
	 *  Set finished.
	 */
	protected void setFinished()
	{
		if(fut instanceof IntermediateFuture)
		{
			((IntermediateFuture)fut).setFinished();
		}
		else
		{
			fut.setResult(callresults);
		}
	}
	
	/**
	 *  Set an exception.
	 *  @param exception The exception.
	 */
	protected void setException(Exception exception)
	{
		fut.setException(exception);
	}
	
}
