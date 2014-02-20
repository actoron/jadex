package jadex.bridge.service;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  Info struct for service publishing details.
 */
public class PublishInfo
{
	public static final String WP_URL = "wp_url";
	public static final String WP_APPNAME = "wp_appname";
	public static final String WP_TARGET = "wp_target";

	//-------- attributes --------
	
	/** The publish id. */
	protected String pid;
	
	/** The publish type. */
	protected String publishtype;
	
	/** The mapping information (e.g. annotated interface). */
	protected ClassInfo mapping;
	
	/** The mapping properties. */
	protected List<UnparsedExpression> properties;

	//-------- constructors --------

	/**
	 *  Create a new publish info.
	 */
	public PublishInfo()
	{
	}

	/**
	 *  Create a new publish info.
	 *  @param pid The publish id, e.g. url.
	 *  @param publishtype The publish type.
	 */
	public PublishInfo(String pid, String publishtype, Class<?> mapping)
	{
		this(pid, publishtype, mapping, null);
	}
		
	/**
	 *  Create a new publish info.
	 *  @param pid The publish id, e.g. url.
	 *  @param publishtype The publish type.
	 */
	public PublishInfo(String pid, String publishtype, 
		Class<?> mapping, UnparsedExpression[] properties)
	{
		this.pid = pid;
		this.publishtype = publishtype;
		this.mapping = mapping==null? null: new ClassInfo(mapping);
		this.properties = SUtil.arrayToList(properties);
	}
	
	//-------- methods --------

	/**
	 *  Get the publishid.
	 *  @return the publishid.
	 */
	public String getPublishId()
	{
		return pid;
	}

	/**
	 *  Set the publishid.
	 *  @param publishid The publishid to set.
	 */
	public void setPublishId(String pid)
	{
		this.pid = pid;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getPublishType()
	{
		return publishtype;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setPublishType(String type)
	{
		this.publishtype = type;
	}

	/**
	 *  Get the mapping information (e.g. annotated interface). 
	 *  @return The mapping.
	 */
	public ClassInfo getMapping()
	{
		return mapping;
	}

	/**
	 *  Set the mapping information (e.g. annotated interface). 
	 *  @param mapping The mapping to set.
	 */
	public void setMapping(ClassInfo mapping)
	{
		this.mapping = mapping;
	}

	/**
	 *  Get the properties.
	 *  @return the properties.
	 */
	public List<UnparsedExpression> getProperties()
	{
		return properties;
	}

	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setProperties(List<UnparsedExpression> properties)
	{
		this.properties = properties;
	}
	
	/**
	 *  Add a property.
	 *  @param property The property to add.
	 */
	public void addProperty(UnparsedExpression property)
	{
		if(properties==null)
			this.properties = new ArrayList<UnparsedExpression>();
		properties.add(property);
	}
	
	/**
	 *  Add a property.
	 *  @param property The property to add.
	 */
	public void addProperty(String name, String val)
	{
		if(properties==null)
			this.properties = new ArrayList<UnparsedExpression>();
		properties.add(new UnparsedExpression(name, val));
	}
}
