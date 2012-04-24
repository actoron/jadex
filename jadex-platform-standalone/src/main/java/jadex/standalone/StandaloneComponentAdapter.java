package jadex.standalone;

import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.Serializable;

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
	protected IFuture	exeservice;
	
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
	 *  Wake up this component.
	 */
	protected void	doWakeup()
	{
		getExecutionService().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				try
				{
//					if(getComponentIdentifier().toString().indexOf("@")==-1)
//					{
//						System.err.println("doWakeup: "+getComponentIdentifier());
//						Thread.dumpStack();
//					}
					((IExecutionService)result).execute(StandaloneComponentAdapter.this);
				}
				catch(RuntimeException e)
				{
					logger.warning("Wakeup failed, execution service is shutting down: "+getComponentIdentifier());
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 *  Get the execution service for waking the agent.
	 */
	protected IFuture	getExecutionService()
	{
		if(exeservice==null)
		{
			exeservice	= SServiceProvider.getService(getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		}
		return exeservice;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Void> killComponent()
	{
		Future<Void>	ret	= new Future<Void>();
		super.killComponent().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				exeservice	= null;
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
}
