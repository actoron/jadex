package jadex.bridge.fipa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;


/**
 *  An component description.
 */
public class DFComponentDescription implements IDFComponentDescription, Serializable, Cloneable
{
	//-------- attributes ----------

	/** Attribute for slot languages. */
	protected List languages;

	/** Attribute for slot componentidentifier. */
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
	 *  Create a new component description.
	 */
	public DFComponentDescription()
	{
		this(null);
	}

	/**
	 *  Create a new component description.
	 *  @param name The name.
	 */
	public DFComponentDescription(IComponentIdentifier name)
	{
		this(name, null, null, null, null, null);
	}

	/**
	 *  Create a new component description.
	 *  @param name The name.
	 *  @param services The services.
	 *  @param protocols The protocols.
	 *  @param ontologies The ontologies.
	 *  @param languages The languages.
	 */
	public DFComponentDescription(IComponentIdentifier name, IDFServiceDescription[] services, 
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

	/**
	 *  Convenience constructor for searching/registering.
	 */
	public DFComponentDescription(IComponentIdentifier name, IDFServiceDescription service)
	{
		this(name, new IDFServiceDescription[]{service}, null, null, null, null);
	}

	/**
	 *  Convenience constructor for searching/registering.
	 */
	public DFComponentDescription(IComponentIdentifier name, IDFServiceDescription service, Date leasetime)
	{
		this(name, new IDFServiceDescription[]{service}, null, null, null, leasetime);
	}

	//-------- accessor methods --------
	
	/**
	 *  Get the languages of this ComponentDescription.
	 *  @return languages
	 */
	public String[] getLanguages()
	{
		return (String[])languages.toArray(new String[languages.size()]);
	}

	/**
	 *  Set the languages of this ComponentDescription.
	 * @param languages the value to be set
	 */
	public void setLanguages(String[] languages)
	{
		this.languages.clear();
		for(int i = 0; i < languages.length; i++)
			this.languages.add(languages[i]);
	}

	/**
	 *  Get an languages of this ComponentDescription.
	 *  @param idx The index.
	 *  @return languages
	 */
	public String getLanguage(int idx)
	{
		return (String)this.languages.get(idx);
	}

	/**
	 *  Set a language to this ComponentDescription.
	 *  @param idx The index.
	 *  @param language a value to be added
	 */
	public void setLanguage(int idx, String language)
	{
		this.languages.set(idx, language);
	}

	/**
	 *  Add a language to this ComponentDescription.
	 *  @param language a value to be removed
	 */
	public void addLanguage(String language)
	{
		this.languages.add(language);
	}

	/**
	 *  Remove a language from this ComponentDescription.
	 *  @param language a value to be removed
	 *  @return  True when the languages have changed.
	 */
	public boolean removeLanguage(String language)
	{
		return this.languages.remove(language);
	}


	/**
	 *  Get the componentidentifier of this ComponentDescription.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getName()
	{
		return this.name;
	}

	/**
	 *  Set the componentidentifier of this ComponentDescription.
	 * @param name the value to be set
	 */
	public void setName(IComponentIdentifier name)
	{
		this.name = name;
	}

	/**
	 *  Get the ontologies of this ComponentDescription.
	 * @return ontologies
	 */
	public String[] getOntologies()
	{
		return (String[])ontologies.toArray(new String[ontologies.size()]);
	}

	/**
	 *  Set the ontologies of this ComponentDescription.
	 * @param ontologies the value to be set
	 */
	public void setOntologies(String[] ontologies)
	{
		this.ontologies.clear();
		for(int i = 0; i < ontologies.length; i++)
			this.ontologies.add(ontologies[i]);
	}

	/**
	 *  Get an ontologies of this ComponentDescription.
	 *  @param idx The index.
	 *  @return ontologies
	 */
	public String getOntology(int idx)
	{
		return (String)this.ontologies.get(idx);
	}

	/**
	 *  Set a ontology to this ComponentDescription.
	 *  @param idx The index.
	 *  @param ontology a value to be added
	 */
	public void setOntology(int idx, String ontology)
	{
		this.ontologies.set(idx, ontology);
	}

	/**
	 *  Add a ontology to this ComponentDescription.
	 *  @param ontology a value to be removed
	 */
	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	/**
	 *  Remove a ontology from this ComponentDescription.
	 *  @param ontology a value to be removed
	 *  @return  True when the ontologies have changed.
	 */
	public boolean removeOntology(String ontology)
	{
		return this.ontologies.remove(ontology);
	}

	/**
	 *  Get the services of this ComponentDescription.
	 * @return services
	 */
	public IDFServiceDescription[] getServices()
	{
		return (IDFServiceDescription[])services.toArray(new IDFServiceDescription[services.size()]);
	}

	/**
	 *  Set the services of this ComponentDescription.
	 * @param services the value to be set
	 */
	public void setServices(IDFServiceDescription[] services)
	{
		this.services.clear();
		for(int i = 0; i < services.length; i++)
			this.services.add(services[i]);
	}

	/**
	 *  Get an services of this ComponentDescription.
	 *  @param idx The index.
	 *  @return services
	 */
	public IDFServiceDescription getService(int idx)
	{
		return (IDFServiceDescription)this.services.get(idx);
	}

	/**
	 *  Set a service to this ComponentDescription.
	 *  @param idx The index.
	 *  @param service a value to be added
	 */
	public void setService(int idx, IDFServiceDescription service)
	{
		this.services.set(idx, service);
	}

	/**
	 *  Add a service to this ComponentDescription.
	 *  @param service a value to be removed
	 */
	public void addService(IDFServiceDescription service)
	{
		this.services.add(service);
	}

	/**
	 *  Remove a service from this ComponentDescription.
	 *  @param service a value to be removed
	 *  @return  True when the services have changed.
	 */
	public boolean removeService(IDFServiceDescription service)
	{
		return this.services.remove(service);
	}

	/**
	 *  Get the lease-time of this ComponentDescription.
	 * @return lease-time
	 */
	public java.util.Date getLeaseTime()
	{
		return this.leasetime;
	}

	/**
	 *  Set the lease-time of this ComponentDescription.
	 * @param leasetime the value to be set
	 */
	public void setLeaseTime(java.util.Date leasetime)
	{
		this.leasetime = leasetime;
	}

	/**
	 *  Get the protocols of this ComponentDescription.
	 * @return protocols
	 */
	public String[] getProtocols()
	{
		return (String[])protocols.toArray(new String[protocols.size()]);
	}

	/**
	 *  Set the protocols of this ComponentDescription.
	 * @param protocols the value to be set
	 */
	public void setProtocols(String[] protocols)
	{
		this.protocols.clear();
		for(int i = 0; i < protocols.length; i++)
			this.protocols.add(protocols[i]);
	}

	/**
	 *  Get an protocols of this ComponentDescription.
	 *  @param idx The index.
	 *  @return protocols
	 */
	public String getProtocol(int idx)
	{
		return (String)this.protocols.get(idx);
	}

	/**
	 *  Set a protocol to this ComponentDescription.
	 *  @param idx The index.
	 *  @param protocol a value to be added
	 */
	public void setProtocol(int idx, String protocol)
	{
		this.protocols.set(idx, protocol);
	}

	/**
	 *  Add a protocol to this ComponentDescription.
	 *  @param protocol a value to be removed
	 */
	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	/**
	 *  Remove a protocol from this ComponentDescription.
	 *  @param protocol a value to be removed
	 *  @return  True when the protocols have changed.
	 */
	public boolean removeProtocol(String protocol)
	{
		return this.protocols.remove(protocol);
	}

	/** 
	 * @param obj
	 * @return true if obj is an ComponentDescription and both are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DFComponentDescription))
			return false;
		DFComponentDescription ad = (DFComponentDescription)obj;

		return eq(ad.name, name) && eq(ad.leasetime, leasetime) && eq(ad.languages, languages) && eq(ad.ontologies, ontologies) && eq(ad.protocols, protocols) && eq(ad.services, services);
	}
	
	/** 
	 * 
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((languages == null) ? 0 : languages.hashCode());
		result = prime * result + ((leasetime == null) ? 0 : leasetime.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ontologies == null) ? 0 : ontologies.hashCode());
		result = prime * result + ((protocols == null) ? 0 : protocols.hashCode());
		result = prime * result + ((services == null) ? 0 : services.hashCode());
		return result;
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
	 *  Clone a component description.
	 */
	public Object clone()
	{
		try
		{
			DFComponentDescription ret = (DFComponentDescription)super.clone();
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
	 *  Get a string representation of this ComponentDescription.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ComponentDescription(" + getName() + (leasetime!=null ? ", "+leasetime.getTime()+")" : ")");
	}
}
