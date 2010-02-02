package jadex.adapter.jade.fipaimpl;

import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *  An agent description.
 */
public class DFAgentDescription implements IDFComponentDescription, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot languages. */
	protected List languages;

	/** Attribute for slot agentidentifier. */
	protected IComponentIdentifier name;

	/** Attribute for slot ontologies. */
	protected List ontologies;

	/** Attribute for slot services. */
	protected List services;

	/** Attribute for slot lease-time. */
	protected java.util.Date leasetime;

	/** Attribute for slot protocols. */
	protected List protocols;

	//-------- constructor --------

	/**
	 *  Create a new agent description.
	 */
	public DFAgentDescription()
	{
		this(null);
	}

	/**
	 *  Create a new agent description.
	 *  @param name The name.
	 */
	public DFAgentDescription(IComponentIdentifier name)
	{
		this(name, null, null, null, null, null);
	}

	/**
	 *  Create a new agent description.
	 *  @param name The name.
	 *  @param services The services.
	 *  @param protocols The protocols.
	 *  @param ontologies The ontologies.
	 *  @param languages The languages.
	 */
	public DFAgentDescription(IComponentIdentifier name, IDFServiceDescription[] services, 
		String[] protocols, String[] ontologies, String[] languages, Date leasetime)
	{
		this.languages = new ArrayList();
		this.ontologies = new ArrayList();
		this.services = new ArrayList();
		this.protocols = new ArrayList();

		this.setName(name);
		this.setLeaseTime(leasetime);
		if(services != null)
			for(int i = 0; i < services.length; i++)
				this.addService(services[i]);
		if(protocols != null)
			for(int i = 0; i < protocols.length; i++)
				this.addProtocol(protocols[i]);
		if(languages != null)
			for(int i = 0; i < languages.length; i++)
				this.addLanguage(languages[i]);
		if(ontologies != null)
			for(int i = 0; i < ontologies.length; i++)
				this.addOntology(ontologies[i]);
	}

	//-------- accessor methods --------
	
	/**
	 *  Get the languages of this AgentDescription.
	 *  @return languages
	 */
	public String[] getLanguages()
	{
		return (String[])languages.toArray(new String[languages.size()]);
	}

	/**
	 *  Set the languages of this AgentDescription.
	 * @param languages the value to be set
	 */
	public void setLanguages(String[] languages)
	{
		this.languages.clear();
		for(int i = 0; i < languages.length; i++)
			this.languages.add(languages[i]);
	}

	/**
	 *  Get an languages of this AgentDescription.
	 *  @param idx The index.
	 *  @return languages
	 */
	public String getLanguage(int idx)
	{
		return (String)this.languages.get(idx);
	}

	/**
	 *  Set a language to this AgentDescription.
	 *  @param idx The index.
	 *  @param language a value to be added
	 */
	public void setLanguage(int idx, String language)
	{
		this.languages.set(idx, language);
	}

	/**
	 *  Add a language to this AgentDescription.
	 *  @param language a value to be removed
	 */
	public void addLanguage(String language)
	{
		this.languages.add(language);
	}

	/**
	 *  Remove a language from this AgentDescription.
	 *  @param language a value to be removed
	 *  @return  True when the languages have changed.
	 */
	public boolean removeLanguage(String language)
	{
		return this.languages.remove(language);
	}


	/**
	 *  Get the agentidentifier of this AgentDescription.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getName()
	{
		return this.name;
	}

	/**
	 *  Set the agentidentifier of this AgentDescription.
	 * @param name the value to be set
	 */
	public void setName(IComponentIdentifier name)
	{
		this.name = name;
	}

	/**
	 *  Get the ontologies of this AgentDescription.
	 * @return ontologies
	 */
	public String[] getOntologies()
	{
		return (String[])ontologies.toArray(new String[ontologies.size()]);
	}

	/**
	 *  Set the ontologies of this AgentDescription.
	 * @param ontologies the value to be set
	 */
	public void setOntologies(String[] ontologies)
	{
		this.ontologies.clear();
		for(int i = 0; i < ontologies.length; i++)
			this.ontologies.add(ontologies[i]);
	}

	/**
	 *  Get an ontologies of this AgentDescription.
	 *  @param idx The index.
	 *  @return ontologies
	 */
	public String getOntology(int idx)
	{
		return (String)this.ontologies.get(idx);
	}

	/**
	 *  Set a ontology to this AgentDescription.
	 *  @param idx The index.
	 *  @param ontology a value to be added
	 */
	public void setOntology(int idx, String ontology)
	{
		this.ontologies.set(idx, ontology);
	}

	/**
	 *  Add a ontology to this AgentDescription.
	 *  @param ontology a value to be removed
	 */
	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	/**
	 *  Remove a ontology from this AgentDescription.
	 *  @param ontology a value to be removed
	 *  @return  True when the ontologies have changed.
	 */
	public boolean removeOntology(String ontology)
	{
		return this.ontologies.remove(ontology);
	}

	/**
	 *  Get the services of this AgentDescription.
	 * @return services
	 */
	public IDFServiceDescription[] getServices()
	{
		return (IDFServiceDescription[])services.toArray(new IDFServiceDescription[services.size()]);
	}

	/**
	 *  Set the services of this AgentDescription.
	 * @param services the value to be set
	 */
	public void setServices(IDFServiceDescription[] services)
	{
		this.services.clear();
		for(int i = 0; i < services.length; i++)
			this.services.add(services[i]);
	}

	/**
	 *  Get an services of this AgentDescription.
	 *  @param idx The index.
	 *  @return services
	 */
	public IDFServiceDescription getService(int idx)
	{
		return (IDFServiceDescription)this.services.get(idx);
	}

	/**
	 *  Set a service to this AgentDescription.
	 *  @param idx The index.
	 *  @param service a value to be added
	 */
	public void setService(int idx, IDFServiceDescription service)
	{
		this.services.set(idx, service);
	}

	/**
	 *  Add a service to this AgentDescription.
	 *  @param service a value to be removed
	 */
	public void addService(IDFServiceDescription service)
	{
		this.services.add(service);
	}

	/**
	 *  Remove a service from this AgentDescription.
	 *  @param service a value to be removed
	 *  @return  True when the services have changed.
	 */
	public boolean removeService(IDFServiceDescription service)
	{
		return this.services.remove(service);
	}

	/**
	 *  Get the lease-time of this AgentDescription.
	 * @return lease-time
	 */
	public java.util.Date getLeaseTime()
	{
		return this.leasetime;
	}

	/**
	 *  Set the lease-time of this AgentDescription.
	 * @param leasetime the value to be set
	 */
	public void setLeaseTime(java.util.Date leasetime)
	{
		this.leasetime = leasetime;
	}

	/**
	 *  Get the protocols of this AgentDescription.
	 * @return protocols
	 */
	public String[] getProtocols()
	{
		return (String[])protocols.toArray(new String[protocols.size()]);
	}

	/**
	 *  Set the protocols of this AgentDescription.
	 * @param protocols the value to be set
	 */
	public void setProtocols(String[] protocols)
	{
		this.protocols.clear();
		for(int i = 0; i < protocols.length; i++)
			this.protocols.add(protocols[i]);
	}

	/**
	 *  Get an protocols of this AgentDescription.
	 *  @param idx The index.
	 *  @return protocols
	 */
	public String getProtocol(int idx)
	{
		return (String)this.protocols.get(idx);
	}

	/**
	 *  Set a protocol to this AgentDescription.
	 *  @param idx The index.
	 *  @param protocol a value to be added
	 */
	public void setProtocol(int idx, String protocol)
	{
		this.protocols.set(idx, protocol);
	}

	/**
	 *  Add a protocol to this AgentDescription.
	 *  @param protocol a value to be removed
	 */
	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	/**
	 *  Remove a protocol from this AgentDescription.
	 *  @param protocol a value to be removed
	 *  @return  True when the protocols have changed.
	 */
	public boolean removeProtocol(String protocol)
	{
		return this.protocols.remove(protocol);
	}

	/** 
	 * @param obj
	 * @return true if obj is an AgentDescription and both are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DFAgentDescription))
			return false;
		DFAgentDescription ad = (DFAgentDescription)obj;

		return eq(ad.name, name) && eq(ad.leasetime, leasetime) && eq(ad.languages, languages) && eq(ad.ontologies, ontologies) && eq(ad.protocols, protocols) && eq(ad.services, services);
	}

	/** 
	 * @param leasetime
	 * @param leasetime2
	 * @return true if both ar null or both are equal
	 */
	private static final boolean eq(Object a, Object b)
	{
		return (a == b) || (a != null && b != null && a.equals(b));
	}

	/**
	 *  Clone an agent description.
	 */
	public Object clone()
	{
		try
		{
			DFAgentDescription ret = (DFAgentDescription)super.clone();
			ret.services = new ArrayList();
			ret.protocols = (List)((ArrayList)protocols).clone();
			ret.languages = (List)((ArrayList)languages).clone();
			ret.ontologies = (List)((ArrayList)ontologies).clone();
			for(int i = 0; i < services.size(); i++)
			{
				ret.services.add(((DFServiceDescription)services.get(i)).clone());
			}
			return ret;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Cannot clone: " + this);
		}
	}


	/**
	 *  Get a string representation of this AgentDescription.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AgentDescription(" + getName() + (leasetime!=null ? ", "+leasetime.getTime()+")" : ")");
	}
}
