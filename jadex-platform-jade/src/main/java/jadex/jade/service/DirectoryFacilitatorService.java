package jadex.jade.service;

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
import jadex.base.fipa.DFComponentDescription;
import jadex.base.fipa.DFServiceDescription;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.IProperty;
import jadex.base.fipa.SearchConstraints;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.jade.ComponentAdapterFactory;
import jadex.jade.SJade;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *  Directory facilitator implementation for JADE platform.
 */
public class DirectoryFacilitatorService extends BasicService implements IDF
{
	//-------- attributes --------

	/** The platform. */
	protected IServiceProvider provider;
	
	/** The logger. */
	//protected Logger logger;
	
	//-------- constructors --------

	/**
	 *  Create a standalone df.
	 */
	public DirectoryFacilitatorService(IServiceProvider provider)
	{
		super(provider.getId(), IDF.class, null);
		this.provider = provider;
		//this.logger = Logger.getLogger("DF" + this);
	}
	
	//-------- IDF interface methods --------

	/**
	 *  Register an component description.
	 *  @throws RuntimeException when the component is already registered.
	 */
	public IFuture register(final IDFComponentDescription adesc)
	{
		final Future ret	= new Future();
		
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
								ret.setResult(SJade.convertAgentDescriptiontoFipa(
									(jade.domain.FIPAAgentManagement.DFAgentDescription)reg.getDescription()));
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
						else
						{
							ret.setException(new RuntimeException("Register failed."));
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
			ComponentAdapterFactory.getInstance().getGatewayController().putO2AObject(e, AgentController.ASYNC);
		}
		catch(Exception ex)
		{
			ret.setException(ex);
		}
		
		return ret;
	}

	/**
	 *  Deregister an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture deregister(final IDFComponentDescription adesc)
	{
		final Future ret	= new Future();
		
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
								ret.setResult(null);
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
						else
						{
							ret.setException(new RuntimeException("Deregister failed."));
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
			ComponentAdapterFactory.getInstance().getGatewayController().putO2AObject(e, AgentController.ASYNC);
		}
		catch(Exception ex)
		{
			ret.setResult(ex);
		}
		
		return ret;
	}

	/**
	 *  Modify an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture modify(final IDFComponentDescription adesc)
	{
		final Future ret	= new Future();
		
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
								ret.setResult(SJade.convertAgentDescriptiontoFipa(
									(jade.domain.FIPAAgentManagement.DFAgentDescription)mod.getDescription()));
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
						else
						{
							ret.setException(new RuntimeException("Modify failed."));
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
			ComponentAdapterFactory.getInstance().getGatewayController().putO2AObject(e, AgentController.ASYNC);
		}
		catch(Exception ex)
		{
			ret.setException(ex);
		}
		
		return ret;
	}
	


	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture search(IDFComponentDescription adesc, ISearchConstraints con)
	{
		return search(adesc, con, false);
	}

	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture search(final IDFComponentDescription adesc, final ISearchConstraints con, boolean remote)
	{
		final Future ret	= new Future();
		
		// Todo: remote search!?
		if(remote)
		{
			ret.setException(new UnsupportedOperationException("Remote DF search not supported in JADE."));
			return ret;
		}
		
		
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
								IDFComponentDescription[] cret = new IDFComponentDescription[descs.size()];
//								IComponentManagementService ams = (IComponentManagementService)platform.getService(IComponentManagementService.class);
								for(int i=0; i<cret.length; i++)
								{
									cret[i] = SJade.convertAgentDescriptiontoFipa(
										(jade.domain.FIPAAgentManagement.DFAgentDescription)descs.get(i));
								}
								ret.setResult(cret);
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
						else
						{
							ret.setException(new RuntimeException("Search failed."));
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
			ComponentAdapterFactory.getInstance().getGatewayController().putO2AObject(e, AgentController.ASYNC);
		}
		catch(Exception ex)
		{
			ret.setException(ex);
		}
		
		return ret;
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
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier agent, IDFServiceDescription service)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
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
	public IDFComponentDescription	createDFComponentDescription(IComponentIdentifier agent, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime)
	{
		DFComponentDescription ret = new DFComponentDescription();
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
		SearchConstraints ret	= new SearchConstraints();
		ret.setMaxResults(maxresults);
		ret.setMaxDepth(maxdepth);
		return ret;
	}

	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name ().
	 *  @return The new agent identifier.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
	{
		return cms.createComponentIdentifier(name, local, null);
		
//		if(local)
//			name = name + "@" + platform.getName();
//		return new AgentIdentifier(name);
	}*/
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses)
	{
		return cms.createComponentIdentifier(name, local, addresses);

//		if(local)
//			name = name + "@" + platform.getName();
//		return new AgentIdentifier(name, addresses, null);
	}*/
	
	//-------- helper methods --------

	/**
	 *  Test if an agent description matches a given template.
	 */
	protected boolean	match(IDFComponentDescription desc, IDFComponentDescription template)
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
	
	//-------- IPlatformService interface --------
	
	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		return IFuture.DONE;
	}
}
