package jadex.micro.examples.mandelbrot;

import jadex.bridge.IInternalAccess;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IIntermediateResultListener;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	
	/** The collection of free services. */
	protected Set	free;
	
	/** The collection of busy services. */
	protected Set	busy;
	
	/** The open tasks with their corresponding allocation data. */
	protected Map	tasks;
	
	/** Flag to indicate an ongoing search. */
	protected boolean searching;
	
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
		this.free	= new HashSet();
		this.busy	= new HashSet();
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
		
		// Allocation data binds tasks together to a single result future.
		AllocationData	ad	= new AllocationData(tasks.size(), retry, user);
		
		boolean	allassigned	= true;
		for(Iterator it=tasks.iterator(); it.hasNext(); )
		{
			boolean	assigned	= retryTask(it.next(), ad);
			allassigned	=  allassigned && assigned;
		}
		
		// Search for new services if not all tasks could be assigned.
		if(!allassigned)
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
			Iterator	it	= free.iterator();
			IService	service	= (IService)it.next();
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
		// Find new available service(s).
		if(!searching)
		{
//			System.out.println("searching services");			
			searching	= true;
			component.getRequiredServices(name).addResultListener(
				component.createResultListener(new IIntermediateResultListener()
			{
				/**
				 *  A service has been found.
				 */
				public void intermediateResultAvailable(Object result)
				{
					IService	service	= (IService)result;
					if(!busy.contains(service) && !free.contains(service) && handler.selectService(service))
					{
						addService(service);
					}
				}
				
				/**
				 *  The service search is finished.
				 */
				public void finished()
				{
					searching	= false;
					// Create new services when there are remaining tasks.
					createServices();
				}
				
				/**
				 *  The service search failed.
				 */
				public void exceptionOccurred(Exception exception)
				{
					searching	= false;
					// Shouldn't happen.
					exception.printStackTrace();
					
					// In any case, create new services when there are remaining tasks.
					createServices();
				}
				
				public void resultAvailable(Object result)
				{
					// ignored
				}
			}));
		}
		
		// Todo: create further services already after timeout (if search takes long time due to remote platforms).
	}
	
	/**
	 *  Add a service to the pool and start working on tasks.
	 *  @param service	The service to add.
	 */
	protected void addService(final IService service)
	{
		assert !busy.contains(service) && !free.contains(service); 
		
		if(tasks.isEmpty())
		{
//			System.out.println("service free: "+service.getServiceIdentifier());
			free.add(service);
		}
		else
		{
			busy.add(service);
			final Object task	=	this.tasks.keySet().iterator().next();
			final AllocationData	ad	= (AllocationData)this.tasks.remove(task);
//			System.out.println("started service: "+service.getServiceIdentifier()+", "+task);
			
			handler.invokeService(service, task, ad.getUserData()).addResultListener(
				component.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("service finished: "+service.getServiceIdentifier()+", "+task);
					
					// Add result of task execution.
					ad.taskFinished(result);
					
					// Invoke service again, if there are more tasks.
					busy.remove(service);
					addService(service);
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("service failed: "+service.getServiceIdentifier()+", "+task);
					
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
					busy.remove(service);
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
		if(!tasks.isEmpty() && (max==-1 || free.size()+busy.size()<max))
		{
			handler.createService().addResultListener(component.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("created service: "+((IService)result).getServiceIdentifier());
					
					// Add if not already found by concurrent search.
					if(!busy.contains(result) && !free.contains(result))
						addService((IService)result);
					
					// Create more services, if needed.
					createServices();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Service creation not supported -> ignore.
					exception.printStackTrace();
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
