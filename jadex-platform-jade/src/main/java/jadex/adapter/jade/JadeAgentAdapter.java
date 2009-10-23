package jadex.adapter.jade;

import jade.content.ContentElement;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jadex.adapter.base.SComponentExecutionService;
import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IPlatform;
import jadex.bridge.MessageFailureException;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.service.clock.IClockService;
import jadex.service.library.ILibraryService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

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
//	protected IAgentIdentifier	aid;

	/** The kernel agent. */
	protected IComponentInstance	agent;

	/** The state of the agent (according to FIPA, managed by AMS). */
	protected String	state;
	
	/** Flag to indicate a fatal error (agent termination will not be passed to kernel) */
	protected boolean	fatalerror;
	
	//-------- attributes --------

	/** The message receiver behaviour. */
	protected Behaviour mesrec;

	/** The timing behaviour. */
//	protected TimingBehaviour timing;

	/** The agenda control behaviour. */
	protected ActionExecutionBehaviour agendacontrol;

	/** The agent thread (hack!!!). */
	protected transient	Thread	agentthread;

	//-------- constructors --------

	/**
	 *  The setup method.
	 *  Automatically called when agents is born.
	 */
	protected void setup()
	{
		// super "constructor"
		super.setup();
		this.agentthread	= Thread.currentThread();
		
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
				
//		this.platform = (IPlatform)args[0];
		this.platform = Platform.getPlatform();
		AMS ams = (AMS)platform.getService(IAMS.class);
		ams.getAgentAdapterMap().put(getComponentIdentifier(), this);
		
		// Initialize the agent from model.
		if(args[0] instanceof String)
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
//			this.agent = platform.getAgentFactory().createKernelAgent(this, (String)args[0], (String)args[1], argsmap);
			this.agent = SComponentFactory.createKernelAgent(platform, this, (String)args[0], (String)args[1], argsmap);
		}
		else //if(args[0] instanceof IMBDIAgent)
		{
			throw new RuntimeException("todo?");
//			this.agent = JadexAgentFactory.createJadexAgent(getName(), getLocalName(),
//				(IMBDIAgent)args[0], (String)args[1], params, this, "jadex.adapter.jade.jade_adapter");
		}

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

		// Start the required jade behaviours.
		// Hack!!! Add action execution behaviour first,
		// because it has to execute agent init action before other behaviours.
		this.agendacontrol	= new ActionExecutionBehaviour(agent);
		addBehaviour(agendacontrol);
//		this.timing	= new TimingBehaviour(agent, clock);
//		addBehaviour(timing);
		this.mesrec	= new MessageReceiverBehaviour(platform, agent, (IAMS)platform.getService(IAMS.class));
		addBehaviour(mesrec);

		//try{Thread.sleep(20000);}catch(Exception e){}
//		System.out.println("Agent is starting: "+this.getName());
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
		assert !IAMSAgentDescription.STATE_INITIATED.equals(state) : this;
		
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(getComponentIdentifier().getName());
		
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
	 * /
	public IAgentIdentifier getAgentIdentifier()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
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
		return SJade.convertAIDtoFipa(getAID(), (IAMS)getServiceContainer().getService(IAMS.class));
	}
	
	/**
	 *  Get the platform.
	 *  @return the platform of this agent
	 */
	public IPlatform	getServiceContainer()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(getComponentIdentifier().getName());

		return platform;
	}
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state))
			throw new AgentTerminatedException(getComponentIdentifier().getName());

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
	 */
	public void killAgent()
	{
		SComponentExecutionService.destroyComponent(platform, getComponentIdentifier(), null);
//		((IAMS)platform.getService(IAMS.class)).destroyAgent(getAgentIdentifier(), null);
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
			throw new AgentTerminatedException(getComponentIdentifier().getName());

		if(!fatalerror)
			agent.killComponent(listener);
		else if(listener!=null)
			listener.resultAvailable(getComponentIdentifier());
			
	}

	/**
	 *  Called when a message was sent to the agent.
	 * /
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state) || fatalerror)
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
		if(IAMSAgentDescription.STATE_TERMINATED.equals(this.state))
			throw new AgentTerminatedException(getComponentIdentifier().getName());

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
	}*/
	
	//-------- test methods --------
	
	/**
	 *  Make kernel agent available.
	 */
	public IComponentInstance	getKernelAgent()
	{
		if(IAMSAgentDescription.STATE_TERMINATED.equals(state) || fatalerror)
			throw new AgentTerminatedException(getComponentIdentifier().getName());

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
		agent = null;
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
			public void resultAvailable(Object result)
			{
				JadeAgentAdapter.super.doDelete();
			}
			public void exceptionOccurred(Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	/**
	 *  Return an agent-identifier that allows to send
	 *  messages to this agent.
	 * /
	public IAgentIdentifier getAgentIdentifier()
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
}


