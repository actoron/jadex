package jadex.bridge.service;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;

/**
 *  Info struct for service publishing details.
 */
public class PublishInfo
{
	public static final String WP_URL = "wp_url";
	public static final String WP_APPNAME = "wp_appname";
	public static final String WP_TARGET = "wp_target";
	public static final String WP_USER = "wp_user";
	public static final String WP_PASS = "wp_pass";

	//-------- attributes --------
	
	/** The publish id. */
	protected String pid;
	
	/** The publish type. */
	protected String publishtype;
	
	/** The publish scope. */
	protected ServiceScope publishscope = ServiceScope.PLATFORM;
	
	/** Flag for allowing publishing to multiple locations. */
	protected boolean multi = false;
	
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
		this(pid, publishtype, ServiceScope.PLATFORM, false, mapping, (UnparsedExpression[])null);
	}
		
	/**
	 *  Create a new publish info.
	 *  @param pid The publish id, e.g. url.
	 *  @param publishtype The publish type.
	 */
	public PublishInfo(String pid, String publishtype, ServiceScope publishscope, boolean multi,
		Class<?> mapping, UnparsedExpression[] properties)
	{
		this.pid = pid;
		this.publishtype = publishtype;
		this.publishscope = publishscope;
		this.multi = multi;
		this.mapping = mapping==null? null: new ClassInfo(mapping);
		if(properties!=null)
		{
			this.properties = SUtil.arrayToList(properties);
		}
	}
	
	/**
	 *  Create a new publish info.
	 *  Convenience constructor that creates unparsed expressions from a string array containing consecutive name/value pairs.
	 *  @param pid The publish id, e.g. url.
	 *  @param publishtype The publish type.
	 */
	public PublishInfo(String pid, String publishtype, ServiceScope publishscope, boolean multi,
		Class<?> mapping, Object[] props)
	{
		this.pid = pid;
		this.publishtype = publishtype;
		this.publishscope = publishscope;
		this.multi = multi;
		this.mapping = mapping==null? null: new ClassInfo(mapping);
		this.properties = new ArrayList<UnparsedExpression>();
		if(props!=null)
		{
			for(int i=0; i<props.length; i+=2)
			{
				properties.add(new UnparsedExpression((String)props[i],
					props[i+1] instanceof String ? "\""+props[i+1]+"\"" : ""+props[i+1]));
			}
		}
	}
	
	/**
	 *  Create a new publish info.
	 */
	public PublishInfo(PublishInfo info)
	{
		this.pid = info.getPublishId();
		this.publishtype = info.getPublishType();
		this.publishscope = info.getPublishScope();
		this.multi = info.multi;
		this.mapping = info.getMapping();
		if(info.getProperties()!=null)
		{
			this.properties = new ArrayList<UnparsedExpression>(info.getProperties());
		}
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
	 *  Gets the publish scope.
	 *  @return The publish scope.
	 */
	public ServiceScope getPublishScope()
	{
		return publishscope;
	}

	/**
	 *  Set the publish scope.
	 *  @param publishscope The publish scope.
	 */
	public void setPublishScope(ServiceScope publishscope)
	{
		this.publishscope = publishscope;
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
	 *  Sets if the publishing should be done on multiple publishing services.
	 *  
	 *  @param multi Set true, if multi-publish.
	 */
	public void setMulti(boolean multi)
	{
		this.multi = multi;
	}
	
	/**
	 *  Gets if the publishing should be done on multiple publishing services.
	 *  
	 *  @return True, if multi-publish.
	 */
	public boolean isMulti()
	{
		return multi;
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

	/**
	 *  Get the string representation.
	 */
	@Override
	public String toString()
	{
		return "PublishInfo [pid=" + pid + ", mapping=" + mapping + "]";
	}
}
