package jadex.micro.examples.mandelbrot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 *  Generic class that allows dispatching tasks to a dynamic pool of services.
 */
public class ServicePoolManager
{
	//-------- attributes --------
	
	/** The component, which manages the pool. */
	protected IInternalAccess	component;
	
	/** The services name. */
	protected String	name;
	
	/** The handler for service creation, selection and invocation. */
	protected IServicePoolHandler	handler;
	
	/** The maximum number of services (-1 for unlimited). */
	protected int	max;
	
	/** The collection of free services (id->service). */
	protected Map	free;
	
	/** The collection of busy services (id->service). */
	protected Map	busy;
	
	/** The open tasks with their corresponding allocation data. */
	protected Map	tasks;
	
	/** Flag to indicate an ongoing search. */
	protected boolean searching;
	
	/** Flag to indicate an ongoing creation. */
	protected boolean creating;
	
	/** The search timeout timer. */
	protected ITimer	timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service pool manager.
	 *  @param name	The services name.
	 *  @param handler	The code for service invocation.
	 */
	public ServicePoolManager(IInternalAccess component, String name, IServicePoolHandler handler, int max)
	{
		this.component	= component;
		this.name	= name;
		this.handler	= handler;
		this.max	= max;
		this.free	= new HashMap();
		this.busy	= new HashMap();
		this.tasks	= new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Perform the given tasks using available or newly created services.
	 *  @param tasks	The set of tasks to be performed.
	 *  @param retry	True, when failed tasks should be retried.
	 *  @param user	User data that is provided for service selection, creation, invocation (if any).
	 *  @return	A future with intermediate and final results. 
	 */
	public IIntermediateFuture	performTasks(Set tasks, boolean retry, Object user)
	{
//		System.out.println("Peforming "+tasks.size()+" tasks");
//		System.out.println("Performing tasks: busy="+busy.size()+", free="+free.size());
		
		// Allocation data binds tasks together to a single result future.
		AllocationData	ad	= new AllocationData(tasks.size(), retry, user);
		
		boolean	allassigned	= true;
		for(Iterator it=tasks.iterator(); it.hasNext(); )
		{
			boolean	assigned	= retryTask(it.next(), ad);
			allassigned	=  allassigned && assigned;
		}
		
		// Search for new services if not all tasks could be assigned.
		if(!allassigned && busy.size()+free.size()<max)
			searchServices();
	
		return ad.getResult();
	}
	
	/**
	 *  Set the maximum number of services.
	 *  Only affects the creation of new services.
	 */
	public void	setMax(int max)
	{
		this.max	= max;
	}
	
	// -------- helper methods --------
	
	/**
	 *  (Re-)start working on a task.
	 *  @param task	The task.
	 *  @param ad	The allocation data.
	 *  @return	True, when the task was assigned to an available service.
	 */
	protected boolean	retryTask(Object task, AllocationData ad)
	{
		boolean	assigned	= false;
		tasks.put(task, ad);
		
		// Assign task to an available service, if any.
		if(!free.isEmpty())
		{
			Iterator	it	= free.values().iterator();
			IService	service	= (IService)it.next();
//			System.out.println("retry: "+service);
			it.remove();
			// Re-adding services makes them look for new tasks.
			addService(service);
			assigned	= true;
		}
		return assigned;
	}
	
	/**
	 *  Search for services or create new services as needed.
	 */
	protected void	searchServices()
	{
		// Start timer to be triggered when search is not finished after 1 second.
		if(timer==null)
		{
			component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					IClockService	cs	= (IClockService)result;
					if(timer==null)
					{
						timer	= cs.createTimer(1000, new ITimedObject()
						{
							public void timeEventOccurred(long currenttime)
							{
								component.getExternalAccess().scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										timer	= null;
										// Create new services when there are remaining tasks.
										createServices();
										return IFuture.DONE;
									}
									
									public String toString()
									{
										return "Search timeout for: "+name;
									}
								});
							}
						});
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// No timeout supported; ignore
				}
			});
		}
		
		// Find new available service(s).
		if(!searching)
		{
//			System.out.println("searching services");			
			searching	= true;
			
//			System.out.println("wurksn0");
			component.getFeature(IRequiredServicesFeature.class).getServices(name).addResultListener(
				new IIntermediateResultListener<Object>()
			{
				/**
				 *  A service has been found.
				 */
				public void intermediateResultAvailable(Object result)
				{
//					System.out.println("wurksn3");
					IService	service	= (IService)result;
					if(!busy.containsKey(service.getServiceId()) && !free.containsKey(service.getServiceId()) && handler.selectService(service))
					{
						addService(service);
					}
				}
				
				/**
				 *  The service search is finished.
				 */
				public void finished()
				{
//					System.out.println("Search finished: busy="+busy.size()+", free="+free.size());
//					System.out.println("wurksn2");
					searching	= false;					
					if(timer!=null)
					{
						timer.cancel();
						timer	= null;
					}
					
					// Create new services when there are remaining tasks.
					createServices();
				}
				
				/**
				 *  The service search failed.
				 */
				public void exceptionOccurred(Exception exception)
				{
//					if(!(exception instanceof ServiceNotFoundException))
//						System.out.println("!snfe: "+exception);
//					
//					System.out.println("wurksn1: "+exception);
					searching	= false;					
					if(timer!=null)
					{
						timer.cancel();
						timer	= null;
					}
					
					// If component still active, create new services when there are remaining tasks.
					// Has to ensure that ComponentTerminatedException does not belong to underlying component.
					if(!(exception instanceof ComponentTerminatedException && 
						((ComponentTerminatedException)exception).getComponentIdentifier().equals(component.getId())))
					{
						createServices();
					}
//					else
//					{
//						component.getLogger().warning("Service error and cannot create new one: "+exception);
//					}
				}
				
				public void resultAvailable(Collection result)
				{
					searching	= false;		
					// ignored
//					System.out.println("wurksnrr");
				}
			});
		}
	}
	
	/**
	 *  Add a service to the pool and start working on tasks.
	 *  @param service	The service to add.
	 */
	protected void addService(final IService service)
	{
		assert !busy.containsKey(service.getServiceId()) && !free.containsKey(service.getServiceId()); 
		
		if(tasks.isEmpty())
		{
//			System.out.println("service free: "+service.getId());
			free.put(service.getServiceId(), service);
		}
		else
		{
			busy.put(service.getServiceId(), service);
			final Object task	=	this.tasks.keySet().iterator().next();
			final AllocationData	ad	= (AllocationData)this.tasks.remove(task);
//			System.out.println("started service: "+service.getId()+", "+task);
			
			handler.invokeService(service, task, ad.getUserData()).addResultListener(
				component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("service finished: "+service.getId()+", "+task);
					
					// Add result of task execution.
					ad.taskFinished(result);
					
					// Invoke service again, if there are more tasks.
					busy.remove(service.getServiceId());
					addService(service);
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("service failed: "+service.getId()+", "+task);
					
					if(ad.isRetry())
					{
//						System.out.println("Retry due to "+exception);
						boolean	assigned	= retryTask(task, ad);
						if(!assigned)
							searchServices();
					}
					else
					{
						ad.taskFailed(exception);
					}
					
					// Remove service on failure and do not continue working on tasks with it.
					busy.remove(service.getServiceId());
				}
			}));
		}
	}
	
	/**
	 *  Create services until there are no more todo items.
	 *  @param todo	The collection of remaining tasks.
	 */
	protected void	createServices()
	{
		if(timer==null && !creating && !tasks.isEmpty() && (max==-1 || free.size()+busy.size()<max))
		{
			creating	= true;
			handler.createService().addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					creating	= false;
//					System.out.println("created service: "+((IService)result).getId());
					
					// Add if not already found by concurrent search.
					IService	service	= (IService)result;
					if(!busy.containsKey(service.getServiceId()) && !free.containsKey(service.getServiceId()))
						addService((IService)result);
					
					// Create more services, if needed.
					createServices();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					creating	= false;
					// Service creation not supported -> ignore.
//					if(!(exception instanceof ComponentTerminatedException))
//					{
//						exception.printStackTrace();
//					}
				}
			}));
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Handler for a single task allocation.
	 */
	public static class	AllocationData
	{
		//-------- attributes --------
		
		/** The counter for open tasks, i.e. number of assigned and unassigned tasks that are not yet finished. */
		protected int	open;
		
		/** The retry flag, i.e. if failed tasks should be assigned again. */
		protected boolean	retry;
		
		/** The user data (if any). */
		protected Object	user;
		
		/** The result future. */
		protected IntermediateFuture	result;
		
		//-------- constructors --------
		
		/**
		 *  Create a new allocation data.
		 */
		public AllocationData(int open, boolean retry, Object user)
		{
			this.open	= open;
			this.retry	= retry;
			this.user	= user;
			this.result	= new IntermediateFuture();
		}
		
		//-------- methods --------
		
		/**
		 *  Get the user data.
		 *  @return The user data (if any).
		 */
		public Object	getUserData()
		{
			return user;
		}
		
		/**
		 *  Get the result.
		 *  @return The intermediate results future.
		 */
		public IntermediateFuture	getResult()
		{
			return result;
		}
				
		/**
		 *  Add an intermediate result.
		 */
		public void	taskFinished(Object result)
		{
			this.result.addIntermediateResult(result);
			open--;
			
			if(open==0)
			{
				this.result.setFinished();
			}
		}
		
		/**
		 *  A task has failed and is not retried.
		 */
		public void	taskFailed(Exception e)
		{
			// Todo: post exception in result?
			e.printStackTrace();
			
			open--;
			if(open==0)
			{
				this.result.setFinished();
			}
		}
		
		/**
		 *  Test if the retry flag is set.
		 *  @return True, if the retry flag is set.
		 */
		public boolean	isRetry()
		{
			return retry;
		}
	}
}
