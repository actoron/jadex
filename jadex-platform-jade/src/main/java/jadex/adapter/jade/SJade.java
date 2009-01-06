package jadex.adapter.jade;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.adapter.jade.fipaimpl.AgentIdentifier;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.collection.SCollection;

import java.util.Iterator;
import java.util.Map;


/**
 *  Helper class for JADE specific issues.
 */
public class SJade
{
	//-------- constants --------

	/** The JADE platform type. */
	public static final String	PLATFORM_TYPE	= "jade";
	
	// Names of the various fields of an ACL messages.
	/*public static final String ENCODING = "encoding";
	public static final String IN_REPLY_TO = "in-reply-to";
	public static final String LANGUAGE = "language";
	public static final String ONTOLOGY = "ontology";
	public static final String PROTOCOL = "protocol";
	public static final String REPLY_BY = "reply-by";
	public static final String REPLY_WITH = "reply-with";
	public static final String RECEIVER = "receiver";
	public static final String REPLY_TO = "reply-to";
	public static final String PERFORMATIVE = "performative";
	public static final String CONTENT = "content";
	public static final String SENDER = "sender";
	public static final String REPLY_BY_DATE = "reply-by-date";
	public static final String CONTENT_START = "content-start";
	public static final String CONTENT_CLASS = "content-class";
	public static final String ACTION_CLASS = "action-class";
	public static final String CONVERSATION_ID = "conversation-id";

	/** The allowed message attributes. */
	/*public static Set MESSAGE_ATTRIBUTES;

	static
	{
		SJade.MESSAGE_ATTRIBUTES = SCollection.createHashSet();
		SJade.MESSAGE_ATTRIBUTES.add(SJade.CONVERSATION_ID);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.ENCODING);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.IN_REPLY_TO);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.LANGUAGE);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.ONTOLOGY);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.PROTOCOL);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.REPLY_BY);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.REPLY_WITH);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.RECEIVER);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.REPLY_TO);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.PERFORMATIVE);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.CONTENT);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.SENDER);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.REPLY_BY_DATE);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.CONTENT_START);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.CONTENT_CLASS);
		SJade.MESSAGE_ATTRIBUTES.add(SJade.ACTION_CLASS);
	}*/

	protected static Map PERFORMATIVES_MAP_TO_JADE;
	protected static Map PERFORMATIVES_MAP_TO_FIPA;
	protected static Map STATES_MAP_TO_JADE;
	protected static Map STATES_MAP_TO_FIPA;
	static
	{
		PERFORMATIVES_MAP_TO_JADE = SCollection.createHashMap();
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.ACCEPT_PROPOSAL, new Integer(ACLMessage.ACCEPT_PROPOSAL));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.AGREE, new Integer(ACLMessage.AGREE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.CANCEL, new Integer(ACLMessage.CANCEL));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.CFP, new Integer(ACLMessage.CFP));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.CONFIRM, new Integer(ACLMessage.CONFIRM));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.DISCONFIRM, new Integer(ACLMessage.DISCONFIRM));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.FAILURE, new Integer(ACLMessage.FAILURE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.INFORM, new Integer(ACLMessage.INFORM));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.INFORM_IF, new Integer(ACLMessage.INFORM_IF));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.INFORM_REF, new Integer(ACLMessage.INFORM_REF));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.NOT_UNDERSTOOD, new Integer(ACLMessage.NOT_UNDERSTOOD));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.PROPOSE, new Integer(ACLMessage.PROPOSE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.QUERY_IF, new Integer(ACLMessage.QUERY_IF));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.QUERY_REF, new Integer(ACLMessage.QUERY_REF));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.REFUSE, new Integer(ACLMessage.REFUSE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.REJECT_PROPOSAL, new Integer(ACLMessage.REJECT_PROPOSAL));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.REQUEST, new Integer(ACLMessage.REQUEST));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.REQUEST_WHEN, new Integer(ACLMessage.REQUEST_WHEN));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.REQUEST_WHENEVER, new Integer(ACLMessage.REQUEST_WHENEVER));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.SUBSCRIBE, new Integer(ACLMessage.SUBSCRIBE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.PROXY, new Integer(ACLMessage.PROXY));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.PROPAGATE, new Integer(ACLMessage.PROPAGATE));
		PERFORMATIVES_MAP_TO_JADE.put(SFipa.UNKNOWN, new Integer(ACLMessage.UNKNOWN));

		PERFORMATIVES_MAP_TO_FIPA = SCollection.createHashMap();
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.ACCEPT_PROPOSAL), SFipa.ACCEPT_PROPOSAL);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.AGREE), SFipa.AGREE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.CANCEL), SFipa.CANCEL);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.CFP), SFipa.CFP);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.CONFIRM), SFipa.CONFIRM);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.DISCONFIRM), SFipa.DISCONFIRM);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.FAILURE), SFipa.FAILURE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.INFORM), SFipa.INFORM);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.INFORM_IF), SFipa.INFORM_IF);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.INFORM_REF), SFipa.INFORM_REF);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.NOT_UNDERSTOOD), SFipa.NOT_UNDERSTOOD);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.PROPOSE), SFipa.PROPOSE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.QUERY_IF), SFipa.QUERY_IF);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.QUERY_REF), SFipa.QUERY_REF);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.REFUSE), SFipa.REFUSE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.REJECT_PROPOSAL), SFipa.REJECT_PROPOSAL);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.REQUEST), SFipa.REQUEST);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.REQUEST_WHEN), SFipa.REQUEST_WHEN);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.REQUEST_WHENEVER), SFipa.REQUEST_WHENEVER);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.SUBSCRIBE), SFipa.SUBSCRIBE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.PROXY), SFipa.PROXY);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.PROPAGATE), SFipa.PROPAGATE);
		PERFORMATIVES_MAP_TO_FIPA.put(new Integer(ACLMessage.UNKNOWN), SFipa.UNKNOWN);
		
		STATES_MAP_TO_FIPA = SCollection.createHashMap();
		STATES_MAP_TO_FIPA.put(jade.domain.FIPAAgentManagement.AMSAgentDescription.ACTIVE, 
			jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_ACTIVE);
		STATES_MAP_TO_FIPA.put(jade.domain.FIPAAgentManagement.AMSAgentDescription.SUSPENDED, 
			jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_SUSPENDED);
		STATES_MAP_TO_FIPA.put(jade.domain.FIPAAgentManagement.AMSAgentDescription.INITIATED, 
			jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_INITIATED);
		STATES_MAP_TO_FIPA.put(jade.domain.FIPAAgentManagement.AMSAgentDescription.WAITING, 
			jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_WAITING);
		STATES_MAP_TO_FIPA.put(jade.domain.FIPAAgentManagement.AMSAgentDescription.TRANSIT, 
			jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_TRANSIT);
		
		STATES_MAP_TO_JADE = SCollection.createHashMap();
		STATES_MAP_TO_JADE.put(jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_ACTIVE, 
			jade.domain.FIPAAgentManagement.AMSAgentDescription.ACTIVE);
		STATES_MAP_TO_JADE.put(jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_SUSPENDED,
			jade.domain.FIPAAgentManagement.AMSAgentDescription.SUSPENDED);
		STATES_MAP_TO_JADE.put(jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_INITIATED,
			jade.domain.FIPAAgentManagement.AMSAgentDescription.INITIATED);
		STATES_MAP_TO_JADE.put(jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_WAITING,
			jade.domain.FIPAAgentManagement.AMSAgentDescription.WAITING);
		STATES_MAP_TO_JADE.put(jadex.adapter.jade.fipaimpl.AMSAgentDescription.STATE_TRANSIT,
			jade.domain.FIPAAgentManagement.AMSAgentDescription.TRANSIT);
	}
	


	//-------- static helper methods --------

	/**
	 *  Create an agent id with address.
	 *  @param gid	The global name.
	 *  @param address	The transport address.
	 */
	public static AID	createAID(String gid, String address)
	{
		AID	aid	= new AID(gid, jade.core.AID.ISGUID);
		if(address!=null)
			aid.addAddresses(address);
		return aid;
	}

	/**
	 *  Create a service description.
	 *  @param name	The service name.
	 *  @param type	The service type.
	 *  @param ownership	The ownership of the service.
	 */
	public static ServiceDescription	createServiceDescription(String name, String type, String ownership)
	{
		ServiceDescription	ret	= new ServiceDescription();
		ret.setName(name);
		ret.setType(type);
		ret.setOwnership(ownership);
		return ret;
	}

	/**
	 *  Create a service description.
	 *  @param name	The service name.
	 *  @param type	The service type.
	 *  @param ownership	The ownership of the service.
	 *  @param languages	The languages understood by the service.
	 *  @param ontologies	The ontologies known by the service.
	 *  @param protocols	The protocols used by the service.
	 *  @param properties	Any additional service properties.
	 */
	public static ServiceDescription	createServiceDescription(String name, String type,
		String ownership, String[] languages, String[] ontologies, String[] protocols, Property[] properties)
	{
		ServiceDescription	ret	= new ServiceDescription();
		ret.setName(name);
		ret.setType(type);
		ret.setOwnership(ownership);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguages(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntologies(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocols(protocols[i]);
		for(int i=0; properties!=null && i<properties.length; i++)
			ret.addProperties(properties[i]);
		return ret;
	}

	/**
	 *  Create an agent description.
	 *  @param agent	The agent id.
	 *  @param service	The agent service.
	 */
	public static DFAgentDescription	createAgentDescription(AID agent, ServiceDescription service)
	{
		DFAgentDescription	ret	= new DFAgentDescription();
		ret.setName(agent);
		if(service!=null)
			ret.addServices(service);
		return ret;
	}

	/**
	 *  Create an agent description.
	 *  @param agent	The agent id.
	 *  @param services	The agent service.
	 *  @param languages	The languages understood by the service.
	 *  @param ontologies	The ontologies known by the service.
	 *  @param protocols	The protocols used by the service.
	 */
	public static DFAgentDescription	createAgentDescription(AID agent, ServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols)
	{
		DFAgentDescription	ret	= new DFAgentDescription();
		ret.setName(agent);
		for(int i=0; services!=null && i<services.length; i++)
			ret.addServices(services[i]);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguages(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntologies(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocols(protocols[i]);
		return ret;
	}

	/**
	 *  Convert a Fipa aid to a Jade AID.
	 */
	public static AID convertAIDtoJade(IAgentIdentifier aid)
	{
		assert aid!=null;
		AID ret = new AID(aid.getName(), AID.ISGUID);
		String[] addresses = aid.getAddresses();
		for(int i=0; i<addresses.length; i++)
			ret.addAddresses(addresses[i]); // Addresses are also string. no conversion.
//		AgentIdentifier[] resolvers = aid.getResolvers();
//		for(int i=0; i<resolvers.length; i++)
//			ret.addResolvers(convertAIDtoJade(resolvers[i])); // todo: can produce endless loop.
		return ret;
	}

	/**
	 *  Convert a Jade AID to a Fipa aid.
	 */
	public static IAgentIdentifier convertAIDtoFipa(AID aid, IAMS ams)
//	public static AgentIdentifier convertAIDtoFipa(AID aid)
	{
//		AgentIdentifier ret = new AgentIdentifier(aid.getName(), false);
		IAgentIdentifier ret = ams.createAgentIdentifier(aid.getName(), false, aid.getAddressesArray());
		
//		String[] addresses = aid.getAddressesArray();
//		for(int i=0; i<addresses.length; i++)
//			ret.addAddress(addresses[i]); // Addresses are also string. no conversion.
//		AID[] resolvers = aid.getResolversArray();
//		for(int i=0; i<resolvers.length; i++)
//			ret.addResolver(convertAIDtoFipa(resolvers[i])); // todo: can produce endless loop.
		return ret;
	}

	/**
	 *  Convert a Fipa performative to a Jade performative.
	 */
	public static int convertPerformativetoJade(String per)
	{
		Integer ret = (Integer)PERFORMATIVES_MAP_TO_JADE.get(per);
		if(ret==null)
			throw new RuntimeException("Unknown fipa performative: "+per);
		return ret.intValue();
	}

	/**
	 *  Convert a Jade performative to a Fipa performative.
	 */
	public static String convertPerformativetoFipa(int per)
	{
		String ret = (String)PERFORMATIVES_MAP_TO_FIPA.get(new Integer(per));
		if(ret==null)
			throw new RuntimeException("Unknown Jade performative: "+per);
		return ret;
	}

	/**
	 *  Convert search constraints to Jade.
	 */
	public static SearchConstraints convertSearchConstraintstoJade(jadex.adapter.base.fipa.ISearchConstraints con)
	{
		SearchConstraints constraints = new SearchConstraints();
		constraints.setMaxDepth(new Long(con.getMaxDepth()));
		constraints.setMaxResults(new Long(con.getMaxResults()));
		constraints.setSearchId(con.getSearchId());
		return constraints;
	}

	/**
	 *  Convert search constraints to Jadex fipa.
	 */
	public static jadex.adapter.jade.fipaimpl.SearchConstraints convertSearchConstraintstoFipa(SearchConstraints con)
	{
		jadex.adapter.jade.fipaimpl.SearchConstraints constraints = new jadex.adapter.jade.fipaimpl.SearchConstraints();
		constraints.setMaxDepth(con.getMaxDepth().intValue());
		constraints.setMaxResults(con.getMaxResults().intValue());
		constraints.setSearchId(con.getSearchId());
		return constraints;
	}

	/**
	 *  Get the Jade AID for a value.
	 */
	public static AID getAID(Object val)
	{
		if(!(val instanceof AID || val instanceof AgentIdentifier))
			throw new RuntimeException("Value must be AgentIdentifier or AID: "+val);
		return val instanceof AID? (AID)val: SJade.convertAIDtoJade((AgentIdentifier)val);
	}

	/**
	 *  Get the performative for a value.
	 */
	public static int getPerformative(Object val)
	{
		if(!(val instanceof Integer || val instanceof String))
			throw new RuntimeException("Value must be int or String: "+val);
		return val instanceof Integer? ((Integer)val).intValue()
			: SJade.convertPerformativetoJade((String)val);
	}

	/**
	 *  Convert a Jade AID to a Fipa aid.
	 */
	public static jadex.adapter.jade.fipaimpl.DFAgentDescription convertAgentDescriptiontoFipa(DFAgentDescription desc, IAMS ams)
	{
		jadex.adapter.jade.fipaimpl.DFAgentDescription ret = new jadex.adapter.jade.fipaimpl.DFAgentDescription(SJade.convertAIDtoFipa(desc.getName(), ams));
		Iterator it = desc.getAllLanguages();
		while(it.hasNext())
			ret.addLanguage((String)it.next());
		it = desc.getAllOntologies();
		while(it.hasNext())
			ret.addOntology((String)it.next());
		it = desc.getAllProtocols();
		while(it.hasNext())
			ret.addProtocol((String)it.next());
		it = desc.getAllServices();
		while(it.hasNext())
			ret.addService(SJade.convertServiceDescriptiontoFipa((ServiceDescription)it.next()));
		ret.setLeaseTime(desc.getLeaseTime());

		return ret;
	}

	/**
	 *  Convert a Jade AID to a Fipa aid.
	 */
	public static DFAgentDescription convertAgentDescriptiontoJade(jadex.adapter.base.fipa.IDFAgentDescription desc)
	{
		DFAgentDescription ret = new DFAgentDescription();
		if(desc.getName()!=null)
			ret.setName(SJade.convertAIDtoJade((AgentIdentifier)desc.getName())); // cast ok?
		String[] langs = desc.getLanguages();
		for(int i=0; i<langs.length; i++)
			ret.addLanguages(langs[i]);
		String[] ontos = desc.getOntologies();
		for(int i=0; i<ontos.length; i++)
			ret.addOntologies(ontos[i]);
		String[] prots = desc.getProtocols();
		for(int i=0; i<prots.length; i++)
			ret.addProtocols(prots[i]);
		jadex.adapter.base.fipa.IDFServiceDescription[] servs = desc.getServices();
		for(int i=0; i<servs.length; i++)
			ret.addServices(SJade.convertServiceDescriptiontoJade(servs[i]));
		ret.setLeaseTime(desc.getLeaseTime());

		return ret;
	}

	/**
	 *  Convert a Jade AID to a Fipa aid.
	 */
	public static jadex.adapter.jade.fipaimpl.DFServiceDescription
		convertServiceDescriptiontoFipa(ServiceDescription desc)
	{
		jadex.adapter.jade.fipaimpl.DFServiceDescription ret = new jadex.adapter.jade.fipaimpl.DFServiceDescription(
			desc.getName(), desc.getType(), desc.getOwnership());
		Iterator it = desc.getAllLanguages();
		while(it.hasNext())
			ret.addLanguage((String)it.next());
		it = desc.getAllOntologies();
		while(it.hasNext())
			ret.addOntology((String)it.next());
		it = desc.getAllProperties();
		while(it.hasNext())
		{
			jade.domain.FIPAAgentManagement.Property	prop	= (jade.domain.FIPAAgentManagement.Property)it.next();
			ret.addProperty(new jadex.adapter.jade.fipaimpl.Property(prop.getName(), prop.getValue()));
		}
		it = desc.getAllProtocols();
		while(it.hasNext())
			ret.addProtocol((String)it.next());
		return ret;
	}

	/**
	 *  Convert a Jade AID to a Fipa aid.
	 */
	public static ServiceDescription
		convertServiceDescriptiontoJade(jadex.adapter.base.fipa.IDFServiceDescription desc)
	{
		ServiceDescription ret = new ServiceDescription();
		ret.setName(desc.getName());
		ret.setType(desc.getType());
		ret.setOwnership(desc.getOwnership());

		String[] langs = desc.getLanguages();
		for(int i=0; i<langs.length; i++)
			ret.addLanguages(langs[i]);
		String[] ontos = desc.getOntologies();
		for(int i=0; i<ontos.length; i++)
			ret.addOntologies(ontos[i]);
		String[] prots = desc.getProtocols();
		for(int i=0; i<prots.length; i++)
			ret.addProtocols(prots[i]);
		jadex.adapter.base.fipa.IProperty[] props = desc.getProperties();
		for(int i=0; i<props.length; i++)
			ret.addProperties(new jade.domain.FIPAAgentManagement.Property(props[i].getName(), props[i].getValue()));
		return ret;
	}

	/**
	 *  Convert an AMS AD to JADE.
	 */
	public static AMSAgentDescription convertAMSAgentDescriptiontoJade(jadex.adapter.base.fipa.IAMSAgentDescription desc)
	{
		AMSAgentDescription ret = new AMSAgentDescription();
		if(desc.getName()!=null)
			ret.setName(SJade.convertAIDtoJade(desc.getName()));
		ret.setOwnership(desc.getOwnership());
		ret.setState((String)STATES_MAP_TO_JADE.get(desc.getState()));
		return ret;
	}

	/**
	 *  Convert an AMS AD to FIPA.
	 */
	public static jadex.adapter.jade.fipaimpl.AMSAgentDescription  convertAMSAgentDescriptiontoFipa(AMSAgentDescription desc, IAMS ams)
	{
		jadex.adapter.jade.fipaimpl.AMSAgentDescription ret = new jadex.adapter.jade.fipaimpl.AMSAgentDescription();
		if(desc.getName()!=null)
			ret.setName(SJade.convertAIDtoFipa(desc.getName(), ams));
		ret.setOwnership(desc.getOwnership());
		ret.setState((String)STATES_MAP_TO_FIPA.get(desc.getState()));
		return ret;
	}
}
