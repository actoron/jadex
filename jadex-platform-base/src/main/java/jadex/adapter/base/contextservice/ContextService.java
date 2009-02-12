package jadex.adapter.base.contextservice;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Contexts are an abstract grouping mechanism for agents on a platform,
 *  which is managed using the context service.
 */
public class ContextService	implements IContextService
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;

	/** All contexts on the platform (name->context). */
	protected Map	contexts;
	
	/** The registered context factories (class->factory). */
	protected Map	factories;
	
	/** The context counter for generating unique names. */
	protected int	contextcnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context service.
	 */
	public ContextService(IPlatform platform)
	{
		this.platform = platform;
		
		addContextFactory(IContext.class, new DefaultContextFactory());
		addContextFactory(DefaultContext.class, new DefaultContextFactory());
	}
	
	//-------- IContextService interface --------
	
	/**
	 *  Get all contexts on the platform (if any).
	 */
	public synchronized IContext[]	getContexts()
	{
		return contexts==null ? null :
			(IContext[])contexts.values().toArray(new IContext[contexts.size()]);
	}

	/**
	 *  Get all contexts with a given type (if any).
	 */
	public synchronized IContext[]	getContexts(Class type)
	{
		List	result	= null;
		if(contexts!=null)
		{
			for(Iterator it=contexts.values().iterator(); it.hasNext(); )
			{
				IContext	context	= (IContext) it.next();
				if(SReflect.isSupertype(type, context.getClass()))
				{
					if(result==null)
						result	= new ArrayList();
					result.add(context);
				}
			}
		}
		return result==null ? null :
			(IContext[])result.toArray(new IContext[result.size()]);
	}

	/**
	 *  Get all direct contexts of an agent (if any).
	 */
	public synchronized IContext[]	getContexts(IAgentIdentifier agent)
	{
		List	result	= null;
		if(contexts!=null)
		{
			for(Iterator it=contexts.values().iterator(); it.hasNext(); )
			{
				IContext	context	= (IContext) it.next();
				if(context.containsAgent(agent))
				{
					if(result==null)
						result	= new ArrayList();
					result.add(context);
				}
			}
		}
		return result==null ? null :
			(IContext[])result.toArray(new IContext[result.size()]);		
	}
	
	/**
	 *  Get a context with a given name.
	 */
	public synchronized IContext	getContext(String name)
	{
		return contexts!=null ? (IContext)contexts.get(name) : null;
	}

	/**
	 *  Create a context.
	 *  @param name	The name or null for auto-generation.
	 *  @param type	The context type.
	 *  @param parent The parent context (if any).
	 *  @param properties Initialization properties (if any).
	 */
	public IContext	createContext(String name, Class type, IContext parent, Map properties)
	{
		if(name!=null && contexts!=null && contexts.containsKey(name))
			throw new RuntimeException("Context '"+name+"' already exists on the platform.");
		
		if(contexts==null)
			contexts	= new HashMap();
		
		if(name==null)
		{
			String	basename	= SReflect.getInnerClassName(type) + "_";
			do
			{
				name	= basename + (++contextcnt); 
			}
			while(contexts.containsKey(name));
		}
		
		IContextFactory	factory	= factories==null ? null : (IContextFactory)factories.get(type);
		if(factory==null)
		{
			throw new RuntimeException("No context factory for "+type);
		}
		IContext	context	= factory.createContext(name, parent, properties);
		if(parent!=null)
			((DefaultContext)parent).addSubContext(context);
		
		return context;
	}
	
	/**
	 *  Delete a context.
	 *  @param listener Listener to be called, when the context is deleted
	 *    (e.g. after all contained agents have been terminated).
	 */
	public synchronized void	deleteContext(final IContext context, final IResultListener listener)
	{
		final IAgentIdentifier[]	agents	= context.getAgents();
		if(agents!=null && agents.length>0)
		{
			// Create AMS result listener (l2), when listener is used.
			// -> notifies listener, when last agent is killed.
			IResultListener	l2	= listener!=null ? new IResultListener()
			{
				int tokill	= agents.length;
				Exception	exception;
				
				public void resultAvailable(Object result)
				{
					result();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(this.exception==null)	// Only return first exception.
						this.exception	= exception;
					result();
				}
				
				/**
				 *  Called for each killed agent.
				 *  Decrease counter and notify listener, when last agent is killed.
				 */
				protected void	result()
				{
					tokill--;
					if(tokill==0)
					{
						if(exception!=null)
							listener.exceptionOccurred(exception);
						else
							listener.resultAvailable(context);
					}
				}
			} : null;
			
			// Kill all agents in the context. 
			IAMS	ams	= (IAMS) platform.getService(IAMS.class);
			for(int i=0; i<agents.length; i++)
			{
				ams.destroyAgent(agents[i], l2);
			}
		}
		else
		{
			if(listener!=null)
				listener.resultAvailable(context);
		}
	}
	
	/**
	 *  Register a context factory for a given context type. 
	 */
	public synchronized void	addContextFactory(Class type, IContextFactory factory)
	{
		if(factories==null)
			factories	= new HashMap();
		
		factories.put(type, factory);
	}

	/**
	 *  Deregister a context factory for a given context type. 
	 */
	public synchronized void	removeContextFactory(Class type)
	{
		if(factories!=null)
		{
			factories.remove(type);
			if(factories.isEmpty())
			{
				factories	= null;
			}
		}		
	}
	
	//-------- IPlatformService interface --------

	/**
	 *  Start the service.
	 */
	public void start()
	{
		// Nothing to do here.
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		// Todo: delete contexts and therefore agents before AMS gets shut down???
	}
}
