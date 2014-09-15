package jadex.bridge.component.impl;

import jadex.base.Starter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.LinkedList;
import java.util.List;

/**
 *  This feature provides component step execution.
 */
public class ExecutionComponentFeature	extends	AbstractComponentFeature	implements IExecutionFeature, IExecutable
{
	//-------- attributes --------
	
	/** The component steps. */
	protected List<Tuple2<IComponentStep<?>, Future<?>>>	steps;
	
	/** The immediate component steps. */
	protected List<Tuple2<IComponentStep<?>, Future<?>>>	isteps;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ExecutionComponentFeature()
	{
	}
	
	protected ExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IComponentFeature interface --------
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IExecutionFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 */
	public IComponentFeature createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new ExecutionComponentFeature(access, info);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void>	body()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		return IFuture.DONE;
	}
	
	//-------- IExecutionFeature interface --------
	
	/**
	 *  Execute a component step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		Future<T>	ret	= new Future<T>();
		synchronized(this)
		{
			if(steps==null)
			{
				steps	= new LinkedList<Tuple2<IComponentStep<?>,Future<?>>>();
			}
			steps.add(new Tuple2<IComponentStep<?>, Future<?>>(step, ret));
		}

		wakeup();
		
		return ret;
	}
	
	/**
	 *  Execute an immediate component step,
	 *  i.e., the step is executed also when the component is currently suspended.
	 */
	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step)
	{
		Future<T>	ret	= new Future<T>();
		synchronized(this)
		{
			if(isteps==null)
			{
				isteps	= new LinkedList<Tuple2<IComponentStep<?>,Future<?>>>();
			}
			isteps.add(new Tuple2<IComponentStep<?>, Future<?>>(step, ret));
		}

		wakeup();
		
		return ret;
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime)
	{
		// Todo:
		return new Future<T>();
	}

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
	{
		// Todo:
		return new Future<T>();
	}

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay, boolean realtime)
	{
		// Todo:
		return new Future<Void>();
	}
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay)
	{
		// Todo:
		return new Future<Void>();
	}
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);
	
	/** Flag to indicate bootstrapping execution of main thread (only for platform, hack???). */
	protected volatile boolean bootstrap;
	
	/** Flag to indicate that the execution service has become available during bootstrapping (only for platform, hack???). */
	protected volatile boolean available;
	
	/**
	 *  Trigger component execution.
	 */
	protected void	wakeup()
	{
		SServiceProvider.getService(component, IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IExecutionService>()
		{
			public void resultAvailable(IExecutionService exe)
			{
				// Hack!!! service is foudn before it is started, grrr.
				if(((IService)exe).isValid().get(null).booleanValue())	// Hack!!! service is raw
				{
					if(bootstrap)
					{
						// Execution service found during bootstrapping execution -> stop bootstrapping as soon as possible.
						available	= true;
					}
					else
					{
						exe.execute(ExecutionComponentFeature.this);
					}
				}
				else
				{
					exceptionOccurred(null);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Happens during platform bootstrapping -> execute on platform rescue thread.
				if(!bootstrap)
				{
					bootstrap	= true;
					Starter.scheduleRescueStep(getComponent().getComponentIdentifier().getRoot(), new Runnable()
					{
						public void run()
						{
							boolean	again	= true;
							while(!available && again)
							{
								again	= execute();
							}
							bootstrap	= false;
							
							if(again)
							{					
								// Bootstrapping finished -> do real kickoff
								wakeup();
							}
						}
					});
				}
			}
		});
	}

	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread()
	{
		// Todo
		return true;
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return new ComponentResultListener<T>(listener, component);
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
	{
		// Todo
		return null;
	}
	
	//-------- IExecutable interface --------
	
	/**
	 *  Execute the executable.
	 *  @return True, if the object wants to be executed again.
	 */
	public boolean execute()
	{
		Tuple2<IComponentStep<?>, Future<?>>	step	= null;
		synchronized(this)
		{
			if(isteps!=null)
			{
				step	= isteps.remove(0);
				if(isteps.isEmpty())
				{
					isteps	= null;
				}
			}
			else if(steps!=null)
			{
				step	= steps.remove(0);
				if(steps.isEmpty())
				{
					steps	= null;
				}
			}
		}
		
		boolean	again;
		
		if(step!=null)
		{
			step.getFirstEntity().execute(component)
				.addResultListener(new DelegationResultListener(step.getSecondEntity()));
			
			synchronized(this)
			{
				again	= isteps!=null || steps!=null;
			}
		}
		else
		{
			again	= false;
		}
		
		return again;
	}
}
