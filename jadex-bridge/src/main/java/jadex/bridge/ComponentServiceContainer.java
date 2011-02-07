package jadex.bridge;

import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.BasicServiceContainer;
import jadex.commons.service.SServiceProvider;

import java.util.Collections;

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
	
	/** The component type. */
	protected String type;
	
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public ComponentServiceContainer(IComponentAdapter adapter, String type)
	{
		super(adapter.getComponentIdentifier());
		this.adapter = adapter;
		this.type	= type;
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
		
		adapter.getChildrenIdentifiers().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result!=null)
				{
					IComponentIdentifier[] childs = (IComponentIdentifier[])result;
					final IResultListener lis = new CollectionResultListener(
						childs.length, true, new DelegationResultListener(ret));
					for(int i=0; i<childs.length; i++)
					{
						cms.getExternalAccess(childs[i]).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								lis.resultAvailable(exta.getServiceProvider());
							}
							
							public void exceptionOccurred(Exception exception)
							{
								lis.exceptionOccurred(exception);
							}
						});
					}
				}
				else
				{
					ret.setResult(Collections.EMPTY_LIST);
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return type;
	}	
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(ComponentServiceContainer.this, IComponentManagementService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				cms = (IComponentManagementService)result;
//				System.out.println("Has cms: "+getId()+" "+cms);

				// Services may need other services and thus need to be able to search
				// the container.
				ComponentServiceContainer.super.start().addResultListener(new DelegationResultListener(ret));
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
