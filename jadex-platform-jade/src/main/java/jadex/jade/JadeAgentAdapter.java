package jadex.jade;

import jade.content.ContentElement;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jadex.base.fipa.SFipa;
import jadex.bridge.CheckedAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IModelInfo;
import jadex.bridge.MessageFailureException;
import jadex.bridge.MessageType;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.jade.service.ComponentManagementService;
import jadex.commons.service.clock.IClockService;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *  Agent adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex agents without any 3rd party
 *  agent platform.
 */
public class JadeAgentAdapter extends Agent implements IComponentAdapter, Serializable
{
	//-------- attributes --------

	/** The platform. */
	protected transient Platform	platform;

	/** The agent identifier. */
	protected IComponentIdentifier	cid;

	/** The kernel agent. */
	protected IComponentInstance	agent;
	
	/** The component model. */
	protected IModelInfo model;
	
	/** The description holding the execution state of the component
	 *  (read only! managed by component execution service). */
	protected IComponentDescription	desc;

	/** The state of the agent (according to FIPA, managed by AMS). */
	protected String	state;
	
	/** Flag to indicate a fatal error (agent termination will not be passed to kernel) */
	protected boolean	fatalerror;
	
	/** The component logger. */
	protected Logger logger;
	
	//-------- attributes --------

	/** The message receiver behaviour. */
	protected Behaviour mesrec;

	/** The timing behaviour. */
//	protected TimingBehaviour timing;

	/** The agenda control behaviour. */
	protected ActionExecutionBehaviour agendacontrol;

	/** The agent thread (hack!!!). */
	protected transient	Thread	agentthread;
	
	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected List	ext_entries;

	/** The flag if external entries are forbidden. */
	protected boolean ext_forbidden;
	
	//-------- steppable attributes --------
	
	/** The flag for a scheduled step (true when a step is allowed in stepwise execution). */
	protected boolean	dostep;
	
	/** The listener to be informed, when the requested step is finished. */
	protected IResultListener	steplistener;
	
	/** The selected breakpoints (component will change to step mode, when a breakpoint is reached). */
	protected Set	breakpoints;
	
	/** The breakpoint commands (executed, when a breakpoint triggers). */
	protected ICommand[]	breakpointcommands;


	//-------- constructors --------

	/**
	 *  Create the agent adapter
	 */
	// Cannot be done in constructor because AID is still null
	// Cannot be done in setAID() etc. because invisble 
	// :-(
//	public JadeAgentAdapter()
//	{
//		this.platform = Platform.getPlatform();
//		AMS ams = (AMS)platform.getService(IComponentManagementService.class);
////		ams.getAgentAdapterMap().put(getComponentIdentifier(), this);
//		ams.adapters.put(getComponentIdentifier(), this);
//	}
	
	/**
	 *  The setup method.
	 *  Automatically called when agents is born.
	 */
	protected void setup()
	{
		// super "constructor"
		super.setup();
		this.agentthread	= Thread.currentThread();
		this.ext_entries = Collections.synchronizedList(new ArrayList());
		
//		this.platform	= platform;
//		this.aid	= aid;
//		this.agent = platform.getAgentFactory().createKernelAgent(this, model, state, args);		
		
		// Use Jadex class loader as default. (hack???)
		// Might override jade specific settings (but unused in JADE 3.3)
//        agentthread.setContextClassLoader(DynamicURLClassLoader.getInstance());

        // Set fallback configuration to JADE.
//        Configuration.setFallbackConfiguration("jadex/adapter/jade/jade_conf.properties");
    	// Hack!!! Make platform name available to Jadex. (set always to override settings in properties)
//    	String	name	= getName();
//    	name	= name.substring(name.lastIndexOf("@")+1);
//    	Configuration.getConfiguration().setProperty("platformname", name);

		// Initialize evaluation parameters, hide first argument (model).
		Object[] args = this.getArguments();
		if(args==null || args.length<2)
			throw new RuntimeException("Requires arguments (model, configuration): "+SUtil.arrayToString(args));
		Object[] args2	= new Object[args.length-2];
		System.arraycopy(args, 2, args2, 0, args2.length);
				
		this.platform = Platform.getPlatform();
		ComponentManagementService ams = (ComponentManagementService)platform.getService(IComponentManagementService.class);
		this.cid	= SJade.convertAIDtoFipa(getAID(), ams);
		ams.getComponentAdapterMap().put(cid, this);
		
//		this.platform = (IPlatform)args[0];

		// Initialize the agent from model.
		/*if(args[0] instanceof String)
		{
			// Parse the arguments.
			Map argsmap = SCollection.createHashMap();
			IExpressionParser exp_parser = new JavaCCExpressionParser();
			for(int i=0; i<args2.length; i++)
			{
				// JADE agents can be supplied from command-line or via ams message with string arguments
				// They also can receive an object array via the inprocess interface (container controller)
				// Is this case the first argument must be a map that can be passed directly to Jadex.
				// Therefore arguments are interpreted when they are strings and contain a "=" character
				if(args2[i] instanceof String)
				{
					String tmp = (String)args2[i];
					int idx = tmp.indexOf("=");
					if(idx!=-1)
					{
						String argname = tmp.substring(0, idx);
						String argvalstr = tmp.substring(idx+1);
						//System.out.println("Found arg: "+argname+" = "+argvalstr);
						// Evaluate the argument value.
						Object argval = null;
						try
						{
							argval = exp_parser.parseExpression(argvalstr, null, null, 
								((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader());
//							argval = SParser.evaluateExpression(argvalstr, null, null);
						}
						catch(Exception e)
						{
							System.out.println("Cannot evaluate argument: "+argname+". Reason: "+e.getMessage());
							//e.printStackTrace();
						}
						argsmap.put(argname, argval);
					}
				}
				else if(args2[i] instanceof Map)
				{
					argsmap.putAll((Map)args2[i]);
				}
			}
//			this.agent = SComponentFactory.createKernelAgent(platform, this, (String)args[0], (String)args[1], argsmap);
			this.agent = SComponentFactory.createKernelComponent(platform, this, (String)args[0], (String)args[1], argsmap);
		}
		else //if(args[0] instanceof IMBDIAgent)
		{
			throw new RuntimeException("todo?");
//			this.agent = JadexAgentFactory.createJadexAgent(getName(), getLocalName(),
//				(IMBDIAgent)args[0], (String)args[1], params, this, "jadex.adapter.jade.jade_adapter");
		}*/

//		// Initialize Jade specific properties.
//		String mmax = Configuration.getConfiguration().getProperty(MESSAGE_QUEUE_MAX);
//		if(mmax==null)
//		{
//			//agent.getLogger().warning("Could not read default message queue size from agent properties.");
//			System.out.println("Could not read default message queue size from agent properties.");
//		}
//		else
//		{
//			int qs = Integer.parseInt(mmax);
//			if(qs>0)
//				setQueueSize(qs);
//		}
//		initLanguagesOntologies();

		//this.transports = new IMessageEventTransport[1];
		//transports[0] =	new JadeMessageEventTransport(this);

//		this.clock	= new JadeAgentClock("JADE clock for agent "+getLocalName());

		//try{Thread.sleep(20000);}catch(Exception e){}
//		System.out.println("Agent is starting: "+this.getName());
	}

	/**
	 *  Set the component.
	 *  @param component The component to set.
	 */
	public void setComponent(IComponentInstance component, IModelInfo model)
	{
		this.agent = component;
		this.model = model;
	}	
	
	/**
	 *  Set the component description.
	 *  @param desc The component description.
	 */
	public void setComponentDescription(IComponentDescription desc)
	{
		this.desc = desc;
	}
	
	/**
	 *  Start the agent by adding the behaviours.
	 */
	public void start()
	{
		// Start the required jade behaviours.
		// Hack!!! Add action execution behaviour first,
		// because it has to execute agent init action before other behaviours.
		this.agendacontrol	= new ActionExecutionBehaviour(this);
		addBehaviour(agendacontrol);
//		this.timing	= new TimingBehaviour(agent, clock);
//		addBehaviour(timing);
		this.mesrec	= new MessageReceiverBehaviour(platform, agent, 
			(IComponentManagementService)platform.getService(IComponentManagementService.class));
		addBehaviour(mesrec);
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
		// Verify that the  agent is running.
		assert !IComponentDescription.STATE_INITIATED.equals(state) : this;
		
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new ComponentTerminatedException(getComponentIdentifier().getName());
		
		// Resume execution of the agent (when active).
		//System.out.println("wakeup called: "+state);
		//if(AMSAgentDescription.STATE_ACTIVE.equals(state)
		//	|| AMSAgentDescription.STATE_TERMINATING.equals(state))
		{
//			platform.getExecutorService().execute(this);
			if(agendacontrol!=null)
				agendacontrol.restart();

//			((IExecutionService)platform.getService(IExecutionService.class)).execute(this);
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
	 * /
	public IComponentIdentifier getAgentIdentifier()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(aid.getName());

		// todo: remove cast, HACK!!!
		IAMS ams = (IAMS)platform.getService(IAMS.class);
		return ((AMS)ams).refreshAgentIdentifier(aid);
		//return (AgentIdentifier)aid.clone();
	}*/
	
	/**
	 *  Return an agent-identifier that allows to send
	 *  messages to this agent.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Get the platform.
	 *  @return the platform of this agent
	 */
	public IServiceProvider getRootServiceProvider()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new ComponentTerminatedException(getComponentIdentifier().getName());

		return platform;
	}
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new ComponentTerminatedException(getComponentIdentifier().getName());

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
		return "JadeAgentAdapter("+getComponentIdentifier().getName()+")";
	}

	//-------- methods called by the standalone platform --------

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from the reasoning engine and delegated to the ams.
	 * /
	public void killComponent()
	{
		((IComponentManagementService)platform.getService(IComponentManagementService.class)).destroyComponent(getComponentIdentifier(), null);
	}*/

	/**
	 *  Gracefully terminate the agent.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 * /
	public void killAgent(IResultListener listener)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state))
			throw new ComponentTerminatedException(getComponentIdentifier().getName());

		if(!fatalerror)
			agent.killComponent(listener);
		else if(listener!=null)
			listener.resultAvailable(this, getComponentIdentifier());
			
	}*/
	
	/**
	 *  Gracefully terminate the component.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the component is finished, the listener must be notified.
	 */
	public void killComponent(final IResultListener listener)
	{
//		System.out.println("killComponent: "+listener);
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
			throw new ComponentTerminatedException(cid.getName());

		if(!fatalerror)
		{
			agent.killComponent(new IResultListener()
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
			
		// LogManager causes memory leak till Java 7
		// No way to remove loggers and no weak references. 
	}

	/**
	 *  Called when a message was sent to the agent.
	 * /
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(getAgentIdentifier().getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		agent.messageArrived(new DefaultMessageAdapter(message, type));
	}*/
	
	/**
	 *  Set the state of the agent.
	 */
	public void	setState(String state)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(this.state))
			throw new ComponentTerminatedException(getComponentIdentifier().getName());

		this.state	= state;
	}
	
	/**
	 *  Get the state of the agent.
	 * /
	public String	getState()
	{
		return  state;
	}*/
	
	/**
	 *  Executable code for running the agent
	 *  in the platforms executor service.
	 * /
	public boolean	execute()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
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
	}*/
	
	//-------- test methods --------
	
	/**
	 *  Make kernel agent available.
	 */
	public IComponentInstance	getKernelAgent()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new ComponentTerminatedException(getComponentIdentifier().getName());

		return agent;
	}
	
	//-------- overridings --------

	/**
	 *  Initialize Jade languages and ontologies.
	 *  Done on init and after agent move.
	 */
	// Todo: remove, replaced by content codec.
//	protected void initLanguagesOntologies()
//	{
//		String[] names = agent.getPropertyNames("jade.language");
//		for(int j=0; j<names.length; j++)
//		{
//			getContentManager().registerLanguage((Codec)agent.getProperty(names[j]));
//		}
//		names = agent.getPropertyNames("jade.ontology");
//		for(int j=0; j<names.length; j++)
//		{
//			getContentManager().registerOntology((Ontology)agent.getProperty(names[j]));
//		}
//	}

	/**
	 *  Actions to perform when cloned.
	 * /
	public void	afterClone()
	{
		initLanguagesOntologies();
		this.agentthread	= Thread.currentThread();
	}*/

	/**
	 *  Actions to perform when moved.
	 * /
	public void	afterMove()
	{
		initLanguagesOntologies();
		this.agentthread	= Thread.currentThread();
	}*/

//	/**
//	 *  Takedown threads when threaded execution.
//	 */
//	protected void takeDown()
//	{
//		//System.out.println("Takedown called!!!");
//		// When jadex agent is still there, JADE agent is killed from outside.
//		// Perform cleanup.
//		if(agent!=null)
//		{
//			agent.cleanup();
//			agent	= null;
//		}
//	}

	//-------- IAgentAdapter methods --------

	/**
	 *  Test if the current thread is the agent thread.
	 *  @param thread The thread to test.
	 *  @return True, if thread is the agent thread.
	 */
	public boolean isAgentThread(Thread thread)
	{
		return thread==agentthread;
	}

	/**
	 *  Kill this agent.
	 */
	public void cleanupAgent()
	{
//		agent = null;
		super.doDelete();
	}

	/**
	 *  Kill this agent.
	 */
	public void doDelete()
	{
		// Add agenda action to avoid threading issues
		// super.doDelete() would interrupt agent thread (while e.g. waiting for plan).
		agent.killComponent(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				JadeAgentAdapter.super.doDelete();
			}
			public void exceptionOccurred(Object source, Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	/**
	 *  Return an agent-identifier that allows to send
	 *  messages to this agent.
	 * /
	public IComponentIdentifier getAgentIdentifier()
	{
		return SJade.convertAIDtoFipa(getAID());
	}*/
	
	/**
	 *  Send a message via the adapter.
	 *  @param mevent The message event. 
	 * /
	public void sendMessage(IRMessageEvent msgevent)
	{
		IMMessageEvent model = (IMMessageEvent)msgevent.getModelElement();
		if(!model.getMessageType().getName().equals(SFipa.MESSAGE_TYPE_NAME_FIPA))
			throw new MessageFailureException(msgevent, "Not a fipa message.");

		prepareSending(msgevent);
		ACLMessage aclmsg = (ACLMessage)msgevent.getMessage();
		
		aclmsg.addUserDefinedParameter(JadeMessageAdapter.MESSAGE_ID, msgevent.getId());
		
		if(!aclmsg.getAllReceiver().hasNext())
			throw new RuntimeException("No receiver specified in: "+aclmsg);
		send(aclmsg);
	}*/
	
	/**
	 *  Send a message via the adapter.
	 *  @param mevent The message event.
	 *  @param mytpe The message type.
	 *  @param codec The content codec.
	 */
	public void sendMessage(Map mevent, MessageType type)
	{
		if(type.getName().equals(SFipa.MESSAGE_TYPE_NAME_FIPA))
			throw new MessageFailureException(mevent, "Not a fipa message.");
		
		ACLMessage aclmsg = prepareSending(mevent);//, codec);
		
		if(!aclmsg.getAllReceiver().hasNext())
			throw new RuntimeException("No receiver specified in: "+aclmsg);
		send(aclmsg);
	}
	
	/**
	 *  Prepare the native platform message for sending.
	 */
	public ACLMessage prepareSending(Map mevent)//, IContentCodec codec)
	{
		Logger logger = Logger.getLogger("hack"); // Hack!
		//Logger logger = mevent.getScope().getLogger();
		//Agent agent = (Agent)msgevent.getScope().getAgent().getAgentAdapter();
		
		// Message should be null except in case of forwarding/resending an already received message.
		// todo: ?
		ACLMessage message = null;//(ACLMessage)mevent.get(SFipa.ORGINAL_MESSAGE);
		if(message==null)
		{
			message	= new ACLMessage(ACLMessage.UNKNOWN);
			return message;
		}		

		// Fill the parameter values in the acl message.
		
		for(Iterator it=mevent.keySet().iterator(); it.hasNext(); )
		{
			String pname = (String)it.next();

			if(!SFipa.MESSAGE_ATTRIBUTES.contains(pname))
			{
				logger.warning("Unsupported parameter in message event. Cannot be mapped to acl message: "+mevent);
			}
			else if(mevent.get(pname)!=null)
			{
				if(pname.equals(SFipa.ENCODING))
				{
					if(message.getEncoding()!=null && !message.getEncoding().equals(mevent.get(pname)))
						logger.warning(SFipa.ENCODING+" value overriden: "+mevent);
					message.setEncoding((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.CONVERSATION_ID))
				{
					if(message.getConversationId()!=null && !message.getConversationId().equals(mevent.get(pname)))
						logger.warning(SFipa.CONVERSATION_ID+" value overriden: "+mevent);
					message.setConversationId((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.IN_REPLY_TO))
				{
					if(message.getInReplyTo()!=null && !message.getInReplyTo().equals(mevent.get(pname)))
						logger.warning(SFipa.IN_REPLY_TO+" value overriden: "+mevent);
					message.setInReplyTo((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.LANGUAGE))
				{
					if(message.getLanguage()!=null && !message.getLanguage().equals(mevent.get(pname)))
						logger.warning(SFipa.LANGUAGE+" value overriden: "+mevent);
					message.setLanguage((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.ONTOLOGY))
				{
					if(message.getOntology()!=null && !message.getOntology().equals(mevent.get(pname)))
						logger.warning(SFipa.ONTOLOGY+" value overriden: "+mevent);
					message.setOntology((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.PROTOCOL))
				{
					if(message.getProtocol()!=null && !message.getProtocol().equals(mevent.get(pname)))
						logger.warning(SFipa.PROTOCOL+" value overriden: "+mevent);
					message.setProtocol((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.REPLY_BY))
				{
					Object	val	= mevent.get(pname);
					Date	date;
					if(val instanceof String)
					{
						try
						{
							date	= DateFormat.getDateInstance().parse((String)val);
						}
						catch(ParseException e)
						{
							throw new RuntimeException("Error parsing date: "+e);
						}
					}
					else
					{
						date	= (Date)val;
					}

					if(message.getReplyByDate()!=null && !message.getReplyByDate().equals(date))
						logger.warning(SFipa.REPLY_BY+" value overriden: "+mevent);
					message.setReplyByDate(date);
				}
				else if(pname.equals(SFipa.REPLY_WITH))
				{
					if(message.getReplyWith()!=null && !message.getReplyWith().equals(mevent.get(pname)))
						logger.warning(SFipa.REPLY_WITH+" value overriden: "+mevent);
					message.setReplyWith((String)mevent.get(pname));
				}
				else if(pname.equals(SFipa.PERFORMATIVE))
				{
					if(message.getPerformative()!=ACLMessage.UNKNOWN
					&& message.getPerformative()!=SJade.getPerformative(mevent.get(pname)))
						logger.warning(SFipa.PERFORMATIVE+" value overriden: "+mevent);
					message.setPerformative(SJade.getPerformative(mevent.get(pname)));
				}
				// not necessary because getContent() can be used
				/*else if(pname.equals(SFipa.CONTENT))
				{
					if(message.getContent()!=null)
						logger.warning(SFipa.CONTENT+" content overriden: "+msgevent);
					message.setContent((String)params[i].getValue());
				}*/
				else if(pname.equals(SFipa.SENDER))
				{
					if(message.getSender()!=null && !message.getSender().equals(SJade.getAID(mevent.get(pname))))
						logger.warning(SFipa.SENDER+" value overriden: "+mevent);
					message.setSender(SJade.getAID(mevent.get(pname)));
				}
//				// supported as parameters also for convenience.
//				else if(pname.equals(SFipa.RECEIVER))
//				{
//					// todo: check if receiver is the same and then produce no warning
//					if(message.getAllReceiver().hasNext())
//					{
//						message.clearAllReceiver();
//						logger.warning(SFipa.RECEIVER+" values overriden: "+msgevent);
//					}
//					message.addReceiver(SJade.getAID(params[i].getValue()));
//				}
				else if(pname.equals(SFipa.REPLY_TO) && mevent.get(pname)!=null)
				{
					// todo: check if replyto is the same and then produce no warning
					if(message.getAllReplyTo().hasNext())
						logger.warning(SFipa.REPLY_TO+" values overriden: "+mevent);

					// Remove all reply tos.
					message.clearAllReplyTo();
					message.addReplyTo(SJade.getAID(mevent.get(pname)));
				}
				
				else if(pname.equals(SFipa.RECEIVERS))
				{
					// todo: check if receivers are the same and then produce no warning
					if(message.getAllReceiver().hasNext())
						logger.warning(SFipa.RECEIVERS+" values overriden: "+mevent);

					// Remove all receivers.
					message.clearAllReceiver();

					// Add receivers from message event.
					Object[] res = (Object[])mevent.get(pname);
					for(int j=0; j<res.length; j++)
						message.addReceiver(SJade.getAID(res[j]));
				}
				else if(pname.equals(SFipa.REPLY_TO))
				{
					// todo: check if replytos are the same and then produce no warning
					if(message.getAllReplyTo().hasNext())
						logger.warning(SFipa.REPLY_TO+" values overriden: "+mevent);

					// Remove all reply tos.
					message.clearAllReplyTo();

					// Add reply tos from message event.
					Object[] res = (Object[])mevent.get(pname);
					for(int j=0; j<res.length; j++)
						message.addReplyTo(SJade.getAID(res[j]));
				}
			}
		}

		// todo: treat content as normal parameter but as LAST parameter!!

		// If content is not null, a native message should be sent.
		if(message.getContent()==null)
		{
			Object cont = mevent.get(SFipa.CONTENT);
			if(cont!=null)
			{
				/*Properties props = new Properties();
				String lang = (String)mevent.get(SFipa.LANGUAGE);
				String onto = (String)mevent.get(SFipa.ONTOLOGY);
				if(lang!=null)
					props.put(SFipa.LANGUAGE, lang);
				if(onto!=null)
					props.put(SFipa.ONTOLOGY, onto);
				*/
				//IContentCodec codec = msgevent.getScope().getContentCodec(props);

				// todo: require lang+onto==null?

				/*if(codec!=null)
				{
					if(message.getContent()!=null)
						logger.warning(SFipa.CONTENT+" value overriden: "+mevent);
					message.setContent(codec.encode(cont));
				}
				else */if(cont instanceof ContentElement)
				{
					try
					{
						getContentManager().fillContent(message, (ContentElement)cont);
					}
					catch(Exception e)
					{
//						e.printStackTrace();
						throw new ContentException(e.getMessage(), e);
					}
				}
				else if(cont instanceof String)
				{
					message.setContent((String)cont);
				}
				/*else
				{
					throw new ContentException("No content codec found for: "+props);
				}*/
			}
		}

		// set message id for the tracer
		message.addUserDefinedParameter("message-id", getLocalName()+":"+mevent.get(SFipa.X_MESSAGE_ID));
   	
		return message;
	}
	
	/**
	 *  Prepare the native platform message for sending.
	 * /
	public void prepareSending(IRMessageEvent msgevent)
	{
		Logger logger = msgevent.getScope().getLogger();
		Agent agent = (Agent)msgevent.getScope().getAgent().getAgentAdapter();
		
		// Message should be null except in case of forwarding/resending an already received message.
		ACLMessage message = (ACLMessage)msgevent.getMessage();
		if(message==null)
		{
			message	= new ACLMessage(ACLMessage.UNKNOWN);
			msgevent.setMessage(message);
		}		

		// Fill the parameter values in the acl message.
		IRParameter[] params = msgevent.getParameters();
		for(int i=0; i<params.length; i++)
		{
			String pname = params[i].getName();

			if(!SFipa.MESSAGE_ATTRIBUTES.contains(pname))
			{
				logger.warning("Unsupported parameter in message event. Cannot be mapped to acl message: "+msgevent);
			}
			else if(params[i].getValue()!=null)
			{
				if(pname.equals(SFipa.ENCODING))
				{
					if(message.getEncoding()!=null && !message.getEncoding().equals(params[i].getValue()))
						logger.warning(SFipa.ENCODING+" value overriden: "+msgevent);
					message.setEncoding((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.CONVERSATION_ID))
				{
					if(message.getConversationId()!=null && !message.getConversationId().equals(params[i].getValue()))
						logger.warning(SFipa.CONVERSATION_ID+" value overriden: "+msgevent);
					message.setConversationId((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.IN_REPLY_TO))
				{
					if(message.getInReplyTo()!=null && !message.getInReplyTo().equals(params[i].getValue()))
						logger.warning(SFipa.IN_REPLY_TO+" value overriden: "+msgevent);
					message.setInReplyTo((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.LANGUAGE))
				{
					if(message.getLanguage()!=null && !message.getLanguage().equals(params[i].getValue()))
						logger.warning(SFipa.LANGUAGE+" value overriden: "+msgevent);
					message.setLanguage((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.ONTOLOGY))
				{
					if(message.getOntology()!=null && !message.getOntology().equals(params[i].getValue()))
						logger.warning(SFipa.ONTOLOGY+" value overriden: "+msgevent);
					message.setOntology((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.PROTOCOL))
				{
					if(message.getProtocol()!=null && !message.getProtocol().equals(params[i].getValue()))
						logger.warning(SFipa.PROTOCOL+" value overriden: "+msgevent);
					message.setProtocol((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.REPLY_BY))
				{
					Object	val	= params[i].getValue();
					Date	date;
					if(val instanceof String)
					{
						try
						{
							date	= DateFormat.getDateInstance().parse((String)val);
						}
						catch(ParseException e)
						{
							throw new RuntimeException("Error parsing date: "+e);
						}
					}
					else
					{
						date	= (Date)val;
					}

					if(message.getReplyByDate()!=null && !message.getReplyByDate().equals(date))
						logger.warning(SFipa.REPLY_BY+" value overriden: "+msgevent);
					message.setReplyByDate(date);
				}
				else if(pname.equals(SFipa.REPLY_WITH))
				{
					if(message.getReplyWith()!=null && !message.getReplyWith().equals(params[i].getValue()))
						logger.warning(SFipa.REPLY_WITH+" value overriden: "+msgevent);
					message.setReplyWith((String)params[i].getValue());
				}
				else if(pname.equals(SFipa.PERFORMATIVE))
				{
					if(message.getPerformative()!=ACLMessage.UNKNOWN
					&& message.getPerformative()!=SJade.getPerformative(params[i].getValue()))
						logger.warning(SFipa.PERFORMATIVE+" value overriden: "+msgevent);
					message.setPerformative(SJade.getPerformative(params[i].getValue()));
				}
				// not necessary because getContent() can be used
				/*else if(pname.equals(SFipa.CONTENT))
				{
					if(message.getContent()!=null)
						logger.warning(SFipa.CONTENT+" content overriden: "+msgevent);
					message.setContent((String)params[i].getValue());
				}* /
				else if(pname.equals(SFipa.SENDER))
				{
					if(message.getSender()!=null && !message.getSender().equals(SJade.getAID(params[i].getValue())))
						logger.warning(SFipa.SENDER+" value overriden: "+msgevent);
					message.setSender(SJade.getAID(params[i].getValue()));
				}
//				// supported as parameters also for convenience.
//				else if(pname.equals(SFipa.RECEIVER))
//				{
//					// todo: check if receiver is the same and then produce no warning
//					if(message.getAllReceiver().hasNext())
//					{
//						message.clearAllReceiver();
//						logger.warning(SFipa.RECEIVER+" values overriden: "+msgevent);
//					}
//					message.addReceiver(SJade.getAID(params[i].getValue()));
//				}
				else if(pname.equals(SFipa.REPLY_TO) && params[i].getValue()!=null)
				{
					// todo: check if replyto is the same and then produce no warning
					if(message.getAllReplyTo().hasNext())
						logger.warning(SFipa.REPLY_TO+" values overriden: "+msgevent);

					// Remove all reply tos.
					message.clearAllReplyTo();
					message.addReplyTo(SJade.getAID(params[i].getValue()));
				}
			}
		}

		IRParameterSet[] paramsets = msgevent.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String psname = paramsets[i].getName();
			if(SFipa.MESSAGE_ATTRIBUTES.contains(psname))
			{
				if(psname.equals(SFipa.RECEIVERS))
				{
					// todo: check if receivers are the same and then produce no warning
					if(message.getAllReceiver().hasNext())
						logger.warning(SFipa.RECEIVERS+" values overriden: "+msgevent);

					// Remove all receivers.
					message.clearAllReceiver();

					// Add receivers from message event.
					Object[] res = paramsets[i].getValues();
					for(int j=0; j<res.length; j++)
						message.addReceiver(SJade.getAID(res[j]));
				}
				else if(psname.equals(SFipa.REPLY_TO))
				{
					// todo: check if replytos are the same and then produce no warning
					if(message.getAllReplyTo().hasNext())
						logger.warning(SFipa.REPLY_TO+" values overriden: "+msgevent);

					// Remove all reply tos.
					message.clearAllReplyTo();

					// Add reply tos from message event.
					Object[] res = paramsets[i].getValues();
					for(int j=0; j<res.length; j++)
						message.addReplyTo(SJade.getAID(res[j]));
				}
			}
		}

		// todo: treat content as normal parameter but as LAST parameter!!

		// If content is not null, a native message should be sent.
		if(message.getContent()==null)
		{
			Object cont = msgevent.getContent();
			if(cont!=null)
			{
				Properties props = new Properties();
				String lang = (String)msgevent.getParameter(SFipa.LANGUAGE).getValue();
				String onto = (String)msgevent.getParameter(SFipa.ONTOLOGY).getValue();
				if(lang!=null)
					props.put(SFipa.LANGUAGE, lang);
				if(onto!=null)
					props.put(SFipa.ONTOLOGY, onto);

				IContentCodec codec = msgevent.getScope().getContentCodec(props);

				// todo: require lang+onto==null?

				if(codec!=null)
				{
					if(message.getContent()!=null)
						logger.warning(SFipa.CONTENT+" value overriden: "+msgevent);
					message.setContent(codec.encode(cont));
				}
				else if(cont instanceof ContentElement)
				{
					try
					{
						agent.getContentManager().fillContent(message, (ContentElement)cont);
					}
					catch(Exception e)
					{
//						e.printStackTrace();
						throw new ContentException(e.getMessage(), e);
					}
				}
				else if(cont instanceof String)
				{
					message.setContent((String)cont);
				}
				else
				{
					throw new ContentException("No content codec found for: "+props);
				}
			}
		}

		// set message id for the tracer
		message.addUserDefinedParameter("message-id", agent.getLocalName()+":"+msgevent.getName());
   	}*/
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		return Thread.currentThread()!=agentthread;
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
	 *  Make kernel component available.
	 */
	public IComponentInstance	getComponentInstance()
	{
		return agent;
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
			
		this.dostep	= true;		
		this.steplistener	= listener;
	}
	
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		// todo: problem: loggers can cause memory leaks
		// http://bugs.sun.com/view_bug.do;jsessionid=bbdb212815ddc52fcd1384b468b?bug_id=4811930
		
		// Todo: include parent name for nested loggers.
		String name = getComponentIdentifier().getLocalName();
		logger = LogManager.getLogManager().getLogger(name);
		
		// if logger does not already exists, create it
		if(logger==null)
		{
			// Hack!!! Might throw exception in applet / webstart.
			try
			{
				logger = Logger.getLogger(name);
				initLogger(logger);
				//System.out.println(logger.getParent().getLevel());
			}
			catch(SecurityException e)
			{
				// Hack!!! For applets / webstart use anonymous logger.
				logger = Logger.getAnonymousLogger();
				initLogger(logger);
			}
		}
		
		return logger;
	}
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected void initLogger(Logger logger)
	{
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		
		Object prop = model.getProperties().get("logging.level");
		Level level = prop==null? Level.SEVERE: (Level)prop;
		logger.setLevel(level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = model.getProperties().get("logging.useParentHandlers");
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = model.getProperties().get("addConsoleHandler");
		if(prop!=null)
		{
            ConsoleHandler console = new ConsoleHandler();
            console.setLevel(Level.parse(prop.toString()));
            logger.addHandler(console);
        }
		
		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
		// if logger should add a filehandler to capture log data in a file. 
		// The user specifies the directory to contain the log file.
		// $scope.getAgentName() can be used to have agent-specific log files 
		//
		// The directory name can use special patterns defined in the
		// class, java.util.logging.FileHandler, 
		// such as "%h" for the user's home directory.
		// 
		String logfile =	(String)model.getProperties().get("logging.file");
		if(logfile!=null)
		{
		    try
		    {
			    Handler fh	= new FileHandler(logfile);
		    	fh.setFormatter(new SimpleFormatter());
		    	logger.addHandler(fh);
		    }
		    catch (IOException e)
		    {
		    	System.err.println("I/O Error attempting to create logfile: "
		    		+ logfile + "\n" + e.getMessage());
		    }
		}
	}
	
	/**
	 *  Executable code for running the component
	 *  in the platforms executor service.
	 */
	public boolean	execute()
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		// Remember execution thread.
		this.agentthread	= Thread.currentThread();
		
		ClassLoader	cl	= agentthread.getContextClassLoader();
		agentthread.setContextClassLoader(model.getClassLoader());

		// Copy actions from external threads into the state.
		// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
		boolean	extexecuted	= false;
		Runnable[]	entries	= null;
		synchronized(ext_entries)
		{
			if(!(ext_entries.isEmpty()))
			{
				entries	= (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
				ext_entries.clear();
				
				extexecuted	= true;
			}
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
						// Fatal error!
						fatalerror	= true;
						e.printStackTrace();
						getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
							
						// Remove component from platform.
						((IComponentManagementService)platform.getService(IComponentManagementService.class)).destroyComponent(cid);

//						StringWriter	sw	= new StringWriter();
//						e.printStackTrace(new PrintWriter(sw));
//						getLogger().severe("Execution of action led to exception: "+sw);
					}
				}
				try
				{
					((CheckedAction)entries[i]).cleanup();
				}
				catch(Exception e)
				{
					// Fatal error!
					fatalerror	= true;
					e.printStackTrace();
					getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
						
					// Remove component from platform.
					((IComponentManagementService)platform.getService(IComponentManagementService.class)).destroyComponent(cid);

//					StringWriter	sw	= new StringWriter();
//					e.printStackTrace(new PrintWriter(sw));
//					getLogger().severe("Execution of action led to exception: "+sw);
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
					// Fatal error!
					fatalerror	= true;
					e.printStackTrace();
					getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
						
					// Remove component from platform.
					((IComponentManagementService)platform.getService(IComponentManagementService.class)).destroyComponent(cid);

//					StringWriter	sw	= new StringWriter();
//					e.printStackTrace(new PrintWriter(sw));
//					getLogger().severe("Execution of action led to exception: "+sw);
				}
			}
		}

		// Suspend when breakpoint is triggered.
		if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			if(agent.isAtBreakpoint(desc.getBreakpoints()))
			{
				ComponentManagementService	ces	= (ComponentManagementService)platform.getService(IComponentManagementService.class);
				ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
			}
		}
		
		// Should the component be executed again?
		boolean	again = false;
		if(!extexecuted && (!IComponentDescription.STATE_SUSPENDED.equals(desc.getState())
			&& !IComponentDescription.STATE_WAITING.equals(desc.getState()) || dostep))
		{
			// Set state to waiting before step. (may be reset by wakup() call in step)
			if(dostep && IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
			{
				ComponentManagementService	ces	= (ComponentManagementService)platform.getService(IComponentManagementService.class);
				ces.setComponentState(cid, IComponentDescription.STATE_WAITING);	// I hope this doesn't cause any deadlocks :-/
			}

			try
			{
				//System.out.println("Executing: "+component);
				again	= agent.executeStep();
			}
			catch(Throwable e)
			{
				// Fatal error!
				fatalerror	= true;
				e.printStackTrace();
				getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
					
				// Remove component from platform.
				((IComponentManagementService)platform.getService(IComponentManagementService.class)).destroyComponent(cid);
			}
			if(dostep)
			{
				dostep	= false;
				// Set back to suspended if components is still waiting but wants to execute again.
				if(again && IComponentDescription.STATE_WAITING.equals(desc.getState()))
				{
					ComponentManagementService	ces	= (ComponentManagementService)platform.getService(IComponentManagementService.class);
					ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
				}
				again	= again && IComponentDescription.STATE_ACTIVE.equals(desc.getState());
				if(steplistener!=null)
					steplistener.resultAvailable(this, desc);
			}
		}

		// Suspend when breakpoint is triggered.
		if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			try
			{
			if(agent.isAtBreakpoint(desc.getBreakpoints()))
			{
				ComponentManagementService	ces	= (ComponentManagementService)platform.getService(IComponentManagementService.class);
				ces.setComponentState(cid, IComponentDescription.STATE_SUSPENDED);	// I hope this doesn't cause any deadlocks :-/
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		// Reset execution thread.
		agentthread.setContextClassLoader(cl);
		this.agentthread = null;
		
		return again || extexecuted;
	}
}


