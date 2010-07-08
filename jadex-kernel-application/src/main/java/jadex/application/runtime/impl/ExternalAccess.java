package jadex.application.runtime.impl;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.service.IServiceProvider;

import java.util.Set;

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
	
	/** The provider name. */
	protected String providername;
	
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
		this.providername = application.getServiceProvider().getName();
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
	 *  Get the first declared service of a given type.
	 *  @param type The type.
	 *  @return The corresponding service.
	 */
	public IFuture getService(final Class type)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.getServiceProvider().getService(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().getService(type).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get a service.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(final Class type)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.getServiceProvider().getServices(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().getServices(type).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get a service.
	 *  @param name The name.
	 *  @return The corresponding service.
	 * /
	public IFuture getService(final Class type, final String name)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(application.getServiceProvider().getService(type, name));
				}
			});
		}
		else
		{
			ret.setResult(application.getServiceProvider().getService(type, name));
		}
		
		return ret;
	}*/
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.getServiceProvider().getServicesTypes().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().getServicesTypes().addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	// todo: remove me?
	/**
	 *  Get all services for a type.
	 *  @param type The type.
	 */
	public IFuture getServiceOfType(final Class type, final Set visited)
	{
//		System.out.println("gSoT: "+application+", "+type+", "+visited);
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
//					System.out.println("gSoT.iL: "+application+", "+type+", "+visited);
					application.getServiceProvider().getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
//			System.out.println("gSoT.d: "+application+", "+type+", "+visited);
			application.getServiceProvider().getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	// todo: remove me?
	/**
	 *  Get all services for a type.
	 *  @param type The type.
	 */
	public IFuture getServicesOfType(final Class type, final Set visited)
	{
//		final Exception e = new Exception();
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
//					e.printStackTrace();
					application.getServiceProvider().getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get the service provider name.
	 *  @return The name.
	 */
	public String getName()
	{
		return providername;
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture addService(final Class type, final Object service)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.getServiceProvider().addService(type, service).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().addService(type, service).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture removeService(final Class type, final Object service)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					application.getServiceProvider().removeService(type, service).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			application.getServiceProvider().removeService(type, service).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
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
