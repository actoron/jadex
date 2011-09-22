package jadex.component;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.AbstractInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  The application interpreter provides a closed environment for components.
 *  If components spawn other components, these will automatically be added to
 *  the context.
 *  When the context is deleted all components will be destroyed.
 *  An component must only be in one application context.
 */
public class ComponentInterpreter extends AbstractInterpreter implements IInternalAccess
{
	//-------- attributes --------
	
	/** The scheduled steps of the component. */
	protected List steps;
	
	/** Flag indicating an added step will be executed without the need for calling wakeup(). */
	// Required for startup bug fix in scheduleStep (synchronization between main thread and executor).
	// While main is running the root component steps, invoke later must not be called to prevent double execution.
	protected boolean willdostep;
	
	//-------- constructors --------
	
	/**
	 *  Create a new interpreter.
	 */
	public ComponentInterpreter(final IComponentDescription desc, final IModelInfo model, final String config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, 
		final RequiredServiceBinding[] bindings, boolean copy, final Future<Tuple2<IComponentInstance, IComponentAdapter>> inited)
	{
		super(desc, model, config, factory, parent, bindings, copy, inited);
		this.steps = new ArrayList();
	
		addStep((new Object[]{new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				init(getModel(), ComponentInterpreter.this.config, arguments)
					.addResultListener(createResultListener(new DelegationResultListener(inited)
				{
					public void customResultAvailable(Object result)
					{
						inited.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(ComponentInterpreter.this, adapter));
					}
					public void exceptionOccurred(Exception exception)
					{
						// Hack!!! May be set already when component terminated during init.
						if(!future.isDone())
							super.exceptionOccurred(exception);
					}
				}));
				
				return IFuture.DONE;
			}
		}, new Future()}));
	}

	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public IFuture scheduleStep(final IComponentStep step)
	{
		final Future ret = new Future();
//		System.out.println("ss: "+getComponentIdentifier()+" "+Thread.currentThread()+" "+step);
		try
		{
			if(adapter.isExternalThread())
			{
				adapter.invokeLater(new Runnable()
				{			
					public void run()
					{
//						System.out.println("as1: "+getComponentIdentifier()+" "+Thread.currentThread()+" "+step);
						addStep(new Object[]{step, ret});
					}
					
					public String toString()
					{
						return "invokeLater("+step+")";
					}
				});
			}
			else
			{
//				System.out.println("as2: "+getComponentIdentifier()+" "+Thread.currentThread()+" "+step);
				addStep(new Object[]{step, ret});
			}
		}
		catch(Exception e)
		{
//			System.out.println("as3: "+getComponentIdentifier()+" "+Thread.currentThread()+" "+step+", "+e);
			ret.setException(e);
		}
		return ret;
	}

	/**
	 *  Add a new step.
	 */
	protected void addStep(Object[] step)
	{
//		if(nosteps)
//		{
//			((Future)step[1]).setException(new ComponentTerminatedException(getComponentAdapter().getComponentIdentifier()));
//		}
//		else
//		{
			steps.add(step);
//			notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_STEP, step[0].getClass().getName(), 
//				step[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)step[0])));
//		}
	}
	
//	/**
//	 *  Add a new step.
//	 */
//	protected Object[] removeStep()
//	{
//		Object[] ret = (Object[])steps.remove(0);
//		notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_STEP, 
//			ret[0].getClass().getName(), ret[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)ret[0])));
////		notifyListeners(new ChangeEvent(this, "removeStep", new Integer(0)));
//		return ret;
//	}
	
	//-------- methods to be called by adapter --------
	
	/**
	 *  Can be called on the component thread only.
	 * 
	 *  Main method to perform component execution.
	 *  Whenever this method is called, the component performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for components
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep()
	{
		try
		{
			Object[] step	= null;
			synchronized(steps)
			{
				if(!steps.isEmpty())
				{
					step = (Object[])steps.remove(0);
				}
			}

			if(step!=null)
			{
				Future future = (Future)step[1];
				try
				{
					Object res = ((IComponentStep)step[0]).execute(this);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(new DelegationResultListener(future));
					}
					else
					{
						future.setResult(res);
					}
				}
				catch(RuntimeException e)
				{
//					e.printStackTrace();
					future.setExceptionIfUndone(e);
					throw e;
				}
			}
			
			boolean ret;
			synchronized(steps)
			{
				ret = !steps.isEmpty();
				willdostep	= ret;
			}
			return ret;
		}
		catch(ComponentTerminatedException ate)
		{
			// Todo: fix kernel bug.
			ate.printStackTrace();
			return false; 
		}
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the component that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		// Todo: application breakpoints!?
		return false;
	}
	
//	/**
//	 *  Create the service container.
//	 *  @return The service container.
//	 */
//	public IServiceContainer getServiceContainer()
//	{
//		if(container==null)
//		{
//			// Init service container.
////			MExpressionType mex = model.getContainer();
////			if(mex!=null)
////			{
////				container = (IServiceContainer)mex.getParsedValue().getValue(fetcher);
////			}
////			else
////			{
////				container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
//				
//				RequiredServiceInfo[] ms = getModel().getRequiredServices();
//				
//				Map sermap = new LinkedHashMap();
//				for(int i=0; i<ms.length; i++)
//				{
//					sermap.put(ms[i].getName(), ms[i]);
//				}
//	
//				if(getConfiguration()!=null)
//				{
//					ConfigurationInfo cinfo = getModel().getConfiguration(getConfiguration());
//					RequiredServiceInfo[] cs = cinfo.getRequiredServices();
//					for(int i=0; i<cs.length; i++)
//					{
//						RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(cs[i].getName());
//						RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType(), rsi.isMultiple(), 
//							new RequiredServiceBinding(cs[i].getDefaultBinding()));
//						sermap.put(newrsi.getName(), newrsi);
//					}
//				}
//				
//				container = new ComponentServiceContainer(getComponentAdapter(), ComponentComponentFactory.FILETYPE_COMPONENT, 
//					(RequiredServiceInfo[])sermap.values().toArray(new RequiredServiceInfo[sermap.size()]), bindings);
////			}			
//		}
//		return container;
//	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitFor(final long delay, final IComponentStep<T> step)
	{
		// todo: remember and cleanup timers in case of component removal.
		
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	//-------- abstract interpreter methods --------
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return this;
	}
}
