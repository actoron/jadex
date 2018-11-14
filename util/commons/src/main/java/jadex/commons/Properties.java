package jadex.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
//	protected Map subproperties;
	
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
//		this.subproperties = new HashMap();
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
	 *  Get all properties.
	 */
	public Property[]	getProperties()
	{
		return (Property[])properties.toArray(new Property[properties.size()]);
	}
	
	/**
	 *  Set the properties.
	 *  @param properties The properties.
	 */
	public void setProperties(Property[] properties)
	{
		this.properties = new ArrayList(Arrays.asList(properties));
	}
	
	/**
	 *  Get all subproperties. 
	 */
	public Properties[] getSubproperties()
	{
		return (Properties[])subproperties.toArray(new Properties[subproperties.size()]);
	}
	
	/**
	 *  Set the subproperties.
	 *  @param subproperties The subproperties to set.
	 */
	public void setSubproperties(Properties[] subproperties)
	{
		this.subproperties = new ArrayList(Arrays.asList(subproperties));
	}
	
	/**
	 *  Get a properties by type.
	 *  @param type The type name. 
	 */
	public Property getProperty(String type)
	{
		Property[] props = getProperties(type);
		if(props.length>1)
			throw new RuntimeException("More than one property of type: "+type+" "+SUtil.arrayToString(props));
		return props.length==1? props[0]: null;
		
//		Property ret = null;
//		for(int i=0; ret==null && i<properties.size(); i++)
//		{
//			Property prop = (Property)properties.get(i);
//			if(type.equals(prop.getType()))
//				ret = prop;
//		}
//		return ret;
	}
	
	/**
	 *  Get the latest property by type.
	 *  @param type The type name. 
	 */
	public Property getLatestProperty(String type)
	{
		Property[] props = getProperties(type);
//		System.out.println("here: "+type+" "+SUtil.arrayToString(props));
		return props.length>0? props[props.length-1]: null;
	}
	
	/**
	 *  Get properties by type.
	 *  @param type The type name. 
	 */
	public Property[]	getProperties(String type)
	{
		List ret = new ArrayList();
		int idx = type.indexOf(".");
		if(idx!=-1)
		{
			String first = type.substring(0, idx);
			String last = type.substring(idx+1);
			
			Properties[] subprops = getSubproperties(first);
			for(int i=0; i<subprops.length; i++)
			{
				Property[] ps = subprops[i].getProperties(last);
				for(int j=0; j<ps.length; j++)
					ret.add(ps[j]);
			}
		}
		else
		{
			for(int i=0; i<properties.size(); i++)
			{
				Property prop = (Property)properties.get(i);
				if(type.equals(prop.getType()))
					ret.add(prop);
			}
		}
		return (Property[])ret.toArray(new Property[ret.size()]);
	}

	/**
	 *  Get a properties by type.
	 *  @param type The type name. 
	 */
	public Properties getSubproperty(String type)
	{
		Properties[] props = getSubproperties(type);
		if(props.length>1)
			throw new RuntimeException("More than one property of type: "+type+" "+SUtil.arrayToString(props));
		return props.length==1? props[0]: null;
		
//		Properties ret = null;
//		for(Iterator it=subproperties.values().iterator(); it.hasNext();)
//		{
//			Properties props = (Properties)it.next();
//			if(SUtil.equals(type, props.getType()))
//				ret = props;
//		}
//		return ret;
	}
	
	/**
	 *  Get subproperties by type. 
	 *  @param type The type.
	 */
	public Properties[] getSubproperties(String type)
	{
		List ret = new ArrayList();
		int idx = type.indexOf(".");
		if(idx!=-1)
		{
			String first = type.substring(0, idx);
			String last = type.substring(idx+1);
			
			Properties[] subprops = getSubproperties(first);
			for(int i=0; i<subprops.length; i++)
			{
				Properties[] ps = subprops[i].getSubproperties(last);
				for(int j=0; j<ps.length; j++)
					ret.add(ps[j]);
			}
		}
		else
		{
			for(int i=0; i<subproperties.size(); i++)
			{
				Properties props = (Properties)subproperties.get(i);
				if(type.equals(props.getType()))
					ret.add(props);
			}
		}
		return (Properties[])ret.toArray(new Properties[ret.size()]);
	}
	
	//-------- manipulation methods --------
	
	/**
	 *  Add subproperties to this properties.
	 */
	public void	addSubproperties(Properties props)
	{
//		if(subproperties.containsKey(props.getType()))
//			throw new RuntimeException("Subproperties already contained: "+props);
		
//		subproperties.put(props.getType(), props);
		subproperties.add(props);
	}
	
	/**
	 *  Add a subproperties to a properties.
	 */
	public void	addSubproperties(String type, Properties subproperties)
	{
		if(subproperties.getType()!=null && !subproperties.getType().equals(type))
			throw new IllegalArgumentException("Incompatible types: "+subproperties.getType()+", "+type);
		
		subproperties.setType(type);
		addSubproperties(subproperties);
	}
	
	/**
	 *  Remove all subproperties of a given type.
	 */
	public void	removeSubproperties(String type)
	{
		for(Iterator it=subproperties.iterator(); it.hasNext(); )
		{
			Properties	sub	= (Properties)it.next();
			if(type.equals(sub.getType()))
				it.remove();
		}
	}
	
	/**
	 *  Add a property to this properties.
	 */
	public void	addProperty(Property prop)
	{
//		if(prop.getType().indexOf('.')!=-1)
//			System.out.println("No '.' allowed in property type: "+prop.getType());
//		System.out.println("adding: "+prop);
		properties.add(prop);
	}
	
	//-------- convenience methods --------
	
	/**
	 *  Get a boolean property.
	 *  @param type The type.
	 *  @return Returns false if the property is not set.
	 */
	public boolean	getBooleanProperty(String type)
	{
		Property	prop	= getLatestProperty(type);
		//return prop!=null && Boolean.parseBoolean(prop.getValue());
		return prop!=null && Boolean.valueOf(prop.getValue()).booleanValue();
	}
	
	/**
	 *  Get a long property.
	 *  @param type The type.
	 *  @return Returns the parsed long value, 0 if not set.
	 */
	public long getLongProperty(String type)
	{
		Property	prop	= getLatestProperty(type);
		return prop==null? 0: Long.parseLong(prop.getValue());
	}
	
	/**
	 *  Get an int  property.
	 *  @param type The type.
	 *  @return Returns the parsed int value, 0 if not set.
	 */
	public int getIntProperty(String type)
	{
		Property	prop	= getLatestProperty(type);
		return prop==null? 0: Integer.parseInt(prop.getValue());
	}
	
	/**
	 *  Get a double  property.
	 *  @param type The type.
	 *  @return Returns the parsed double value, 0 if not set.
	 */
	public double getDoubleProperty(String type)
	{
		Property	prop	= getLatestProperty(type);
		return prop==null? 0: Double.parseDouble(prop.getValue());
	}

	/**
	 *  Get a string  property.
	 *  @param type The type.
	 *  @return Returns the string value or null if not set.
	 */
	public String getStringProperty(String type)
	{
		Property	prop	= getLatestProperty(type);
		return prop==null? null: prop.getValue();
	}
	
	/** 
	 *  Add the complete content of another properties. 
	 */
	public void addProperties(Properties toadd)
	{
		Property[] subprops = toadd.getProperties();
		for(int j=0; j<subprops.length; j++)
		{
			addProperty(subprops[j]);
		}
		
		Properties[] subpropis = toadd.getSubproperties();
		for(int i=0; i<subpropis.length; i++)
		{
//			Properties tmp = getSubproperty(subpropis[i].getType());
//			if(tmp!=null)
//			{
//				tmp.addProperties(subpropis[i]);
//			}
//			else
			{
				addSubproperties(subpropis[i]);
			}
		}
	}
	
	//-------- static helpers --------
	
	/**
	 *  Get a boolean property.
	 *  @param type The type.
	 *  @return Returns false if the property is not set.
	 */
	public static boolean getBooleanProperty(Properties[] props, String type)
	{
		Property prop = getLatestProperty(props, type);
		return prop!=null && Boolean.valueOf(prop.getValue()).booleanValue();
	}
	
	/**
	 *  Get a long property.
	 *  @param type The type.
	 *  @return Returns the parsed long value, 0 if not set.
	 */
	public static long getLongProperty(Properties[] props, String type)
	{
		Property prop = getLatestProperty(props, type);
		return prop==null? 0: Long.parseLong(prop.getValue());
	}
	
	/**
	 *  Get an int  property.
	 *  @param type The type.
	 *  @return Returns the parsed int value, 0 if not set.
	 */
	public static int getIntProperty(Properties[] props, String type)
	{
		Property prop = getLatestProperty(props, type);
		return prop==null? 0: Integer.parseInt(prop.getValue());
	}
	
	/**
	 *  Get a string  property.
	 *  @param type The type.
	 *  @return Returns the string value or null if not set.
	 */
	public static String getStringProperty(Properties[] props, String type)
	{
		Property prop = getLatestProperty(props, type);
		return prop==null? null: prop.getValue();
	}
	
	/**
	 *  Get the latest property by type.
	 *  @param type The type name. 
	 */
	public static Property getLatestProperty(Properties[] props, String type)
	{
		Property ret = null;
		for(int i=props.length-1; i>-1 && ret==null; i--)
		{
			Property[] tmp = props[i].getProperties(type);
			if(tmp.length>0)
				ret = tmp[tmp.length-1];
		}
		return ret;
	}
	
	/**
	 *  Get subproperties by type. 
	 *  @param type The type.
	 */
	public static Properties[] getSubproperties(Properties[] props, String type)
	{
		List ret = new ArrayList();
		for(int i=0; i<props.length; i++)
		{
			Properties[] tmp = props[i].getSubproperties(type);
			for(int j=0; j<tmp.length; j++)
				ret.add(tmp[j]);
		}
		return (Properties[])ret.toArray(new Properties[ret.size()]);
	}
	
	/**
	 *  Get properties by type. 
	 *  @param type The type.
	 */
	public static Property[] getProperties(Properties[] props, String type)
	{
		List ret = new ArrayList();
		for(int i=0; i<props.length; i++)
		{
			Property[] tmp = props[i].getProperties(type);
			for(int j=0; j<tmp.length; j++)
				ret.add(tmp[j]);
		}
		return (Property[])ret.toArray(new Property[ret.size()]);
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
