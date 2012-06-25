package jadex.bridge.service.component.multiinvoke;

import jadex.bridge.service.IService;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Sequential call distributor.
 *    
 *  It determines:
 *  - which services are called: services are called as long as arguments are available
 *  - with which arguments: each argument array once
 *  - when finished: after all arguments have been used
 */
public class SequentialMultiplexDistributor extends SimpleMultiplexDistributor
{
	/** The arguments. */
	protected Iterator<Object[]> itargs;
	
	/** The list of free services. */
	protected List<IService> freeservices;

	/** The list of busy services. */
	protected List<IService> busyservices;

	/** The waiting for service calls. */
	protected List<Future<IService>> waitingcalls;
	
	/**
	 *  Add a new service.
	 *  @param service The service.
	 */
	public void addService(IService service)
	{
		freeservices.add(service);
		checkPerformCall(service);
	}
	
	/**
	 *  Notify that the search has finished.
	 */
	public void serviceSearchFinished()
	{
		if(freeservices.size()+busyservices.size()==0)
		{
			results.setException(new RuntimeException("No service found."));
		}
		else
		{
			checkPerformCall(null);
		}
	}
	
	/**
	 *  Get the argument iterator.
	 */
	protected Iterator<Object[]> getArgumentIterator()
	{
		if(itargs==null)
		{
			if(args!=null && args.length>0)
			{
				itargs = SReflect.getIterator(args[0]);
			}
			else
			{
				itargs = new ArrayList<Object[]>().iterator();
			}
		}
		return itargs;
	}
	
	/**
	 *  Get the arguments for a call.
	 *  @return The arguments.
	 */
	public Object[] getArguments()
	{
		return itargs.next();
	}
	
	/**
	 *  Test if has arguments.
	 *  @return True if has args.
	 */
	public boolean hasArguments()
	{
		return itargs.hasNext();
	}
	
	/**
	 *  Get the service for a call.
	 *  @return The service.
	 */
	protected IFuture<IService> getFreeService(IService service)
	{
		final Future<IService> ret = new Future<IService>();
		
		if(service!=null)
		{
			if(freeservices.contains(service))
			{
				freeservices.remove(service);
				busyservices.add(service);
				ret.setResult(service);
			}
			else
			{
				waitingcalls.add(ret);
			}
		}
		else
		{	
			if(!freeservices.isEmpty())
			{
				IService ser = freeservices.remove(0);
				busyservices.add(ser);
				ret.setResult(ser);
			}
			else
			{
				waitingcalls.add(ret);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void freeService(IService service)
	{
		if(waitingcalls.size()>0)
		{
			Future<IService> wc = waitingcalls.remove(0);
			wc.setResult(service);
		}
		else
		{
			busyservices.remove(service);
			freeservices.add(service);
//			if(searchfin) // should use service directly again or wait for search?!
			checkPerformCall(null);
		}
	}
	
	/**
	 *  Check perform call.
	 */
	public void checkPerformCall(final IService service)
	{
		if(hasArguments())
		{
			getFreeService(service).addResultListener(new IResultListener<IService>()
			{
				public void resultAvailable(IService result)
				{
					try
					{
						Object res = performCall(service, getArguments());
						
						if(res instanceof IFuture)
						{
							((IFuture)res).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result) 
								{
									freeService(service);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									resultAvailable(null);
								}
							});
						}
						else
						{
							freeService(service);
						}
					}
					catch(Exception e)
					{
						freeService(service);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Is not called
				}
			});
		}
		else
		{
			results.setFinished();
		}
	}
}
