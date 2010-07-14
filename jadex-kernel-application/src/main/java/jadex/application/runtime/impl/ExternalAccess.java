package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.runtime.ISpace;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.service.IServiceProvider;

/**
 *  External access for applications.
 */
public class ExternalAccess implements IApplicationExternalAccess
{
	//-------- attributes --------

	/** The agent. */
	protected Application application;

	/** The agent adapter. */
	protected IComponentAdapter adapter;
	
	/** The toString value. */
	protected String tostring;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(Application application)
	{
		this.application = application;
		this.adapter = application.getComponentAdapter();
		this.tostring = application.getComponentIdentifier().getLocalName();
		this.provider = application.getServiceProvider();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public ILoadableComponentModel getModel()
	{
		return application.getModel();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public ISpace getSpace(final String name)
	{
		// Application getSpace() is synchronized
		return application.getSpace(name);
		
//		final Future ret = new Future();
//		
//		if(adapter.isExternalThread())
//		{
//			adapter.invokeLater(new Runnable() 
//			{
//				public void run() 
//				{
//					ret.setResult(application.getSpace(name));
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(application.getSpace(name));
//		}
//		
//		return ret;
	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return application.getParent();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return application.getChildren();
	}
	
	/**
	 *  Get the application component.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}

	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.killComponent().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.killComponent().addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}

}
