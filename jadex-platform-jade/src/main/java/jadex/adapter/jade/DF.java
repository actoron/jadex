package jadex.adapter.jade;

import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Search;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Event;
import jade.wrapper.AgentController;
import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.IProperty;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.jade.fipaimpl.AgentIdentifier;
import jadex.adapter.jade.fipaimpl.DFAgentDescription;
import jadex.adapter.jade.fipaimpl.DFServiceDescription;
import jadex.adapter.jade.fipaimpl.SearchConstraints;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *  Directory facilitator implementation for standalone platform.
 */
public class DF implements IDF, IService
{
	//-------- attributes --------

	/** The platform. */
	protected Platform platform;
	
	/** The logger. */
	//protected Logger logger;
	
	//-------- constructors --------

	/**
	 *  Create a standalone df.
	 */
	public DF(Platform platform)
	{
		this.platform = platform;
		//this.logger = Logger.getLogger("DF" + this);
	}
	
	//-------- IDF interface methods --------

	/**
	 *  Register an agent description.
	 *  @throws RuntimeException when the agent is already registered.
	 */
	public void	register(final IDFAgentDescription adesc, IResultListener lis)
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
//						System.out.println("Reply received: "+reply);
						if(reply.getPerformative()==ACLMessage.INFORM)
						{
							try
							{
								Done done = (Done)myAgent.getContentManager().extractContent(reply);
								Register reg = (Register)((Action)done.getAction()).getAction();
								listener.resultAvailable(SJade.convertAgentDescriptiontoFipa(
									(jade.domain.FIPAAgentManagement.DFAgentDescription)reg.getDescription(), 
									(IAMS)platform.getService(IAMS.class)));
							}
							catch(Exception e)
							{
								listener.exceptionOccurred(e);
							}
						}
						else
						{
							listener.exceptionOccurred(new RuntimeException("Register failed."));
						}
						done = true;
					}
				}
				else
				{
					try 
					{
						Register r = new Register();
						r.setDescription(SJade.convertAgentDescriptiontoJade(adesc));
						Action ac = new Action();
						ac.setActor(myAgent.getDefaultDF());
						ac.setAction(r);

						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(myAgent.getDefaultDF());
						request.setSender(myAgent.getAID());
						request.setOntology(FIPAManagementOntology.NAME);
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
		catch(Exception ex)
		{
			listener.exceptionOccurred(ex);
		}
	}

	/**
	 *  Deregister an agent description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	deregister(final IDFAgentDescription adesc, IResultListener lis)
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
//						System.out.println("Reply received: "+reply);
						if(reply.getPerformative()==ACLMessage.INFORM)
						{
							try
							{
								listener.resultAvailable(null);
							}
							catch(Exception e)
							{
								listener.exceptionOccurred(e);
							}
						}
						else
						{
							listener.exceptionOccurred(new RuntimeException("Deregister failed."));
						}
						done = true;
					}
				}
				else
				{
					try 
					{
						Deregister r = new Deregister();
						r.setDescription(SJade.convertAgentDescriptiontoJade(adesc));
						Action ac = new Action();
						ac.setActor(myAgent.getDefaultDF());
						ac.setAction(r);

						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(myAgent.getDefaultDF());
						request.setSender(myAgent.getAID());
						request.setOntology(FIPAManagementOntology.NAME);
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
		catch(Exception ex)
		{
			listener.exceptionOccurred(ex);
		}
	}

	/**
	 *  Modify an agent description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	modify(final IDFAgentDescription adesc, IResultListener lis)
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
//						System.out.println("Reply received: "+reply);
						if(reply.getPerformative()==ACLMessage.INFORM)
						{
							try
							{
								Done done = (Done)myAgent.getContentManager().extractContent(reply);
								Modify mod = (Modify)((Action)done.getAction()).getAction();
								listener.resultAvailable(SJade.convertAgentDescriptiontoFipa(
									(jade.domain.FIPAAgentManagement.DFAgentDescription)mod.getDescription(), 
									(IAMS)platform.getService(IAMS.class)));
							}
							catch(Exception e)
							{
								listener.exceptionOccurred(e);
							}
						}
						else
						{
							listener.exceptionOccurred(new RuntimeException("Modify failed."));
						}
						done = true;
					}
				}
				else
				{
					try 
					{
						Modify r = new Modify();
						r.setDescription(SJade.convertAgentDescriptiontoJade(adesc));
						Action ac = new Action();
						ac.setActor(myAgent.getDefaultDF());
						ac.setAction(r);

						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(myAgent.getDefaultDF());
						request.setSender(myAgent.getAID());
						request.setOntology(FIPAManagementOntology.NAME);
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
		catch(Exception ex)
		{
			listener.exceptionOccurred(ex);
		}
	}

	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions. 
	 */
	public void	search(final IDFAgentDescription adesc, final ISearchConstraints con, IResultListener lis)
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
//						System.out.println("Reply received: "+reply);
						if(reply.getPerformative()==ACLMessage.INFORM)
						{
							try
							{
								Result res = (Result)myAgent.getContentManager().extractContent(reply);
								jade.util.leap.List descs = res.getItems();
								IDFAgentDescription[] ret = new IDFAgentDescription[descs.size()];
								IAMS ams = (IAMS)platform.getService(IAMS.class);
								for(int i=0; i<ret.length; i++)
								{
									ret[i] = SJade.convertAgentDescriptiontoFipa(
										(jade.domain.FIPAAgentManagement.DFAgentDescription)descs.get(i), ams);
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
						Search s = new Search();
						s.setDescription(SJade.convertAgentDescriptiontoJade(adesc));
						s.setConstraints(con!=null? SJade.convertSearchConstraintstoJade(con): 
							new jade.domain.FIPAAgentManagement.SearchConstraints());
						Action ac = new Action();
						ac.setActor(myAgent.getDefaultDF());
						ac.setAction(s);

						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(myAgent.getDefaultDF());
						request.setSender(myAgent.getAID());
						request.setOntology(FIPAManagementOntology.NAME);
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
		catch(Exception ex)
		{
			listener.exceptionOccurred(ex);
		}
	}

	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership)
	{
		return new DFServiceDescription(name, type, ownership);
	}
	
	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @param properties The properties.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership,
		String[] languages, String[] ontologies, String[] protocols, IProperty[] properties)
	{
		DFServiceDescription	ret	= new DFServiceDescription(name, type, ownership);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguage(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntology(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocol(protocols[i]);
		for(int i=0; properties!=null && i<properties.length; i++)
			ret.addProperty(properties[i]);
		return ret;
	}

	/**
	 *  Create a df agent description.
	 *  @param agent The agent.
	 *  @param service The service.
	 *  @return The df agent description.
	 */
	public IDFAgentDescription createDFAgentDescription(IAgentIdentifier agent, IDFServiceDescription service)
	{
		DFAgentDescription	ret	= new DFAgentDescription();
		ret.setName(agent);
		if(service!=null)
			ret.addService(service);
		return ret;
	}

	/**
	 *  Create a new df agent description.
	 *  @param agent The agent id.
	 *  @param services The services.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @return The agent description.
	 */
	public IDFAgentDescription	createDFAgentDescription(IAgentIdentifier agent, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime)
	{
		DFAgentDescription	ret	= new DFAgentDescription();
		ret.setName(agent);
		ret.setLeaseTime(leasetime);
		for(int i=0; services!=null && i<services.length; i++)
			ret.addService(services[i]);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguage(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntology(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocol(protocols[i]);
		return ret;
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
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name ().
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
	 *  Do necessary cleanup here (if any).
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		listener.resultAvailable(null);
	}

	//-------- helper methods --------

	/**
	 *  Test if an agent description matches a given template.
	 */
	protected boolean	match(IDFAgentDescription desc, IDFAgentDescription template)
	{
		boolean	ret	= true;

		// Match protocols, languages, and ontologies.
		ret	= includes(desc.getLanguages(), template.getLanguages());
		ret	= ret && includes(desc.getOntologies(), template.getOntologies());
		ret	= ret && includes(desc.getProtocols(), template.getProtocols());

		// Match service descriptions.
		if(ret)
		{
			IDFServiceDescription[]	tservices	= template.getServices();
			for(int t=0; ret && t<tservices.length; t++)
			{
				ret	= false;
				IDFServiceDescription[]	dservices	= desc.getServices();
				for(int d=0; !ret && d<dservices.length; d++)
				{
					ret	= match(dservices[d], tservices[t]);
				}
			}
		}

		return ret;
	}

	/**
	 *  Test if a service description matches a given template.
	 */
	protected boolean	match(IDFServiceDescription desc, IDFServiceDescription template)
	{
		// Match name, type, and ownership;
		boolean	ret	= template.getName()==null || template.getName().equals(desc.getName());
		ret	= ret && (template.getType()==null || template.getType().equals(desc.getType()));
		ret	= ret && (template.getOwnership()==null || template.getOwnership().equals(desc.getOwnership()));

		// Match protocols, languages, ontologies, and properties.
		ret	= ret && includes(desc.getLanguages(), template.getLanguages());
		ret	= ret && includes(desc.getOntologies(), template.getOntologies());
		ret	= ret && includes(desc.getProtocols(), template.getProtocols());
		ret	= ret && includes(desc.getProperties(), template.getProperties());

		return ret;
	}

	/**
	 *  Test if one array of objects is included in the other
	 *  (without considering the order).
	 *  Test is performed using equals().
	 */
	protected boolean	includes(Object[] a, Object[] b)
	{
		Set	entries	= new HashSet();
		for(int i=0; i<b.length; i++)
			entries.add(b[i]);
		for(int i=0; i<a.length; i++)
			entries.remove(a[i]);
		return entries.isEmpty();
	}
}
