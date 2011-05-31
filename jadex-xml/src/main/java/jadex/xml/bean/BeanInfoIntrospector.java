package jadex.xml.bean;


import jadex.commons.collection.LRU;
import jadex.xml.SXML;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.annotation.XMLExclude;
import jadex.xml.annotation.XMLInclude;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  Introspector for Java beans. It uses the property inspector
 *  to build up a map with property infos (name, read/write method, etc.) 
 */
public class BeanInfoIntrospector implements IBeanIntrospector
{
	//-------- attributes --------
	
	/** The cache for saving time for multiple lookups. */
	protected LRU beaninfos;
	
	//-------- constructors --------
	
	/**
	 * Create a new introspector.
	 */
	public BeanInfoIntrospector()
	{
		this(200);
	}
	
	/**
	 * Create a new introspector.
	 */
	public BeanInfoIntrospector(int lrusize)
	{
		this.beaninfos = new LRU(lrusize);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the bean properties for a specific clazz.
	 */
	public Map getBeanProperties(Class clazz, boolean includefields)
	{
		Map ret = (Map)beaninfos.get(clazz);
		
		if(ret==null)
		{
			try
			{
				BeanInfo bi = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] pds = bi.getPropertyDescriptors();
				ret = new HashMap();
	            for(int k=0; k<pds.length; k++) 
	            {
					PropertyDescriptor pd = pds[k];
					Method setter = pd.getWriteMethod();
					Method getter = pd.getReadMethod();
					if(setter != null && getter != null) 
					{
						XMLExclude exset = setter.getAnnotation(XMLExclude.class);
						XMLExclude exget = getter.getAnnotation(XMLExclude.class);
						if(exset==null && exget==null)
						{
							Class[] setter_param_type = setter.getParameterTypes();
							if (setter_param_type.length==1) 
							{
								ret.put(pd.getName(), new BeanProperty(pd.getName(), pd.getPropertyType(), getter, setter, setter_param_type[0]));
							}
						}
					}
				}
				
	            // Get all public fields.
				if(includefields)
				{
					Field[] fields = clazz.getFields();
					for(int i = 0; i < fields.length; i++)
					{
						String property_java_name = fields[i].getName();
						XMLExclude ex = fields[i].getAnnotation(XMLExclude.class);
						if(!ret.containsKey(property_java_name) && ex==null)
						{
							ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
						}
					}
				}
				else
				{
					Field[] fields = clazz.getFields();
					for(int i = 0; i < fields.length; i++)
					{
						String property_java_name = fields[i].getName();
						XMLInclude in = fields[i].getAnnotation(XMLInclude.class);
						if(!ret.containsKey(property_java_name) && in!=null)
						{
							ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
						}
					}
				}
	            
	            // Get final values (val$xyz fields) for anonymous classes.
	            if(clazz.isAnonymousClass())
	            {
		            Field[] fields = clazz.getDeclaredFields();
		            for(int i=0; i<fields.length; i++)
		            {
		            	String property_java_name = fields[i].getName();
		            	if(property_java_name.startsWith("val$"))
		            	{
		            		property_java_name	= property_java_name.substring(4);
			            	if(!ret.containsKey(property_java_name))
			            	{
			            		ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
			            	}
		            	}
		            	
		            	// Add XML class name property if field present (hack!!!)
		            	else if(SXML.XML_CLASSNAME.equals(property_java_name))
		            	{
		            		ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
		            	}
		            }
		            
		            // Add value of xml class name annotation. (hack!!! shouldn't be property)
		            if(!ret.containsKey(SXML.XML_CLASSNAME))
		            {
		            	XMLClassname xmlc = SXML.getXMLClassnameAnnotation(clazz);
		            	
		            	if(xmlc!=null)
		            		ret.put(SXML.XML_CLASSNAME, xmlc);
		            }
	            }
	            
	            beaninfos.put(clazz, ret);
			}
			catch(Exception e)
			{	
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
}