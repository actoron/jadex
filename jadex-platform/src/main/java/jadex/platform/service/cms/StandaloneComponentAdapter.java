package jadex.platform.service.cms;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DefaultResultListener;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *  Component adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex components without any 3rd party
 *  execution platform.
 */
public class StandaloneComponentAdapter	extends AbstractComponentAdapter	implements IComponentAdapter, IExecutable, Serializable
{
	//-------- attributes --------
	
	/** The execution service (cached for speed). */
	protected IExecutionService	exeservice;
	
	/** The parent adapter (cached for speed). */
	protected StandaloneComponentAdapter	parenta;
	
	/** The synchronous subcomponents that want to be executed (if any). */
	protected Set<StandaloneComponentAdapter>	subcomponents;
	
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public StandaloneComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInstance component, IExternalAccess parent)
	{
		super(desc, model, component, parent);
	}
	
	//-------- AbstractComponentAdapter methods --------
	
	/**
	 *  Execute the component and its synchronous subcomponents (if any).
	 */
	public boolean execute()
	{
//		System.out.println("execute0: "+getComponentIdentifier());
		boolean	ret	= super.execute();

		StandaloneComponentAdapter[]	subs	= null;
		synchronized(this)
		{
			if(subcomponents!=null)
			{
				subs	= subcomponents.toArray(new StandaloneComponentAdapter[subcomponents.size()]);
				subcomponents	= null;
			}
		}
		if(subs!=null)
		{
			for(StandaloneComponentAdapter sub: subs)
			{
//				System.out.println("execute1: "+sub.getComponentIdentifier());
				this.componentthread	= Thread.currentThread();
				boolean	again	= sub.execute();
				this.componentthread	= null;
				if(again)
				{
					addSubcomponent(sub);
				}
				ret	= again || ret;
			}
		}
		
		return ret;
	}

	/**
	 *  Wake up this component.
	 */
	protected void	doWakeup()
	{
//		System.out.println("dowakeup: "+getComponentIdentifier());
		
		if(desc.getSynchronous()!=null && desc.getSynchronous().booleanValue())
		{
			// Add to parent and wake up parent.
			if(parenta==null)
			{
				SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<IComponentManagementService>()
				{
					public void resultAvailable(IComponentManagementService cms)
					{
						cms.getComponentAdapter(getComponentIdentifier().getParent())
							.addResultListener(new DefaultResultListener<IComponentAdapter>()
						{
							public void resultAvailable(IComponentAdapter result)
							{
								parenta	= (StandaloneComponentAdapter)result;
								parenta.addSubcomponent(StandaloneComponentAdapter.this);
								parenta.wakeup();
							}
							
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
						});
					}
				});
			}
			else
			{
				parenta.addSubcomponent(this);
				parenta.wakeup();
			}
		}
		
		else if(exeservice==null)
		{
//			if(getComponentIdentifier().toString().indexOf("Environment")!=-1)
//			{
//				System.out.println("exe1");
//			}
			SServiceProvider.getService(getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener<IExecutionService>()
			{
				public void resultAvailable(IExecutionService result)
				{
					exeservice	= result;
					try
					{
						exeservice.execute(StandaloneComponentAdapter.this);
					}
					catch(RuntimeException e)
					{
//						e.printStackTrace();
						throw new ComponentTerminatedException(getComponentIdentifier());
						// Happens, when execution service shutdown() is called and timer should be registered for result future, but service already terminated
					}
				}
			});
		}
		else
		{
//			if(getComponentIdentifier().toString().indexOf("Environment")!=-1)
//			{
//				System.out.println("exe2");
//			}
			try
			{
				exeservice.execute(StandaloneComponentAdapter.this);
			}
			catch(RuntimeException e)
			{
//				e.printStackTrace();
				throw new ComponentTerminatedException(getComponentIdentifier())
				{
					public void printStackTrace()
					{
//						Thread.dumpStack();
						super.printStackTrace();
					}
				};
				// Happens, when execution service shutdown() is called and timer should be registered for result future, but service already terminated
			}
		}
	}

	/**
	 *  Add a synchronous subcomponent that will run on its parent's thread.
	 */
	protected void	addSubcomponent(StandaloneComponentAdapter sub)
	{
		synchronized(this)
		{
			if(subcomponents==null)
			{
				subcomponents	= new HashSet<StandaloneComponentAdapter>();
			}
			subcomponents.add(sub);
		}
	}

	/**
	 *  Remove a synchronous subcomponent.
	 */
	protected void	removeSubcomponent(StandaloneComponentAdapter sub)
	{
		synchronized(this)
		{
			if(subcomponents!=null)
			{
				subcomponents.remove(sub);
			}
		}
	}

	/**
	 *  Block the current thread and allow execution on other threads.
	 *  @param monitor	The monitor to wait for.
	 */
	public void block(Object monitor, long timeout)
	{
		if(parenta!=null)
		{
			parenta.block(monitor, timeout);
		}
		else
		{
			super.block(monitor, timeout);
		}
	}
	
	/**
	 *  Unblock the thread waiting for the given monitor
	 *  and cease execution on the current thread.
	 *  @param monitor	The monitor to notify.
	 */
	public void unblock(Object monitor, Throwable exception)
	{
		if(parenta!=null)
		{
			parenta.unblock(monitor, exception);
		}
		else
		{
			super.unblock(monitor, exception);
		}
	}
	
	/**
	 *  Clean up this component.
	 */
	public void cleanup()
	{
		super.cleanup();
		if(parenta!=null)
		{
			parenta.removeSubcomponent(this);
		}
	}

}
