package jadex.standalone;

import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.execution.IExecutionService;

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
		SServiceProvider.getService(getServiceContainer(), IExecutionService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				try
				{
//					if(getComponentIdentifier().toString().indexOf("@")==-1)
//						new RuntimeException("doWakeup: "+getComponentIdentifier()).printStackTrace();
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
