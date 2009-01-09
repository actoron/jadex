package jadex.adapter.jade;

import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Search;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.jade.fipaimpl.AMSAgentDescription;
import jadex.adapter.jade.fipaimpl.AgentIdentifier;
import jadex.adapter.jade.fipaimpl.SearchConstraints;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IAgentModel;
import jadex.bridge.IMessageService;
import jadex.bridge.IPlatformService;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Built-in standalone agent platform, with only basic features.
 *  // todo: what about this property change support? where used?
 */
public class AMS implements IAMS, IPlatformService
{
	//-------- constants --------

	/** The agent counter. */
	public static int agentcnt = 0;

	//-------- attributes --------

	/** The agent platform. */
	protected Platform platform;

	/** The agents (aid->adapter agent). */
	protected Map adapters;
	
	/** The ams agent descriptions (aid -> ams agent description). */
//	protected Map agentdescs;
	
	/** The logger. */
	protected Logger logger;

	/** The ams listeners. */
	protected List listeners;
	
    //-------- constructors --------

    /**
     *  Create a new AMS.
     */
    public AMS(Platform platform)
    {
		this.platform = platform;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
//		this.agentdescs = Collections.synchronizedMap(SCollection.createHashMap());
		this.logger = Logger.getLogger("JADE_Platform.ams");
		this.listeners = SCollection.createArrayList();
    }

    //-------- IAMS interface methods --------
    
	/**
	 *  Create a new agent on the platform.
	 *  Ensures (in non error case) that the aid of
	 *  the new agent is added to the AMS when call returns.
	 *  @param name The agent name (null for auto creation)
	 *  @param model The model name.
	 *  @param confi The configuration.
	 *  @param args The arguments map (name->value).
	 */
	public void	createAgent(String name, String model, String config, Map args, IResultListener lis)
	{
//		System.out.println("Create agent: "+name);
		final IResultListener listener = lis!=null? lis: DefaultResultListener.getInstance();
		IAgentIdentifier aid = null;
		AMSAgentDescription ad = null;
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}
		
//		if(platform.isShuttingDown())
//		{
//			listener.exceptionOccurred(new RuntimeException("No new agents may be created when platform is shutting down."));
//			return;
//			//throw new RuntimeException("No new agents may be created when platform is shutting down.");
//		}

		if(name==null)
		{
			name = generateAgentName(getShortName(model));
		}

		List argus = new ArrayList();
		argus.add(model);
		argus.add(config);//==null? "default": config);
		if(args!=null)
			argus.add(args);	// Jadex argument map is supplied as 3rd index in JADE argument array.  
				
//		Event e = new Event(-1, new SimpleBehaviour()
//		{
//			String convid = null;
//			boolean done = false;
//			
//			public void action()
//			{
//				System.out.println("Create agent code started.");
//
//				if(convid!=null)
//				{
//					ACLMessage reply = myAgent.receive();
//					if(reply==null)
//					{
//						block();
//					}
//					else
//					{
//						System.out.println("Reply received: "+reply);
//						if(reply.getPerformative()==ACLMessage.INFORM)
//						{
//							IAMSListener[]	alisteners;
//							synchronized(listeners)
//							{
//								alisteners	= (IAMSListener[])listeners.toArray(new IAMSListener[listeners.size()]);
//							}
//							// todo: can be called after listener has (concurrently) deregistered
//							for(int i=0; i<alisteners.length; i++)
//							{
//								alisteners[i].agentAdded(null);
//							}
//							
//							// Hack!!! Bug in JADE not returning created agent's AID.
//							// Should do ams_search do get correct AID?
//							AID aid = (AID)myAgent.getAMS().clone();
//							int idx = aid.getName().indexOf("@");
//							aid.setName(name + aid.getName().substring(idx));
//							IAgentIdentifier ret = SJade.convertAIDtoFipa(aid, (IAMS)platform.getService(IAMS.class));
//							
//							listener.resultAvailable(ret);
//						}
//						else
//						{
//							listener.exceptionOccurred(new AgentCreationException(reply.getContent(), null));
//						}
//						done = true;
//					}
//				}
//				else
//				{
//					try 
//					{
//						CreateAgent ca = new CreateAgent();
//						ca.setAgentName(name!=null? name: getShortName(model));
//						ca.setClassName("jadex.adapter.jade.JadeAgentAdapter");
//						ca.setContainer(new ContainerID(myAgent.getContainerController().getContainerName(), null));
//						if(argus!=null)
//						{
//							for(int i=0; i<argus.size(); i++)
//							{
//								Object arg = argus.get(i);
////											System.out.println(arg);
//								ca.addArguments(arg);
//							}
//						}
//						Action ac = new Action();
//						ac.setActor(myAgent.getAMS());
//						ac.setAction(ca);
//						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//						request.addReceiver(myAgent.getAMS());
//						request.setSender(myAgent.getAID());
//						request.setOntology(JADEManagementOntology.getInstance().getName());
//						request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
//						request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//						
//						convid = SUtil.createUniqueId(myAgent.getLocalName());
//						request.setConversationId(convid);
//						
//						myAgent.getContentManager().fillContent(request, ac);
//						// ACLMessage reply = FIPAService.doFipaRequestClient(this, request, 10000);
//						myAgent.send(request);
//						block();
//					} 
//					catch (Exception e) 
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//			
//			public boolean done()
//			{
//				return done;
//			}
//		});
//		platform.getPlatformAgentController().putO2AObject(e, AgentController.ASYNC);
				
		try
		{
			AgentController ac = platform.getPlatformController().createNewAgent(name, "jadex.adapter.jade.JadeAgentAdapter", argus.toArray());
			// Hack!!! Bug in JADE not returning created agent's AID.
			// Should do ams_search do get correct AID?
			AID tmp = (AID)platform.getPlatformAgent().clone();
			int idx = tmp.getName().indexOf("@");
			tmp.setName(name + tmp.getName().substring(idx));
			aid = SJade.convertAIDtoFipa(tmp, (IAMS)platform.getService(IAMS.class));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			listener.exceptionOccurred(e);
			return;
		}
		
		ad	= new AMSAgentDescription(aid);
		ad.setState(IAMSAgentDescription.STATE_INITIATED);
		
//		System.out.println("added: "+agentdescs.size()+", "+aid);

		IAMSListener[]	alisteners;
		synchronized(listeners)
		{
			alisteners	= (IAMSListener[])listeners.toArray(new IAMSListener[listeners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].agentAdded(ad);
		}
		
//		System.out.println("Created agent: "+aid);
		listener.resultAvailable(aid); 
	}

	/**
	 *  Start a previously created agent on the platform.
	 *  @param agent The id of the previously created agent.
	 */
	public void	startAgent(final IAgentIdentifier agent, IResultListener lis)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent identifier must not null.");
		
		final IResultListener listener = lis!=null? lis: DefaultResultListener.getInstance();
		
//		try
//		{
//			Event e = new Event(-1, new SimpleBehaviour()
//			{
//				String convid = null;
//				boolean done = false;
//				
//				public void action()
//				{
//					System.out.println("Start agent code started.");
//
//					if(convid!=null)
//					{
//						ACLMessage reply = myAgent.receive();
//						if(reply==null)
//						{
//							block();
//						}
//						else
//						{
//							System.out.println("Reply received: "+reply);
//							if(reply.getPerformative()==ACLMessage.INFORM)
//							{
//								listener.resultAvailable(null);
//							}
//							else
//							{
//								listener.exceptionOccurred(new RuntimeException("Cannot start agent "+agent+" "+reply.getContent()));
//							}
//							done = true;
//						}
//					}
//					else
//					{
//						try 
//						{
//							jade.domain.FIPAAgentManagement.AMSAgentDescription amsd = new jade.domain.FIPAAgentManagement.AMSAgentDescription();
//							amsd.setName(SJade.convertAIDtoJade(agent));
//							amsd.setState(jade.domain.FIPAAgentManagement.AMSAgentDescription.SUSPENDED);
//							Modify m = new Modify();
//							m.setDescription(amsd);
//							Action ac = new Action();
//							ac.setActor(myAgent.getAMS());
//							ac.setAction(m);
//
//							ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//							request.addReceiver(myAgent.getAMS());
//							request.setSender(myAgent.getAID());
//							request.setOntology(FIPAManagementOntology.NAME);
//							request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
//							request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//					
//							convid = SUtil.createUniqueId(myAgent.getLocalName());
//							request.setConversationId(convid);
//							
//							myAgent.getContentManager().fillContent(request, ac);
//							// ACLMessage reply = FIPAService.doFipaRequestClient(this, request, 10000);
//							myAgent.send(request);
//							block();
//						} 
//						catch (Exception e) 
//						{
//							e.printStackTrace();
//						}
//					}
//				}
//				
//				public boolean done()
//				{
//					return done;
//				}
//			});
//			
////					platform.getPlatformAgentController().putO2AObject(e, AgentController.ASYNC);
//			platform.getPlatformController().getAgent("platform").putO2AObject(e, AgentController.ASYNC);
//			
////					AgentController ac = platform.getPlatformController().createNewAgent(getShortName(model), "jadex.adapter.jade.JadeAgentAdapter", argus.toArray());
////					ac.start();
//		}
//		catch(ControllerException e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
				
		try
		{
			AgentController ac = platform.getPlatformController().getAgent(agent.getLocalName());
			ac.start();
			listener.resultAvailable(null);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an agent on the platform.
	 *  @param aid	The agent to destroy.
	 */
	public void destroyAgent(final IAgentIdentifier aid, IResultListener listener)
	{
		if(aid==null)
			throw new IllegalArgumentException("Agent identifier must not null.");
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		JadeAgentAdapter adapter = (JadeAgentAdapter)adapters.get(aid);
		adapter.killAgent(new CleanupCommand(aid, listener));
		
//		try
//		{
//			
//			AgentController ac = platform.getPlatformController().getAgent(aid.getLocalName());
//			ac.kill();
//			listener.resultAvailable(null);
//		}
//		catch(Exception e)
//		{
//			listener.exceptionOccurred(e);
//		}
	}
	
	/**
	 *  Suspend the execution of an agent.
	 *  @param aid The agent identifier.
	 *  // todo: make sure that agent is really suspended an does not execute
	 *  an action currently.
	 */
	public void suspendAgent(final IAgentIdentifier aid, IResultListener lis)
	{
		final IResultListener listener = lis!=null? lis: DefaultResultListener.getInstance();
		
//		Event e = new Event(-1, new SimpleBehaviour()
//		{
//			String convid = null;
//			boolean done = false;
//			
//			public void action()
//			{
//				System.out.println("Create agent code started.");
//
//				if(convid!=null)
//				{
//					ACLMessage reply = myAgent.receive();
//					if(reply==null)
//					{
//						block();
//					}
//					else
//					{
//						System.out.println("Reply received: "+reply);
//						if(reply.getPerformative()==ACLMessage.INFORM)
//						{
//							listener.resultAvailable(ret);
//						}
//						else
//						{
//							listener.exceptionOccurred(new AgentCreationException(reply.getContent(), null));
//						}
//						done = true;
//					}
//				}
//				else
//				{
//					try 
//					{
//						jade.domain.FIPAAgentManagement.AMSAgentDescription amsd = new jade.domain.FIPAAgentManagement.AMSAgentDescription();
//						amsd.setName(SJade.convertAIDtoJade(aid));
//						amsd.setState(jade.domain.FIPAAgentManagement.AMSAgentDescription.SUSPENDED);
//						Modify m = new Modify();
//						m.setDescription(amsd);
//						Action ac = new Action();
//						ac.setActor(myAgent.getAMS());
//						ac.setAction(m);
//
//						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//						request.addReceiver(myAgent.getAMS());
//						request.setSender(myAgent.getAID());
//						request.setOntology(FIPAManagementOntology.NAME);
//						request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
//						request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//				
//						convid = SUtil.createUniqueId(myAgent.getLocalName());
//						request.setConversationId(convid);
//						
//						myAgent.getContentManager().fillContent(request, ac);
//						// ACLMessage reply = FIPAService.doFipaRequestClient(this, request, 10000);
//						myAgent.send(request);
//						block();
//					} 
//					catch (Exception e) 
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//			
//			public boolean done()
//			{
//				return done;
//			}
//		});
//		platform.getPlatformController().getAgent("platform").putO2AObject(e, AgentController.ASYNC);
	
		try
		{
			AgentController ac = platform.getPlatformController().getAgent(aid.getLocalName());
			ac.suspend();
			listener.resultAvailable(null);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
	
	/**
	 *  Resume the execution of an agent.
	 *  @param aid The agent identifier.
	 */
	public void resumeAgent(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		try
		{
			AgentController ac = platform.getPlatformController().getAgent(aid.getLocalName());
			ac.activate();
			listener.resultAvailable(null);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}

	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions.
	 */
	public void	searchAgents(final IAMSAgentDescription adesc, final ISearchConstraints con, IResultListener lis)
	{
		final IResultListener listener = lis!=null? lis: DefaultResultListener.getInstance();
		
		Event e = new Event(-1, new SimpleBehaviour()
		{
			String convid = null;
			boolean done = false;
			
			public void action()
			{
				if(convid!=null)
				{
					ACLMessage reply = myAgent.receive(MessageTemplate.MatchConversationId(convid));
					if(reply==null)
					{
						block();
					}
					else
					{
						if(reply.getPerformative()==ACLMessage.INFORM)
						{
							try
							{
								Result res = (Result)myAgent.getContentManager().extractContent(reply);
								jade.util.leap.List descs = res.getItems();
								IAMSAgentDescription[] ret = new IAMSAgentDescription[descs.size()];
								for(int i=0; i<ret.length; i++)
								{
									ret[i] = SJade.convertAMSAgentDescriptiontoFipa(
										(jade.domain.FIPAAgentManagement.AMSAgentDescription)descs.get(i), AMS.this);
								}
								listener.resultAvailable(ret);
							}
							catch(Exception e)
							{
								listener.exceptionOccurred(e);
							}
						}
						else
						{
							listener.exceptionOccurred(new RuntimeException("Search failed."));
						}
						done = true;
					}
				}
				else
				{
					try 
					{
						// Hack !!! Strip addresses/resolvers from aid before search (JADE doesn't store these in AMS and therefore finds no matches, grrr).
						jade.domain.FIPAAgentManagement.AMSAgentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoJade(adesc);
						if(amsadesc.getName()!=null)
							amsadesc.setName(new AID(amsadesc.getName().getName(), AID.ISGUID));
						Search search = new Search();
						search.setDescription(amsadesc);
						search.setConstraints(SJade.convertSearchConstraintstoJade(con));
						Action ac = new Action();
						ac.setActor(myAgent.getAMS());
						ac.setAction(search);
						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(myAgent.getAMS());
						request.setSender(myAgent.getAID());
						request.setOntology(FIPAManagementOntology.getInstance().getName());
						request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
						request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
						
						convid = SUtil.createUniqueId(myAgent.getLocalName());
						request.setConversationId(convid);
						
						myAgent.getContentManager().fillContent(request, ac);
						// ACLMessage reply = FIPAService.doFipaRequestClient(this, request, 10000);
						myAgent.send(request);
						block();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
			
			public boolean done()
			{
				return done;
			}
		});
		
		try
		{
			platform.getPlatformController().getAgent("platform").putO2AObject(e, AgentController.ASYNC);
		}
		catch(ControllerException ex)
		{
			listener.exceptionOccurred(ex);
		}
	}
	
	/**
	 *  Test if an agent is currently living on the platform.
	 *  @param aid The agent identifier.
	 *  @return True, if agent is hosted on platform.
	 */
	public void containsAgent(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(adapters.containsKey(aid)? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the agent description of a single agent.
	 *  @param aid The agent identifier.
	 *  @return The agent description of this agent.
	 */
	public void getAgentDescription(IAgentIdentifier aid, final IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		AMSAgentDescription adesc = new AMSAgentDescription();
		adesc.setName(aid);
		searchAgents(adesc, null, new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAMSAgentDescription[] descs = (IAMSAgentDescription[])result;
				listener.resultAvailable(descs.length==1? descs[0]: null);
			}
			public void exceptionOccurred(Exception exception)
			{
				listener.exceptionOccurred(exception);
			}
		});
	}
	
	/**
	 *  Get the agent descriptions.
	 *  @return The agent descriptions.
	 */
	public void getAgentDescriptions(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		AMSAgentDescription adesc = new AMSAgentDescription();
		SearchConstraints con = new SearchConstraints();
		con.setMaxResults(Integer.MAX_VALUE);
		searchAgents(adesc, con, listener);
	}
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public void getAgentIdentifiers(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		IAgentIdentifier[] ret;
		
		synchronized(adapters)
		{
			ret = (IAgentIdentifier[])adapters.keySet().toArray(new IAgentIdentifier[adapters.size()]);
			for(int i=0; i<ret.length; i++)
				ret[i] = refreshAgentIdentifier(ret[i]); // Hack!
		}
		
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Get the number of active agents.
	 *  @return The number of active agents.
	 */
	public void getAgentCount(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(new Integer(adapters.size()));
	}
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 * /
	public void getAgentIdentifiers(final IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		getAgentDescriptions(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAMSAgentDescription[] descs = (IAMSAgentDescription[])result;
				IAgentIdentifier[] ret = new IAgentIdentifier[descs.length];
				for(int i=0; i<descs.length; i++)
				{
					ret[i] = descs[i].getName();
				}
				listener.resultAvailable(ret);
			}
			public void exceptionOccurred(Exception exception)
			{
				listener.exceptionOccurred(exception);
			}
		});
	}*/
	
	/**
	 *  Get the number of active agents.
	 *  @return The number of active agents.
	 * /
	public void getAgentCount(final IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		getAgentDescriptions(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				listener.resultAvailable(((IAMSAgentDescription[])result).length);
			}
			public void exceptionOccurred(Exception exception)
			{
				listener.exceptionOccurred(exception);
			}
		});
	}*/
	
	/**
	 *  Get the agent adapter for an agent identifier.
	 *  @param aid The agent identifier.
	 *  @return The agent adapter.
	 */
	public void getAgentAdapter(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(adapters.get(aid));
	}
	
	/**
	 *  Get the external access of an agent.
	 *  @param aid The agent identifier.
	 *  @param listener The result listener.
	 */
	public void getExternalAccess(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		JadeAgentAdapter adapter = (JadeAgentAdapter)adapters.get(aid);
		if(adapter==null)
			listener.exceptionOccurred(new RuntimeException("No local agent found for agent identifier: "+aid));
		else
			adapter.getKernelAgent().getExternalAccess(listener);
	}

	
	//-------- IPlatformService interface methods --------
	 
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// nothing to do.
	}
	
	/**
	 *  Called when the platform shuts down.
	 *  At this time all agents already had time to kill themselves
	 *  (as specified in the platform shutdown time).
	 *  Remaining agents should be discarded.
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		listener.resultAvailable(null);
	}
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new agent identifier.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new AgentIdentifier(name);
	}
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local, String[] addresses)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new AgentIdentifier(name, addresses, null);
	}
	
	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth)
	{
		SearchConstraints	ret	= new SearchConstraints();
		ret.setMaxResults(maxresults);
		ret.setMaxDepth(maxdepth);
		return ret;
	}

	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @return The ams agent description.
	 */
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent)
	{
		return new AMSAgentDescription(agent);
	}

	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @param state The state.
	 *  @param ownership The ownership.
	 *  @return The ams agent description.
	 */
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent, String state, String ownership)
	{
		AMSAgentDescription	ret	= new AMSAgentDescription(agent);
		ret.setState(state);
		ret.setOwnership(ownership);
		return ret;
	}

	/**
	 *  Shutdown the platform.
	 *  @param listener The listener.
	 */
	public void shutdownPlatform(IResultListener listener)
	{
		// todo
//		platform.shutdown(listener);
	}

	//-------- Helper methods --------
		
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public IAgentAdapter[] getAgentAdapters()
	{
		synchronized(adapters)
		{
			return (IAgentAdapter[])adapters.values().toArray(new IAgentAdapter[adapters.size()]);
		}
	}
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public Map getAgentAdapterMap()
	{
		return adapters;
	}
	
	/**
	 *  Copy and refresh local agent identifier.
	 *  @param aid The agent identifier.
	 *  @return The refreshed copy of the aid.
	 */
	public IAgentIdentifier refreshAgentIdentifier(IAgentIdentifier aid)
	{
		IAgentIdentifier	ret	= (IAgentIdentifier)((AgentIdentifier)aid).clone();
		if(adapters.containsKey(aid))
		{
			IMessageService	ms	= (IMessageService)platform.getService(IMessageService.class);
			if(ms!=null)
				((AgentIdentifier)ret).setAddresses(ms.getAddresses());
		}
		return ret;
	}
	
	/**
	 *  Create an agent name that is not yet used on the platform.
	 *  @param typename The type name.
	 *  @return The agent name.
	 */
	protected String generateAgentName(String typename)
	{
		AgentIdentifier ret = null;

		synchronized(adapters)
		{
			do
			{
				ret = new AgentIdentifier(typename+(agentcnt++)+"@"+platform.getName()); // Hack?!
			}
			while(adapters.containsKey(ret));
		}
		
		return ret.getLocalName();
	}
	
	//-------- listener methods --------
	
	/**
     *  Add an ams listener.
     *  The listener is registered for ams changes.
     *  @param listener  The listener to be added.
     */
    public void addAMSListener(IAMSListener listener)
	{
		synchronized(listeners)
		{
			listeners.add(listener);
		}
    }
    
    /**
     *  Remove an ams listener.
     *  @param listener  The listener to be removed.
     */
    public void removeAMSListener(IAMSListener listener)
	{
		synchronized(listeners)
		{
			listeners.remove(listener);
		}
    }

	/**
	 *  Get the short type name from a model filename.
	 *  @param filename The filename.
	 *  @return The short type name.
	 */
	public String getShortName(String filename)
	{
		IAgentModel	model	= platform.getAgentFactory().loadModel(filename);
		return model.getName();
	}

	/**
	 *  Command that is executed on agent cleanup.
	 */
	class CleanupCommand implements IResultListener
	{
		protected IAgentIdentifier aid;
		protected IResultListener listener;
		
		public CleanupCommand(IAgentIdentifier aid, IResultListener listener)
		{
			this.aid = aid;
			this.listener = listener;
		}
		
		public void resultAvailable(Object result)
		{
			synchronized(adapters)
			{
				JadeAgentAdapter adapter	= (JadeAgentAdapter)adapters.remove(aid);
				if(adapter==null)
					throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				adapter.setState(IAMSAgentDescription.STATE_TERMINATED);
				
				// Stop execution of agent.
				adapter.cleanupAgent();
			}
			
			IAMSListener[]	alisteners;
			synchronized(listeners)
			{
				alisteners	= (IAMSListener[])listeners.toArray(new IAMSListener[listeners.size()]);
			}
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				AMSAgentDescription ad = new AMSAgentDescription();
				ad.setName(aid);
				alisteners[i].agentRemoved(ad);
			}
			
			if(listener!=null)
				listener.resultAvailable(null);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			resultAvailable(null);
		}
	}
}

