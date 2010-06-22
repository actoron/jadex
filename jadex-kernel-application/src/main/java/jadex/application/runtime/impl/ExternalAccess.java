package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.IComponentAdapter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.service.BasicServiceProvider;

/**
 *  External access for applications.
 */
public class ExternalAccess extends BasicServiceProvider implements IApplicationExternalAccess
{
	//-------- attributes --------

	/** The agent. */
	protected Application application;

	/** The agent adapter. */
	protected IComponentAdapter adapter;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(Application application)
	{
		this.application = application;
		this.adapter = application.getComponentAdapter();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public IFuture getModel()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(application.getModel());
				}
			});
		}
		else
		{
			ret.setResult(application.getModel());
		}
		
		return ret;
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IFuture getComponentIdentifier()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(adapter.getComponentIdentifier());
				}
			});
		}
		else
		{
			ret.setResult(adapter.getComponentIdentifier());
		}
		
		return ret;
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IFuture getSpace(final String name)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(application.getSpace(name));
				}
			});
		}
		else
		{
			ret.setResult(application.getSpace(name));
		}
		
		return ret;
	}
	
	/**
	 *  Get the parent.
	 */
	public IFuture getParent()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(application.getParent());
				}
			});
		}
		else
		{
			ret.setResult(application.getParent());
		}
		
		return ret;
	}
}
