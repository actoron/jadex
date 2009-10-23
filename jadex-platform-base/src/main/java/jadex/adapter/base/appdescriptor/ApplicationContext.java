package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.SComponentExecutionService;
import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.IPlatform;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  The application context provides a closed environment for agents.
 *  If agents spawn other agents, these will automatically be added to
 *  the context.
 *  When the context is deleted all agents will be destroyed.
 *  An agent must only be in one application context.
 */
public class ApplicationContext	extends BaseContext implements IApplicationContext
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform	platform;
	
	/** The application type. */
	protected MApplicationType	apptype;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more agents can be added). */
	protected boolean	terminating;
	
	/** The agent types (aid -> typename). */
	protected Map	agenttypes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ApplicationContext(String name, /*IContext parent,*/ Map properties, IPlatform platform)
	{
		super(name, /*parent,*/ properties);
		this.platform	= platform;
		this.apptype	= properties!=null ? (MApplicationType)properties.get(PROPERTY_APPLICATION_TYPE) : null;
		if(apptype==null)
			throw new RuntimeException("Property '"+PROPERTY_APPLICATION_TYPE+"' required.");
	}

	//-------- IContext interface --------
		
	/**
	 *  Add an agent to a context.
	 */
	// Cannot be synchronized due to deadlock with space (uses context.getAgentType()).
	public /*synchronized*/ void	addAgent(IComponentIdentifier agent)
	{
		if(isTerminating())
			throw new RuntimeException("Cannot add agent to terminating context: "+agent+", "+this);
		
		// Todo: when not synchronized, the check might not alwyas detect
		// duplicate addition when race condition (do we care!?)
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
	public void	agentCreated(IComponentIdentifier creator, IComponentIdentifier newagent)
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
		final IComponentIdentifier[]	agents	= getAgents();
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
//			IAMS ams = (IAMS) platform.getService(IAMS.class);
			for(int i=0; i<agents.length; i++)
			{
				SComponentExecutionService.destroyComponent(platform, agents[i], l2);
//				ams.destroyAgent(agents[i], l2);
			}
		}
		else
		{
			if(listener!=null)
				listener.resultAvailable(this);
		}

//		System.out.println("Deleted: "+this);
	}

	//-------- methods --------
	
	/**
	 *  Get the applicattion type.
	 */
	public MApplicationType	getApplicationType()
	{
		return apptype;
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	public IPlatform getPlatform()
	{
		return platform;
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
	public void createAgent(String name, final String type, String configuration,
			Map arguments, final boolean start, final boolean master, 
			final IResultListener listener, IComponentIdentifier creator)
	{
		MAgentType	at	= apptype.getMAgentType(type);
		if(at==null)
			throw new RuntimeException("Unknown agent type '"+type+"' in application: "+apptype);
//		final IAMS	ams	= (IAMS) platform.getService(IAMS.class);
		
		SComponentExecutionService.createComponent(platform, name, at.getFilename(), configuration, arguments, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				if(listener!=null)
					listener.exceptionOccurred(exception);
			}
			public void resultAvailable(Object result)
			{
				IComponentIdentifier aid = (IComponentIdentifier)result;
				synchronized(ApplicationContext.this)
				{
					if(agenttypes==null)
						agenttypes	= new HashMap();
					agenttypes.put(aid, type);
				}
				
				if(!containsAgent(aid))
					addAgent(aid);	// Hack??? agentCreated() may be called from AMS.
				
				if(master)
				{
					addProperty(aid, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
				}
				
				if(start)
				{
					SComponentExecutionService.startComponent(platform, aid, listener);
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
	// Cannot be synchronized due to deadlock with space (uses context.getAgentType()).
	public /*synchronized*/ void	removeAgent(IComponentIdentifier agent)
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
	public void setAgentMaster(IComponentIdentifier agent, boolean master)
	{
		addProperty(agent, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @return True, if agent is master.
	 */
	public boolean isAgentMaster(IComponentIdentifier agent)
	{
		Boolean ret = (Boolean)getProperty(agent, PROPERTY_AGENT_MASTER);
		return ret==null? false: ret.booleanValue();
	}
	
	/**
	 *  Get the agent type for an agent id.
	 *  @param aid	The agent id.
	 *  @return The agent type name.
	 */
	public synchronized String	getAgentType(IComponentIdentifier aid)
	{
		return agenttypes!=null ? (String)agenttypes.get(aid) : null;
	}
	
	/**
	 *  Get the agent types.
	 *  @return The agent types.
	 */
	public String[] getAgentTypes()
	{
		List atypes = apptype.getMAgentTypes();
		String[] ret = atypes!=null? new String[atypes.size()]: SUtil.EMPTY_STRING;
		
		for(int i=0; i<ret.length; i++)
		{
			MAgentType at = (MAgentType)atypes.get(i);
			ret[i] = at.getName();
		}
		
		return ret;
	}
	
	/**
	 *  Get the agent type for an agent filename.
	 *  @param aid	The agent filename.
	 *  @return The agent type name.
	 */
	public String getAgentType(String filename)
	{
		String ret = null;
		filename = filename.replace('\\', '/');
		
		List agenttypes = apptype.getMAgentTypes();
		for(Iterator it=agenttypes.iterator(); it.hasNext(); )
		{
			MAgentType agenttype = (MAgentType)it.next();
			if(filename.endsWith(agenttype.getFilename()))
				ret = agenttype.getName();
		}
		
		return ret;
	}
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return apptype.getAllImports();
	}
}
