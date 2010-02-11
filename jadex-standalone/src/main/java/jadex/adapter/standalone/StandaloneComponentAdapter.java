package jadex.adapter.standalone;

import jadex.adapter.standalone.fipaimpl.ComponentIdentifier;
import jadex.adapter.standalone.service.componentexecution.ComponentExecutionService;
import jadex.bridge.CheckedAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.commons.ICommand;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.execution.IExecutionService;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	/** The component model. */
	protected ILoadableComponentModel model;

	/** The description holding the execution state of the component
	 *  (read only! managed by component execution service). */
	protected IComponentDescription	desc;
	
	/** Flag to indicate a fatal error (component termination will not be passed to instance) */
	protected boolean	fatalerror;
	
	//-------- steppable attributes --------
	
	/** The flag for a scheduled step (true when a step is allowed in stepwise execution). */
	protected boolean	dostep;
	
	/** The listener to be informed, when the requested step is finished. */
	protected IResultListener	steplistener;
	
	/** The selected breakpoints (component will change to step mode, when a breakpoint is reached). */
	protected Set	breakpoints;
	
	/** The breakpoint commands (executed, when a breakpoint triggers). */
	protected ICommand[]	breakpointcommands;
	
	//-------- external actions --------

	/** The thread executing the agent (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread componentthread;

	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected List	ext_entries;

	/** The flag if external entries are forbidden. */
	protected boolean ext_forbidden;
	
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public StandaloneComponentAdapter(IServiceContainer container, IComponentDescription desc)
	{
		this.container = container;
		this.desc	= desc;
		this.cid	= desc.getName();
		this.ext_entries = Collections.synchronizedList(new ArrayList());
	}
	
	/**
	 *  Set the component.
	 *  @param component The component to set.
	 */
	public void setComponent(IComponentInstance component, ILoadableComponentModel model)
	{
		this.component = component;
		this.model = model;
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
//		System.err.println("wakeup: "+getComponentIdentifier());
//		Thread.dumpStack();
		
		// todo: check this assert meaning!
		
		// Verify that the agent is running.
//		assert !IComponentDescription.STATE_INITIATED.equals(state) : this;
		
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
			throw new ComponentTerminatedException(cid.getName());
		
		// Change back to suspended, when previously waiting.
		if(IComponentDescription.STATE_WAITING.equals(desc.getState()))
		{
			ComponentExecutionService	ces	= (ComponentExecutionService)container.getService(IComponentExecutionService.class);
			ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
		}

		// Resume execution of the agent (when active or terminating).
		if(IComponentDescription.STATE_ACTIVE.equals(desc.getState())
			/*|| IComponentDescription.STATE_TERMINATING.equals(desc.getState())*/
			|| IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))	// Hack!!! external entries must also be executed in suspended state.
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
			pmap.put(sen, getComponentIdentifier());
		
		String idid = mt.getIdIdentifier();
		Object id = message.getValue(idid);
		if(id==null)
			pmap.put(idid, SUtil.createUniqueId(getComponentIdentifier().getLocalName()));

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
	 *  Return a component-identifier that allows to send
	 *  messages to this agent.
	 *  Return a copy of the original.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		// todo: remove cast, HACK!!!
		// todo: add transport addresses for multi-platform communication.
		return (IComponentIdentifier)((ComponentIdentifier)cid).clone();
	}
	
	/**
	 *  Get the container.
	 *  @return The container of this component.
	 */
	public IServiceContainer	getServiceContainer()
	{
		return container;
	}
	
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
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void killComponent(final IResultListener listener)
	{
//		System.out.println("killComponent: "+listener);
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
			throw new ComponentTerminatedException(cid.getName());

		if(!fatalerror)
		{
			component.killComponent(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					listener.resultAvailable(this, getComponentIdentifier());
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					listener.resultAvailable(this, getComponentIdentifier());
				}
			});
		}
		else if(listener!=null)
		{
			listener.resultAvailable(this, getComponentIdentifier());
		}
			
	}

	/**
	 *  Called when a message was sent to the agent.
	 *  (Called from message transport).
	 *  (Is it ok to call on external thread?).
	 */
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		IMessageAdapter msg = new DefaultMessageAdapter(message, type);
		component.messageArrived(msg);
	}
	
	//-------- IExecutable interface --------

	/**
	 *  Executable code for running the agent
	 *  in the platforms executor service.
	 */
	public boolean	execute()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		// Remember execution thread.
		this.componentthread	= Thread.currentThread();
		
		ClassLoader	cl	= componentthread.getContextClassLoader();
		componentthread.setContextClassLoader(model.getClassLoader());

		// Copy actions from external threads into the state.
		// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
		boolean	extexecuted	= false;
		Runnable[]	entries	= null;
		synchronized(ext_entries)
		{
			if(!(ext_entries.isEmpty()))
			{
				entries	= (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
//				for(int i=0; i<ext_entries.size(); i++)
//					state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, ext_entries.get(i));
				ext_entries.clear();
				
				extexecuted	= true;
			}
//			String agentstate = (String)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
//			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED.equals(agentstate))
//				ext_forbidden = true;
		}
		for(int i=0; entries!=null && i<entries.length; i++)
		{
			if(entries[i] instanceof CheckedAction)
			{
				if(((CheckedAction)entries[i]).isValid())
				{
					try
					{
						entries[i].run();
					}
					catch(Exception e)
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						System.err.println("Execution of action led to exeception: "+sw);
//						AgentRules.getLogger(state, ragent).severe("Execution of action led to exeception: "+sw);
					}
				}
				try
				{
					((CheckedAction)entries[i]).cleanup();
				}
				catch(Exception e)
				{
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					System.err.println("Execution of action led to exeception: "+sw);
//					AgentRules.getLogger(state, ragent).severe("Execution of action led to exeception: "+sw);
				}
			}
			else //if(entries[i] instanceof Runnable)
			{
				try
				{
					entries[i].run();
				}
				catch(Exception e)
				{
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					System.err.println("Execution of action led to exeception: "+sw);
//					AgentRules.getLogger(state, ragent).severe("Execution of action led to exeception: "+sw);
				}
			}
		}

		// Suspend when breakpoint is triggered.
		if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			if(component.isAtBreakpoint(desc.getBreakpoints()))
			{
				ComponentExecutionService	ces	= (ComponentExecutionService)container.getService(IComponentExecutionService.class);
				ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
			}
		}
		
		// Should the component be executed again?
		boolean	again = false;
		if(!extexecuted && (!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()) || dostep))
		{
			try
			{
				//System.out.println("Executing: "+agent);
				again	= component.executeStep();
			}
			catch(Throwable e)
			{
				// Fatal error!
				fatalerror	= true;
				e.printStackTrace();
				//agent.getLogger().severe("Fatal error, agent '"+aid+"' will be removed.");
				System.out.println("Fatal error, agent '"+cid+"' will be removed.");
					
				// Remove agent from platform.
				((IComponentExecutionService)container.getService(IComponentExecutionService.class)).destroyComponent(cid, null);
			}
			if(dostep)
			{
				dostep	= false;
				if(!again && IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
				{
					ComponentExecutionService	ces	= (ComponentExecutionService)container.getService(IComponentExecutionService.class);
					ces.setComponentState(cid, IComponentDescription.STATE_WAITING);	// I hope this doesn't cause any deadlocks :-/
				}
				again	= again && IComponentDescription.STATE_ACTIVE.equals(desc.getState());
				if(steplistener!=null)
					steplistener.resultAvailable(this, desc);
			}
		}

		// Suspend when breakpoint is triggered.
		if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			if(component.isAtBreakpoint(desc.getBreakpoints()))
			{
				ComponentExecutionService	ces	= (ComponentExecutionService)container.getService(IComponentExecutionService.class);
				ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
			}
		}

		// Reset execution thread.
		componentthread.setContextClassLoader(cl);
		this.componentthread = null;
		
		return again || extexecuted;
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		return Thread.currentThread()!=componentthread;
	}
	
	//-------- external access --------
	
	/**
	 *  Execute an action on the component thread.
	 *  May be safely called from any (internal or external) thread.
	 *  The contract of this method is as follows:
	 *  The component adapter ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action to be executed on the component thread.
	 */
	public void invokeLater(Runnable action)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		synchronized(ext_entries)
		{
			if(ext_forbidden)
				throw new ComponentTerminatedException("External actions cannot be accepted " +
					"due to terminated component state: "+this);
			{
				ext_entries.add(action);
			}
		}
		wakeup();
	}
	
	//-------- test methods --------
	
	/**
	 *  Make kernel agent available.
	 */
	public IComponentInstance	getComponentInstance()
	{
		return component;
	}

	//-------- step handling --------
	
	/**
	 *  Set the step mode.
	 */
	public void	doStep(IResultListener listener)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		if(dostep)
			listener.exceptionOccurred(this, new RuntimeException("Only one step allowed at a time."));
			
		this.dostep	= true;		this.steplistener	= listener;
	}
}
