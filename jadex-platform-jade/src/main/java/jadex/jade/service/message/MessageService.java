package jadex.jade.service.message;

import jade.content.Concept;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import jadex.base.fipa.CMSCreateComponent;
import jadex.base.fipa.CMSDestroyComponent;
import jadex.base.fipa.CMSResumeComponent;
import jadex.base.fipa.CMSSearchComponents;
import jadex.base.fipa.CMSSuspendComponent;
import jadex.base.fipa.DFDeregister;
import jadex.base.fipa.DFModify;
import jadex.base.fipa.DFRegister;
import jadex.base.fipa.DFSearch;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.SFipa;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IContentCodec;
import jadex.bridge.IMessageListener;
import jadex.bridge.IMessageService;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.jade.ComponentAdapterFactory;
import jadex.jade.ComponentAgent;
import jadex.jade.JadeComponentAdapter;
import jadex.jade.SJade;
import jadex.jade.service.ComponentManagementService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  The Message service serves several message-oriented purposes: a) sending and
 *  delivering messages by using transports 
 */
public class MessageService  extends BasicService implements IMessageService
{
	//-------- constants --------
	
	/** The default codecs. */
	public static IContentCodec[]	DEFCODECS	= new IContentCodec[]
	{
		new jadex.base.contentcodecs.JadexXMLContentCodec(),
		new jadex.base.contentcodecs.JavaXMLContentCodec(),
		new jadex.base.contentcodecs.NuggetsXMLContentCodec()
	};
	
	//-------- attributes --------

	/** The provider. */
    protected IServiceProvider provider;
	
	/** The transports. */
//	protected List transports;

	/** The message types. */
	protected Map messagetypes;

//	/** All addresses of this platform. */
//	private String[] addresses;

//	/** The send message action executed by platform executor. */
//	protected SendMessage sendmsg;
	
//	/** The deliver message action executed by platform executor. */
//	protected DeliverMessage delivermsg;
	
	/** The logger. */
	protected Logger logger;

	/** The listeners. */
	protected List listeners;
	
	/** The cashed clock service. */
	protected IClockService	clockservice;
	
	/** The cashed clock service. */
	protected IComponentManagementService cms;
	
	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(IServiceProvider provider, MessageType[] messagetypes)
	{
		super(provider.getId(), IMessageService.class, null);
		this.provider = provider;
		this.logger = Logger.getLogger("JADE_Platform.mts");
		
		this.messagetypes	= SCollection.createHashMap();
		for(int i=0; i<messagetypes.length; i++)
			this.messagetypes.put(messagetypes[i].getName(), messagetypes[i]);		
	}
	
	//-------- interface methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	public IFuture sendMessage(Map message, MessageType type, IComponentIdentifier sender, ClassLoader cl)
//	public void sendMessage(Map message, MessageType type, IComponentAdapter adapter, ClassLoader cl)
	{
		final Future ret = new Future();
		
//		IComponentIdentifier sender = adapter.getComponentIdentifier();
		if(sender==null)
			throw new RuntimeException("Sender must not be null: "+message);
		
		// Automatically add optional meta information.
		String senid = type.getSenderIdentifier();
		Object sen = message.get(senid);
		if(sen==null)
			message.put(senid, sender);
		
		String idid = type.getIdIdentifier();
		Object id = message.get(idid);
		if(id==null)
			message.put(idid, SUtil.createUniqueId(sender.getLocalName()));

		String sd = type.getTimestampIdentifier();
		Object senddate = message.get(sd);
		if(senddate==null)
		{
//			IClockService	clock	= (IClockService)platform.getService(IClockService.class);
			if(clockservice!=null)
				message.put(sd, ""+clockservice.getTime());
		}
		
		IComponentIdentifier[] receivers = null;
		Object tmp = message.get(type.getReceiverIdentifier());
		if(tmp instanceof Collection)
			receivers = (IComponentIdentifier[])((Collection)tmp).toArray(new IComponentIdentifier[0]);
		else
			receivers = (IComponentIdentifier[])tmp;
		
		if(receivers==null || receivers==new IComponentIdentifier[0])
		{
			throw new RuntimeException("Receivers must not be empty: "+message);
		}

		// Hack!!! Convert Jadex/Nuggets AMS/DF messages to FIPA.
		if(type.equals(SFipa.FIPA_MESSAGE_TYPE) && receivers.length==1)
		{
			if(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME.equals(message.get(SFipa.ONTOLOGY)))
			{
				Object	content	= message.get(SFipa.CONTENT);
				Concept	request	= null;
				String	ontology	= FIPAManagementOntology.NAME;	// Default for most AMS/DF requests
				
				if(content instanceof DFRegister)
				{
					IDFComponentDescription	dfadesc	= ((DFRegister)content).getComponentDescription();
					Register	register	= new Register();
					register.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= register;
				}
				else if(content instanceof DFModify)
				{
					IDFComponentDescription	dfadesc	= ((DFModify)content).getComponentDescription();
					Modify	modify	= new Modify();
					modify.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= modify;
				}
				else if(content instanceof DFSearch)
				{
					IDFComponentDescription	dfadesc	= ((DFSearch)content).getComponentDescription();
					Search	search	= new Search();
					search.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					ISearchConstraints	cons	= ((DFSearch)content).getSearchConstraints();
					if(cons!=null)
					{
						search.setConstraints(SJade.convertSearchConstraintstoJade(cons));
					}
					else
					{
						SearchConstraints	scon	= new SearchConstraints();
						scon.setMaxResults(new Long(-1));
						search.setConstraints(scon);
					}
					request	= search;
				}
				else if(content instanceof DFDeregister)
				{
					IDFComponentDescription	dfadesc	= ((DFDeregister)content).getComponentDescription();
					Deregister	deregister	= new Deregister();
					deregister.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= deregister;
				}
				else if(content instanceof CMSCreateComponent)
				{
					CMSCreateComponent	aca	= (CMSCreateComponent)content;
					if(aca.getName()==null)
					{
//						ComponentManagementService	ams	= (ComponentManagementService)platform.getService(IComponentManagementService.class);
//						aca.setName(ams.generateAgentName(ams.getShortName(aca.getType())));
						aca.setName(cms.generateComponentIdentifier(aca.getType()).getLocalName());
					}
					CreateAgent	create	= new CreateAgent();
					create.setAgentName(aca.getName());
					create.addArguments(aca.getType());
					create.addArguments(aca.getConfiguration()!=null ? aca.getConfiguration() : "");
					if(aca.getArguments()!=null)
						create.addArguments(aca.getArguments());
					create.setClassName(ComponentAgent.class.getName());
					if(aca.isSuspend())
						throw new RuntimeException("Delayed agent start not yet supported.");

					create.setContainer(new ContainerID("Main-Container", null));
					
					request	= create;
					ontology	= JADEManagementOntology.NAME;
				}
				// todo: start?
				/*else if(content instanceof CMSStartComponent)
				{
					IComponentIdentifier	amsaid	= ((AMSStartAgent)content).getComponentIdentifier();
					Modify	start	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.ACTIVE);
					start.setDescription(adesc);
					request	= start;
				}*/
				else if(content instanceof CMSDestroyComponent)
				{
					IComponentIdentifier	amsaid	= ((CMSDestroyComponent)content).getComponentIdentifier();
					KillAgent	destroy	= new KillAgent();
					destroy.setAgent(SJade.convertAIDtoJade(amsaid));
					request	= destroy;
					ontology	= JADEManagementOntology.NAME;
				}
				else if(content instanceof CMSSuspendComponent)
				{
					IComponentIdentifier	amsaid	= ((CMSSuspendComponent)content).getComponentIdentifier();
					Modify	suspend	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.SUSPENDED);
					suspend.setDescription(adesc);
					request	= suspend;
				}
				else if(content instanceof CMSResumeComponent)
				{
					IComponentIdentifier	amsaid	= ((CMSResumeComponent)content).getComponentIdentifier();
					Modify	resume	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.ACTIVE);
					resume.setDescription(adesc);
					request	= resume;
				}
				else if(content instanceof CMSSearchComponents)
				{
					Search	search	= new Search();
					AMSAgentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoJade(((CMSSearchComponents)content).getComponentDescription());
					// Hack !!! Strip addresses/resolvers from aid before search (JADE doesn't store these in AMS and therefore finds no matches, grrr).
					if(amsadesc.getName()!=null)
						amsadesc.setName(new AID(amsadesc.getName().getName(), AID.ISGUID));
					search.setDescription(amsadesc);
					ISearchConstraints	cons	= ((CMSSearchComponents)content).getSearchConstraints();
					if(cons!=null)
					{
						search.setConstraints(SJade.convertSearchConstraintstoJade(cons));
					}
					else
					{
						SearchConstraints	scon	= new SearchConstraints();
						scon.setMaxResults(new Long(-1));
						search.setConstraints(scon);
					}
					request	= search;
				}
				else
				{
					throw new RuntimeException("Action not supported: "+content);
				}

				Action	action	= new Action(SJade.convertAIDtoJade(receivers[0]), request);
				ContentManager	cm	= new ContentManager();
				cm.registerLanguage(new SLCodec(0));
				cm.registerOntology(FIPAManagementOntology.getInstance());
				cm.registerOntology(JADEManagementOntology.getInstance());
				ACLMessage	dummy	= new ACLMessage(0);
				dummy.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				dummy.setOntology(ontology);
				try
				{
					cm.fillContent(dummy, action);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				message.put(SFipa.CONTENT, dummy.getContent());
				message.put(SFipa.LANGUAGE, FIPANames.ContentLanguage.FIPA_SL0);
				message.put(SFipa.ONTOLOGY, ontology);
			}
		}

		// Conversion via platform specific codecs
		for(Iterator it=message.keySet().iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= message.get(name);
			IContentCodec	codec	= type.findContentCodec(DEFCODECS, message, name);
			if(codec!=null)
			{
				message.put(name, codec.encode(value, cl));
			}
			else if(value!=null && !(value instanceof String) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())))
			{	
				throw new ContentException("No content codec found for: "+name+", "+message);
			}
		}

		// Prepare message for Jade.
		final ACLMessage msg = SJade.convertMessagetoJade(message, type);
		
		// Send message over Jade.
//		ComponentManagementService ams = (ComponentManagementService)platform.getService(IComponentManagementService.class);
		
		JadeComponentAdapter adapter = ((ComponentManagementService)cms).getComponentAdapter(sender);
		adapter.getJadeAgent().send(msg);
		ret.setResult(null);
//		System.out.println("message sent: "+msg);
		
		return ret;
		
//		sendmsg.addMessage(message, type.getName(), receivers);
	}

	/**
	 *  Deliver a message to the intended agents. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public synchronized void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers)
	{	
		// Not necessary in JADE.
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(Map msg, MessageType mt)
	{
		Map reply = new HashMap();
		
		MessageType.ParameterSpecification[] params	= mt.getParameters();
		for(int i=0; i<params.length; i++)
		{
			String sourcename = params[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					reply.put(params[i].getName(), sourceval);
				}
			}
		}
		
		MessageType.ParameterSpecification[] paramsets = mt.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String sourcename = paramsets[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					List tmp = new ArrayList();
					tmp.add(sourceval);
					reply.put(paramsets[i].getName(), tmp);	
				}
			}
		}
		
		return reply;
	}

	/**
	 *  Get the adresses of an agent.
	 *  @return The addresses of this agent.
	 */
	public String[] getAddresses()
	{
		// Hack! Should be looked up dynamically.
		return ComponentAdapterFactory.getInstance().getGatewayAgent().getAddressesArray();
	}
	
	/**
	 *  Get addresses of all transports.
	 *  @return The address schemes of all transports.
	 */
	public String[] getAddressSchemes()
	{
		// todo
		return new String[0];
//		String[] ret = platform.getPlatformAgent().getAddressesArray();
		
	}
	
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 */
	public synchronized void addMessageListener(IMessageListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		listeners.add(listener);
	}
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public synchronized void removeMessageListener(IMessageListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	public MessageType getMessageType(String type)
	{
		return (MessageType)messagetypes.get(type);
	}

	//-------- IPlatformService interface --------
	
	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, IClockService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				clockservice = (IClockService)result;
				SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						cms = (IComponentManagementService)result;
						MessageService.super.startService().addResultListener(new DelegationResultListener(ret));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called when the platform shuts down. Do necessary cleanup here (if any).
	 * /
	public IFuture shutdownService()
	{
		return new Future(null);
	}*/
}


