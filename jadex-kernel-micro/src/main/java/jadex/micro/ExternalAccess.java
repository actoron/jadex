package jadex.micro;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
<<<<<<< .mine
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
=======
import jadex.commons.concurrent.DefaultResultListener;
>>>>>>> .r2090
import jadex.service.BasicServiceContainer;
import jadex.service.IServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * External access interface.
 */
public class ExternalAccess implements IMicroExternalAccess 
{
	// -------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
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
	public ExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		super(interpreter.getAgentAdapter().getComponentIdentifier().getLocalName());
		this.agent = agent;
		this.interpreter = interpreter;
		this.adapter = interpreter.getAgentAdapter();
		this.provider = interpreter.getServiceProvider();
	}

	// -------- eventbase shortcut methods --------

	/**
	 *  Send a message.
	 * 
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public void sendMessage(final Map me, final MessageType mt)
	{
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				agent.sendMessage(me, mt);
				// System.out.println("Send message: "+rme);
			}
		});
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public void	scheduleStep(Runnable step)
	{
		interpreter.scheduleStep(step);
	}

	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IFuture getAgent()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(agent);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the model of the component.
	 */
	public ILoadableComponentModel	getModel()
	{
		return interpreter.getAgentModel();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}
	
	/**
	 *  Get the parent component.
	 *  @return The parent component.
	 */
	public IExternalAccess	getParent()
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
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return this.interpreter;
	}
	
	/**
	 *  Get the first declared service of a given type.
	 *  @param type The type.
	 *  @return The corresponding service.
	 */
	public IFuture getService(final Class type)
	{
<<<<<<< .mine
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					provider.getService(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.getService(type).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
=======
		final Future ret = new Future();
		
		getService(IComponentManagementService.class).addResultListener(new ComponentResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentIdentifier[] childs = cms.getChildren(getComponentIdentifier());
				List res = new ArrayList();
				for(int i=0; i<childs.length; i++)
				{
					IExternalAccess ex = (IExternalAccess)cms.getExternalAccess(childs[i]);
					res.add(ex);
				}
				ret.setResult(res);
			}
		}, adapter));
		
		return ret;
>>>>>>> .r2090
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
					provider.getServices(type).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.getServices(type).addResultListener(new DelegationResultListener(ret));
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
					provider.getServicesTypes().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.getServicesTypes().addResultListener(new DelegationResultListener(ret));
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
					provider.getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
//			System.out.println("gSoT.d: "+application+", "+type+", "+visited);
			provider.getServiceOfType(type, visited).addResultListener(new DelegationResultListener(ret));
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
					provider.getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			provider.getServicesOfType(type, visited).addResultListener(new DelegationResultListener(ret));
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

}
