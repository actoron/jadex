package jadex.bridge;

import java.util.ArrayList;
import java.util.List;

/**
 *  The configuration properties.
 */
public class Properties
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The property type (defines the kind of property). */
	protected String type;
	
	/** The id. */
	protected String id;
	
	/** The direct properties. */
	protected List properties;
	
	/** The subproperties. */
	protected List subproperties;
	
	/** todo: the property refs. */
//	protected List propertyrefs;
	
	//-------- constructors --------
	
	/**
	 *  Create a new properties.
	 */
	public Properties()
	{
		this(null, null, null);
	}

	/**
	 *  Create a new properties.
	 */
	public Properties(String name, String type, String id)
	{
		this.name	= name;
		this.type	= type;
		this.id = id;
		this.properties = new ArrayList();
		this.subproperties = new ArrayList();
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Set the name of the properties.
	 */
	public void setName(String name)
	{
		this.name	= name;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type of the properties.
	 */
	public void setType(String type)
	{
		this.type	= type;
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 *  Set the id.
	 */
	public void setId(String id)
	{
		this.id	= id;
	}
	
	/**
	 *  Get a properties by type.
	 *  @param type The type name. 
	 */
	public Property getProperty(String type)
	{
		Property ret = null;
		for(int i=0; ret==null && i<properties.size(); i++)
		{
			Property prop = (Property)properties.get(i);
			if(type.equals(prop.getType()))
				ret = prop;
		}
		return ret;
	}
	
	/**
	 *  Get all properties.
	 */
	public Property[]	getProperties()
	{
		return (Property[])properties.toArray(new Property[properties.size()]);
	}
	
	/**
	 *  Get properties by type.
	 *  @param type The type name. 
	 */
	public Property[]	getProperties(String type)
	{
		List ret = new ArrayList();
		for(int i=0; i<properties.size(); i++)
		{
			Property prop = (Property)properties.get(i);
			if(type.equals(prop.getType()))
				ret.add(prop);
		}
		return (Property[])ret.toArray(new Property[ret.size()]);
	}
	
	/**
	 *  Get a properties by type.
	 *  @param type The type name. 
	 */
	public Properties getSubproperty(String type)
	{
		Properties ret = null;
		for(int i=0; ret==null && i<subproperties.size(); i++)
		{
			Properties props = (Properties)subproperties.get(i);
			if(type.equals(props.getType()))
				ret = props;
		}
		return ret;
	}
	
	/**
	 *  Get all subproperties. 
	 */
	public Properties[] getSubproperties()
	{
		return (Properties[])subproperties.toArray(new Properties[subproperties.size()]);
	}
	
	/**
	 *  Get subproperties by type. 
	 *  @param type The type.
	 */
	public Properties[] getSubproperties(String type)
	{
		List ret = new ArrayList();
		for(int i=0; i<subproperties.size(); i++)
		{
			Properties props = (Properties)subproperties.get(i);
			if(type.equals(props.getType()))
				ret.add(props);
		}
		return (Properties[])ret.toArray(new Properties[ret.size()]);
	}
	
	//-------- manipulation methods --------
	
	/**
	 *  Add subproperties to this properties.
	 */
	public void	addSubproperties(Properties props)
	{
		subproperties.add(props);
	}
	
	/**
	 *  Add a property to this properties.
	 */
	public void	addProperty(Property prop)
	{
		properties.add(prop);
	}
	
	//-------- convenience methods --------
	
	/**
	 *  Get a boolean property.
	 *  @param type The type.
	 *  @returns Returns false if the property is not set.
	 */
	public boolean	getBooleanProperty(String type)
	{
		Property	prop	= getProperty(type);
		//return prop!=null && Boolean.parseBoolean(prop.getValue());
		return prop!=null && Boolean.valueOf(prop.getValue()).booleanValue();
	}
	
	/**
	 *  Get a long property.
	 *  @param type The type.
	 *  @returns Returns the parsed long value, 0 if not set.
	 */
	public long getLongProperty(String type)
	{
		Property	prop	= getProperty(type);
		return prop==null? 0: Long.parseLong(prop.getValue());
	}
	
	/**
	 *  Get an int  property.
	 *  @param type The type.
	 *  @returns Returns the parsed int value, 0 if not set.
	 */
	public int getIntProperty(String type)
	{
		Property	prop	= getProperty(type);
		return prop==null? 0: Integer.parseInt(prop.getValue());
	}
	
	/**
	 *  Get a string  property.
	 *  @param type The type.
	 *  @returns Returns the string value or null if not set.
	 */
	public String	getStringProperty(String type)
	{
		Property	prop	= getProperty(type);
		return prop==null? null: prop.getValue();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Properties( type="+type+", name="+name+" , id="+id+")";
	}
}
