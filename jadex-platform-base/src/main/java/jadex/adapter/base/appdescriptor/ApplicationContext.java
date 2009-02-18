package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.base.contextservice.IContext;
import jadex.adapter.base.contextservice.IContextService;
import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  The application context provides a closed environment for agents.
 *  If agents spawn other agents, these will automatically be added to
 *  the context.
 *  When the context is deleted all agents will be destroyed.
 *  An agent must only be in one application context.
 */
public class ApplicationContext	extends BaseContext
{
	//-------- constants --------
	
	/** The application type property (required for context creation). */
	public static final String	PROPERTY_APPLICATION_TYPE	= "application-type";
	
	/** The master flag. */
	public static final String PROPERTY_AGENT_MASTER = "master";
	
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform	platform;
	
	/** The application type. */
	protected ApplicationType	apptype;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more agents can be added). */
	protected boolean	terminating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ApplicationContext(String name, IContext parent, Map properties, IPlatform platform)
	{
		super(name, parent, properties);
		this.platform	= platform;
		this.apptype	= properties!=null ? (ApplicationType)properties.get(PROPERTY_APPLICATION_TYPE) : null;
		if(apptype==null)
			throw new RuntimeException("Property '"+PROPERTY_APPLICATION_TYPE+"' required.");
	}

	//-------- IContext interface --------
		
	/**
	 *  Add an agent to a context.
	 */
	public synchronized void	addAgent(IAgentIdentifier agent)
	{
		if(isTerminating())
			throw new RuntimeException("Cannot add agent to terminating context: "+agent+", "+this);
		
		IContextService	cs	= (IContextService) platform.getService(IContextService.class);
		IContext[]	appcontexts	= cs.getContexts(agent, ApplicationContext.class);
		if(appcontexts!=null && appcontexts.length>0)
			throw new RuntimeException("Cannot add agent to "+this+". Agent already belongs to "+appcontexts[0]);

		super.addAgent(agent);
	}
		
	//-------- BaseContext template methods --------
	
	/**
	 *  Called by AMS when an agent has been created by another agent in this context.
	 *  @param creator	The creator of the new agent.
	 *  @param newagent	The newly created agent.
	 */
	public void	agentCreated(IAgentIdentifier creator, IAgentIdentifier newagent)
	{
		addAgent(newagent);
	}

	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Application context behavior is to destroy
	 *  contained agents.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(final IResultListener listener)
	{
		this.setTerminating(true);
		final IAgentIdentifier[]	agents	= getAgents();
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
							listener.resultAvailable(ApplicationContext.this);
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
				listener.resultAvailable(this);
		}

		System.out.println("Deleted: "+this);
	}

	//-------- methods --------
	
	/**
	 *  Get the applicattion type.
	 */
	public ApplicationType	getApplicationType()
	{
		return apptype;
	}
	
	/**
	 *  Create an agent in the context.
	 *  @param name	The name of the newly created agent.
	 *  @param type	The agent type as defined in the application type.
	 *  @param configuration	The agent configuration.
	 *  @param arguments	Arguments for the new agent.
	 *  @param start	Should the new agent be started?
	 *  
	 *  @param istener	A listener to be notified, when the agent is created (if any).
	 *  @param creator	The agent that wants to create a new agent (if any).	
	 */
	public void createAgent(String name, String type, String configuration,
			Map arguments, final boolean start, final boolean master, 
			final IResultListener listener, IAgentIdentifier creator)
	{
		AgentType	at	= apptype.getAgentType(type);
		if(at==null)
			throw new RuntimeException("Unknown agent type '"+type+"' in application: "+apptype);
		final IAMS	ams	= (IAMS) platform.getService(IAMS.class);
		ams.createAgent(name, at.getFilename(), configuration, arguments, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				if(listener!=null)
					listener.exceptionOccurred(exception);
			}
			public void resultAvailable(Object result)
			{
				IAgentIdentifier aid = (IAgentIdentifier)result;
				addAgent(aid);
				if(master)
				{
					addProperty(aid, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
				}
				if(start)
				{
					ams.startAgent(aid, listener);
				}
				else
				{
					if(listener!=null)
						listener.resultAvailable(result);
				}
			}
		}, creator);
	}
	
	/**
	 *  Remove an agent from a context.
	 */
	public synchronized void removeAgent(IAgentIdentifier agent)
	{
		boolean master = isAgentMaster(agent);
			
		super.removeAgent(agent);
		
		if(master)
			((IContextService)platform.getService(IContextService.class)).deleteContext(this, null);
	}

	/**
	 *  Get the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	public boolean	isTerminating()
	{
		return this.terminating;
	}

	/**
	 *  Set the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	protected void setTerminating(boolean terminating)
	{
		if(!terminating || this.terminating)
			throw new RuntimeException("Cannot terminate; illegal state: "+this.terminating+", "+terminating);
			
		this.terminating	= terminating;
	}

	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @param master The master.
	 */
	public synchronized void setAgentMaster(IAgentIdentifier agent, boolean master)
	{
		addProperty(agent, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @return True, if agent is master.
	 */
	public synchronized boolean isAgentMaster(IAgentIdentifier agent)
	{
		Boolean ret = (Boolean)getProperty(agent, PROPERTY_AGENT_MASTER);
		return ret==null? false: ret.booleanValue();
	}
}
