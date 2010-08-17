package jadex.bridge;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *  Service container for active components.
 */
public class ComponentServiceContainer	extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The cms. */
	protected IComponentManagementService cms;
	
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public ComponentServiceContainer(IComponentAdapter adapter)
	{
		super(adapter.getComponentIdentifier());
		this.adapter = adapter;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		final Future ret = new Future();
		
		ret.setResult(adapter.getParent()!=null ? adapter.getParent().getServiceProvider() : null);
		
		return ret;
	}
	
	/**
	 *  Get the children service containers.
	 *  @return The children containers.
	 */
	public IFuture	getChildren()
	{
		final Future ret = new Future();
		
		adapter.getChildren().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null)
				{
					IComponentIdentifier[] childs = (IComponentIdentifier[])result;
					final IResultListener lis = new CollectionResultListener(
						childs.length, false, new DelegationResultListener(ret));
					for(int i=0; i<childs.length; i++)
					{
						cms.getExternalAccess(childs[i]).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								lis.resultAvailable(null, exta.getServiceProvider());
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
				}
				else
				{
					ret.setResult(Collections.EMPTY_LIST);
				}
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(ComponentServiceContainer.this, IComponentManagementService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				cms = (IComponentManagementService)result;
//				System.out.println("Has cms: "+getId()+" "+cms);
				
				// Services may need other services and thus need to be able to search
				// the container.
				ComponentServiceContainer.super.start().addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ComponentServiceContainer(name="+getId()+")";
	}
}
