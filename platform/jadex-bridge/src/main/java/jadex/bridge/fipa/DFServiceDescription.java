package jadex.bridge.fipa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.bridge.service.types.df.IProperty;

/**
 *  The service description.
 */
public class DFServiceDescription implements IDFServiceDescription, Serializable, Cloneable
{
	//-------- attributes ----------

	/** Attribute for slot languages. */
	protected List languages;

	/** Attribute for slot type. */
	protected String type;

	/** Attribute for slot properties. */
	protected List properties;

	/** Attribute for slot name. */
	protected String name;

	/** Attribute for slot ontologies. */
	protected List ontologies;

	/** Attribute for slot ownership. */
	protected String ownership;

	/** Attribute for slot protocols. */
	protected List protocols;

	//-------- constructors --------

	/**
	 *  Create a new service description.
	 */
	public DFServiceDescription()
	{
		this(null, null, null);
	}

	/**
	 *  Create a new service description.
	 *  @param name The name.
	 *  @param type The type expression.
	 *  @param ownership The ownership.
	 */
	public DFServiceDescription(String name, String type, String ownership)
	{
		this.languages = new ArrayList();
		this.properties = new ArrayList();
		this.ontologies = new ArrayList();
		this.protocols = new ArrayList();

		this.setName(name);
		this.setType(type);
		this.setOwnership(ownership);
	}

	/** 
	 * @param obj
	 * @return true if obj is an ServiceDescription and both are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DFServiceDescription))
			return false;
		DFServiceDescription sd = (DFServiceDescription)obj;

		return eq(sd.name, name) && eq(sd.ownership, ownership) && eq(sd.type, type) && eq(sd.languages, languages) && eq(sd.ontologies, ontologies) && eq(sd.protocols, protocols)
			&& eq(sd.properties, properties);
	}
	
	/** 
	 * 
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((languages == null) ? 0 : languages.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ontologies == null) ? 0 : ontologies.hashCode());
		result = prime * result + ((ownership == null) ? 0 : ownership.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((protocols == null) ? 0 : protocols.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	//-------- accessor methods --------

	/**
	 *  Get the languages of this ServiceDescription.
	 * @return languages
	 */
	public String[] getLanguages()
	{
		return (String[])languages.toArray(new String[languages.size()]);
	}

	/**
	 *  Set the languages of this ServiceDescription.
	 * @param languages the value to be set
	 */
	public void setLanguages(String[] languages)
	{
		this.languages.clear();
		for(int i = 0; i < languages.length; i++)
			this.languages.add(languages[i]);
	}

	/**
	 *  Get an languages of this ServiceDescription.
	 *  @param idx The index.
	 *  @return languages
	 */
	public String getLanguage(int idx)
	{
		return (String)this.languages.get(idx);
	}

	/**
	 *  Set a language to this ServiceDescription.
	 *  @param idx The index.
	 *  @param language a value to be added
	 */
	public void setLanguage(int idx, String language)
	{
		this.languages.set(idx, language);
	}

	/**
	 *  Add a language to this ServiceDescription.
	 *  @param language a value to be removed
	 */
	public void addLanguage(String language)
	{
		this.languages.add(language);
	}

	/**
	 *  Remove a language from this ServiceDescription.
	 *  @param language a value to be removed
	 *  @return  True when the languages have changed.
	 */
	public boolean removeLanguage(String language)
	{
		return this.languages.remove(language);
	}


	/**
	 *  Get the type of this ServiceDescription.
	 * @return type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type of this ServiceDescription.
	 * @param type the value to be set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the properties of this ServiceDescription.
	 * @return properties
	 */
	public IProperty[] getProperties()
	{
		return (IProperty[])properties.toArray(new IProperty[properties.size()]);
	}

	/**
	 *  Set the properties of this ServiceDescription.
	 * @param properties the value to be set
	 */
	public void setProperties(IProperty[] properties)
	{
		this.properties.clear();
		for(int i = 0; i < properties.length; i++)
			this.properties.add(properties[i]);
	}

	/**
	 *  Get an properties of this ServiceDescription.
	 *  @param idx The index.
	 *  @return properties
	 */
	public IProperty getProperty(int idx)
	{
		return (IProperty)this.properties.get(idx);
	}

	/**
	 *  Set a property to this ServiceDescription.
	 *  @param idx The index.
	 *  @param property a value to be added
	 */
	public void setProperty(int idx, IProperty property)
	{
		this.properties.set(idx, property);
	}

	/**
	 *  Add a property to this ServiceDescription.
	 *  @param property a value to be removed
	 */
	public void addProperty(IProperty property)
	{
		this.properties.add(property);
	}

	/**
	 *  Remove a property from this ServiceDescription.
	 *  @param property a value to be removed
	 *  @return  True when the properties have changed.
	 */
	public boolean removeProperty(IProperty property)
	{
		return this.properties.remove(property);
	}

	/**
	 *  Get the name of this ServiceDescription.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this ServiceDescription.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the ontologies of this ServiceDescription.
	 * @return ontologies
	 */
	public String[] getOntologies()
	{
		return (String[])ontologies.toArray(new String[ontologies.size()]);
	}

	/**
	 *  Set the ontologies of this ServiceDescription.
	 * @param ontologies the value to be set
	 */
	public void setOntologies(String[] ontologies)
	{
		this.ontologies.clear();
		for(int i = 0; i < ontologies.length; i++)
			this.ontologies.add(ontologies[i]);
	}

	/**
	 *  Get an ontologies of this ServiceDescription.
	 *  @param idx The index.
	 *  @return ontologies
	 */
	public String getOntology(int idx)
	{
		return (String)this.ontologies.get(idx);
	}

	/**
	 *  Set a ontology to this ServiceDescription.
	 *  @param idx The index.
	 *  @param ontology a value to be added
	 */
	public void setOntology(int idx, String ontology)
	{
		this.ontologies.set(idx, ontology);
	}

	/**
	 *  Add a ontology to this ServiceDescription.
	 *  @param ontology a value to be removed
	 */
	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	/**
	 *  Remove a ontology from this ServiceDescription.
	 *  @param ontology a value to be removed
	 *  @return  True when the ontologies have changed.
	 */
	public boolean removeOntology(String ontology)
	{
		return this.ontologies.remove(ontology);
	}


	/**
	 *  Get the ownership of this ServiceDescription.
	 * @return ownership
	 */
	public String getOwnership()
	{
		return this.ownership;
	}

	/**
	 *  Set the ownership of this ServiceDescription.
	 * @param ownership the value to be set
	 */
	public void setOwnership(String ownership)
	{
		this.ownership = ownership;
	}

	/**
	 *  Get the protocols of this ServiceDescription.
	 * @return protocols
	 */
	public String[] getProtocols()
	{
		return (String[])protocols.toArray(new String[protocols.size()]);
	}

	/**
	 *  Set the protocols of this ServiceDescription.
	 * @param protocols the value to be set
	 */
	public void setProtocols(String[] protocols)
	{
		this.protocols.clear();
		for(int i = 0; i < protocols.length; i++)
			this.protocols.add(protocols[i]);
	}

	/**
	 *  Get an protocols of this ServiceDescription.
	 *  @param idx The index.
	 *  @return protocols
	 */
	public String getProtocol(int idx)
	{
		return (String)this.protocols.get(idx);
	}

	/**
	 *  Set a protocol to this ServiceDescription.
	 *  @param idx The index.
	 *  @param protocol a value to be added
	 */
	public void setProtocol(int idx, String protocol)
	{
		this.protocols.set(idx, protocol);
	}

	/**
	 *  Add a protocol to this ServiceDescription.
	 *  @param protocol a value to be removed
	 */
	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	/**
	 *  Remove a protocol from this ServiceDescription.
	 *  @param protocol a value to be removed
	 *  @return  True when the protocols have changed.
	 */
	public boolean removeProtocol(String protocol)
	{
		return this.protocols.remove(protocol);
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
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Cannot clone: " + this);
		}
	}
}
