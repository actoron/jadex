package jadex.adapter.standalone;

import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.standalone.ams.AMS;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IKernelAgent;
import jadex.bridge.IPlatform;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.clock.IClockService;
import jadex.service.execution.IExecutionService;

import java.io.Serializable;
import java.util.Map;

/**
 *  Agent adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex agents without any 3rd party
 *  agent platform.
 */
public class StandaloneAgentAdapter implements IAgentAdapter, IExecutable, Serializable
{
	//-------- attributes --------

	/** The platform. */
	protected transient IPlatform	platform;

	/** The agent identifier. */
	protected IAgentIdentifier	aid;

	/** The kernel agent. */
	protected IKernelAgent	agent;

	/** The state of the agent (according to FIPA, managed by AMS). */
	protected String	state;
	
	/** Flag to indicate a fatal error (agent termination will not be passed to kernel) */
	protected boolean	fatalerror;
	
	//-------- constructors --------

	/**
	 *  Create a new StandaloneAgentAdapter.
	 *  Uses the thread pool for executing the jadex agent.
	 */
	public StandaloneAgentAdapter(IPlatform platform, IAgentIdentifier aid, String model, String state, Map args)
	{
		this.platform	= platform;
		this.aid	= aid;
		this.agent = MetaAgentFactory.createKernelAgent(platform, this, model, state, args);
//		this.agent = platform.getAgentFactory().createKernelAgent(this, model, state, args);		
	}

	//-------- IAgentAdapter methods --------
	
	/**
	 *  Called by the agent when it probably awoke from an idle state.
	 *  The platform has to make sure that the agent will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no agent related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void wakeup()
	{
		// todo: check this assert meaning!
		
		// Verify that the agent is running.
//		assert !IAMSAgentDescription.STATE_INITIATED.equals(state) : this;
		
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());
		
		if(IAMSAgentDescription.STATE_ACTIVE.equals(state))
		{
			// Resume execution of the agent (when active).
			//System.out.println("wakeup called: "+state);
			//if(AMSAgentDescription.STATE_ACTIVE.equals(state)
			//	|| AMSAgentDescription.STATE_TERMINATING.equals(state))
			{
	//			platform.getExecutorService().execute(this);
				((IExecutionService)platform.getService(IExecutionService.class)).execute(this);
			}
		}
	}

	/**
	 *  Send a message via the adapter.
	 *  @param message The message (name/value pairs).
	 *  @param mytpe The message type.
	 * /
	public void sendMessage(IMessageAdapter message)
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		Map pmap = message.getParameterMap();
		
		// Check and possibly insert sender
		MessageType mt = message.getMessageType();
		
		// Automatically add optional meta information.
		String sen = mt.getSenderIdentifier();
		Object sender = message.getValue(sen);
		if(sender==null)
			pmap.put(sen, getAgentIdentifier());
		
		String idid = mt.getIdIdentifier();
		Object id = message.getValue(idid);
		if(id==null)
			pmap.put(idid, SUtil.createUniqueId(getAgentIdentifier().getLocalName()));

		String sd = mt.getTimestampIdentifier();
		Object senddate = message.getValue(sd);
		if(senddate==null)
			pmap.put(sd, ""+getClock().getTime());
		
		IAgentIdentifier[] recs = null;
		Object tmp = message.getValue(mt.getReceiverIdentifier());
		if(tmp instanceof Collection)
			recs = (IAgentIdentifier[])((Collection)tmp).toArray(new IAgentIdentifier[0]);
		else
			recs = (IAgentIdentifier[])tmp;
		
		IMessageService msgservice = (IMessageService)platform.getService(IMessageService.class);
		msgservice.sendMessage(pmap, mt, recs);
	}*/

	/**
	 *  Return an agent-identifier that allows to send
	 *  messages to this agent.
	 *  Return a copy of the original.
	 */
	public IAgentIdentifier getAgentIdentifier()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		// todo: remove cast, HACK!!!
		IAMS ams = (IAMS)platform.getService(IAMS.class);
		return ((AMS)ams).refreshAgentIdentifier(aid);
		//return (AgentIdentifier)aid.clone();
	}
	
	/**
	 *  Get the platform.
	 *  @return the platform of this agent
	 */
	public IPlatform	getPlatform()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		return platform;
	}
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

//		return platform.getClock();
		return (IClockService)platform.getService(IClockService.class);
	}
	
	// Hack!!!! todo: remove
	/**
	 *  Get the execution control.
	 *  @return The execution control.
	 * /
	public ExecutionContext getExecutionControl()
	{
		if(AMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		return platform.getExecutionControl();
	}*/
	
	/**
	 *  String representation of the agent.
	 */
	public String toString()
	{
		return "StandaloneAgentAdapter("+aid.getName()+")";
	}

	//-------- methods called by the standalone platform --------

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from the reasoning engine and delegated to the ams.
	 */
	public void killAgent()
	{
		((IAMS)platform.getService(IAMS.class)).destroyAgent(aid, null);
	}

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void killAgent(IResultListener listener)
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		if(!fatalerror)
			agent.killAgent(listener);
		else if(listener!=null)
			listener.resultAvailable(getAgentIdentifier());
			
	}

	/**
	 *  Called when a message was sent to the agent.
	 */
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(aid.getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		agent.messageArrived(new DefaultMessageAdapter(message, type));
	}
	
	/**
	 *  Set the state of the agent.
	 */
	public void	setState(String state)
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(this.state))
			throw new AgentTerminatedException(aid.getName());

		this.state	= state;
	}
	
	/**
	 *  Get the state of the agent.
	 */
	public String	getState()
	{
		return  state;
	}
	
	//-------- IExecutable interface --------

	/**
	 *  Executable code for running the agent
	 *  in the platforms executor service.
	 */
	public boolean	execute()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(aid.getName());

		boolean	executed	= false;
		try
		{
			//System.out.println("Executing: "+agent);
			executed	= agent.executeAction();
		}
		catch(Throwable e)
		{
			// Fatal error!
			fatalerror	= true;
			e.printStackTrace();
			//agent.getLogger().severe("Fatal error, agent '"+aid+"' will be removed.");
			System.out.println("Fatal error, agent '"+aid+"' will be removed.");
				
			// Remove agent from platform.
			((IAMS)platform.getService(IAMS.class)).destroyAgent(aid, null);				
		}
		
		return executed;
	}
	
	//-------- test methods --------
	
	/**
	 *  Make kernel agent available.
	 */
	public IKernelAgent	getKernelAgent()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(aid.getName());

		return agent;
	}
}
