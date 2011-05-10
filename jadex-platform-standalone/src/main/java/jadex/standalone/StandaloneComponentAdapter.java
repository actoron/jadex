package jadex.standalone;

import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.execution.IExecutionService;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DefaultResultListener;

import java.io.Serializable;

/**
 *  Component adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex components without any 3rd party
 *  execution platform.
 */
public class StandaloneComponentAdapter	extends AbstractComponentAdapter	implements IComponentAdapter, IExecutable, Serializable
{
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
		SServiceProvider.getService(getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				try
				{
					if(getComponentIdentifier().toString().indexOf("@")==-1)
					{
//						System.err.println("doWakeup: "+getComponentIdentifier());
//						Thread.dumpStack();
					}
					((IExecutionService)result).execute(StandaloneComponentAdapter.this);
				}
				catch(RuntimeException e)
				{
					// ignore if service is shutting down.
					e.printStackTrace();
				}
			}
		});
	}
}
