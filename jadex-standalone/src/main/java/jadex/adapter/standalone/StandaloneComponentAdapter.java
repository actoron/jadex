package jadex.adapter.standalone;

import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.service.execution.IExecutionService;

import java.io.Serializable;
import java.util.Map;

/**
 *  Component adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex components without any 3rd party
 *  execution platform.
 */
public class StandaloneComponentAdapter implements IComponentAdapter, IExecutable, Serializable
{
	//-------- attributes --------

	/** The container. */
	protected transient IServiceContainer	container;

	/** The component identifier. */
	protected IComponentIdentifier	cid;

	/** The component instance. */
	protected IComponentInstance component;

	/** The execution state of the component (managed by component execution service). */
	protected String	state;
	
	/** Flag to indicate a fatal error (component termination will not be passed to instance) */
	protected boolean	fatalerror;
	
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public StandaloneComponentAdapter(IServiceContainer container, IComponentIdentifier cid)
	{
		this.container = container;
		this.cid = cid;
	}
	
	/**
	 *  Set the component.
	 *  @param component The component to set.
	 */
	public void setComponent(IComponentInstance component)
	{
		this.component = component;
	}	
	
	//-------- IComponentAdapter methods --------

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
//		assert !IComponentDescription.STATE_INITIATED.equals(state) : this;
		
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(cid.getName());
		
		// Resume execution of the agent (when active or terminating).
		if(IComponentDescription.STATE_ACTIVE.equals(state)
			|| IComponentDescription.STATE_TERMINATING.equals(state))
		{
			//System.out.println("wakeup called: "+state);
			((IExecutionService)container.getService(IExecutionService.class)).execute(this);
		}
	}

	/**
	 *  Send a message via the adapter.
	 *  @param message The message (name/value pairs).
	 *  @param mytpe The message type.
	 * /
	public void sendMessage(IMessageAdapter message)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
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
		
		IComponentIdentifier[] recs = null;
		Object tmp = message.getValue(mt.getReceiverIdentifier());
		if(tmp instanceof Collection)
			recs = (IComponentIdentifier[])((Collection)tmp).toArray(new IComponentIdentifier[0]);
		else
			recs = (IComponentIdentifier[])tmp;
		
		IMessageService msgservice = (IMessageService)platform.getService(IMessageService.class);
		msgservice.sendMessage(pmap, mt, recs);
	}*/

	/**
	 *  Return an agent-identifier that allows to send
	 *  messages to this agent.
	 *  Return a copy of the original.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(cid.getName());

		// todo: remove cast, HACK!!!
		// todo: add transport addresses for multi-platform communication.
//		IAMS ams = (IAMS)container.getService(IAMS.class);
//		return ((AMS)ams).refreshAgentIdentifier(cid);
		return (IComponentIdentifier)((AgentIdentifier)cid).clone();
	}
	
	/**
	 *  Get the container.
	 *  @return The container of this component.
	 */
	public IServiceContainer	getServiceContainer()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(cid.getName());

		return container;
	}
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(cid.getName());

//		return platform.getClock();
		return (IClockService)container.getService(IClockService.class);
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
		return "StandaloneAgentAdapter("+cid.getName()+")";
	}

	//-------- methods called by the standalone platform --------

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from the reasoning engine and delegated to the ams.
	 */
	public void killAgent()
	{
		((IComponentExecutionService)container.getService(IComponentExecutionService.class)).destroyComponent(cid, null);
	}

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void killAgent(IResultListener listener)
	{
//		System.out.println("killAgent: "+listener);
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(cid.getName());

		if(!fatalerror)
			component.killComponent(listener);
		else if(listener!=null)
			listener.resultAvailable(getComponentIdentifier());
			
	}

	/**
	 *  Called when a message was sent to the agent.
	 */
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(cid.getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		component.messageArrived(new DefaultMessageAdapter(message, type));
	}
	
	/**
	 *  Set the state of the agent.
	 */
	public void	setState(String state)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(this.state))
			throw new AgentTerminatedException(cid.getName());

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
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(cid.getName());

		boolean	executed	= false;
		try
		{
			//System.out.println("Executing: "+agent);
			executed	= component.executeStep();
		}
		catch(Throwable e)
		{
			// Fatal error!
			fatalerror	= true;
			e.printStackTrace();
			//agent.getLogger().severe("Fatal error, agent '"+aid+"' will be removed.");
			System.out.println("Fatal error, agent '"+cid+"' will be removed.");
				
			// Remove agent from platform.
			killAgent();
		}
		
		return executed;
	}
	
	//-------- test methods --------
	
	/**
	 *  Make kernel agent available.
	 */
	public IComponentInstance	getComponentInstance()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(cid.getName());

		return component;
	}
}
