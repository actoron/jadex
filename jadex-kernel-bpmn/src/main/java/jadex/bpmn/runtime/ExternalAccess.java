package jadex.bpmn.runtime;

import java.util.Set;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.service.IServiceProvider;

/**
 *  External access for bpmn components.
 */
public class ExternalAccess implements IExternalAccess
{
	//-------- attributes --------

	/** The agent. */
	protected BpmnInterpreter interpreter;

	/** The agent adapter. */
	protected IComponentAdapter adapter;

	/** The provider. */
	protected IServiceProvider provider;
	
	/** The provider name. */
	protected String providername;
	
	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(BpmnInterpreter interpreter)
	{
		this.interpreter = interpreter;
		this.adapter = interpreter.getComponentAdapter();
		this.provider = interpreter.getServiceProvider();
		this.providername = interpreter.getServiceProvider().getName();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public ILoadableComponentModel	getModel()
	{
		return interpreter.getModel();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return interpreter.getParent();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return interpreter.getChildren();
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
					interpreter.getServiceProvider().getService(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.getServiceProvider().getService(type).addResultListener(new DelegationResultListener(ret));
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
					interpreter.getServiceProvider().getServices(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.getServiceProvider().getServices(type).addResultListener(new DelegationResultListener(ret));
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
					interpreter.getServiceProvider().getServicesTypes().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.getServiceProvider().getServicesTypes().addResultListener(new DelegationResultListener(ret));
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
					interpreter.getServiceProvider().getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
//			System.out.println("gSoT.d: "+application+", "+type+", "+visited);
			interpreter.getServiceProvider().getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
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
					interpreter.getServiceProvider().getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.getServiceProvider().getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
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
					provider.addService(type, service).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.addService(type, service).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
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
					provider.removeService(type, service).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.removeService(type, service).addResultListener(new DelegationResultListener(ret));
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
					interpreter.killComponent().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.killComponent().addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 * /
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}*/
}
