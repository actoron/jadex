package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.writer.IObjectWriterHandler;
import jadex.commons.xml.writer.WriteObjectInfo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *  Java bean version for fetching write info for an object. 
 */
public class BeanObjectWriterHandler implements IObjectWriterHandler
{
	//-------- attributes --------
	
	/** The bean introspector. */
	protected IBeanIntrospector introspector = new BeanReflectionIntrospector();
	
	//-------- methods --------
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, Object context)
	{
		return object.getClass();
	}
	
	/**
	 *  Get write info for an object.
	 */
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, Object context)
	{
		// todo: handle proper attribute -> string conversion
		
		WriteObjectInfo wi = new WriteObjectInfo();
		HashSet doneprops = new HashSet();
		
		if(typeinfo!=null)
		{
			// Comment
			
			Object cominfo = typeinfo.getCommentInfo();
			if(cominfo!=null)
			{
				String propname = getPropertyName(cominfo);
				try
				{
					Method method = findGetMethod(object, propname, new String[]{"get", "is"});
					Object value = method.invoke(object, new Object[0]);
					if(value!=null)
					{
						wi.setComment(""+value);
						doneprops.add(propname);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Content
			
			Object cinfo = typeinfo.getContentInfo();
			if(cinfo!=null)
			{
				String propname = getPropertyName(cinfo);
				try
				{
					Method method = findGetMethod(object, propname, new String[]{"get", "is"});
					Object value = method.invoke(object, new Object[0]);
					if(value!=null)
					{
						wi.setContent(""+value);
						doneprops.add(propname);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Attributes
			
			Collection attrinfos = typeinfo.getAttributeInfos();
			if(attrinfos!=null)
			{
				for(Iterator it=attrinfos.iterator(); it.hasNext(); )
				{
					Object info = it.next();
					String propname = getPropertyName(info);
					try
					{
						Method method = findGetMethod(object, propname, new String[]{"get", "is"});
						
						Object value = method.invoke(object, new Object[0]);
						if(value!=null)
						{
							String xmlattrname = null;
							if(info instanceof AttributeInfo)
								xmlattrname = ((AttributeInfo)info).getXMLAttributeName();
							if(xmlattrname==null)
								xmlattrname = propname;
							wi.addAttribute(xmlattrname, ""+value);
							doneprops.add(propname);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			// Subobjects 
			
			Collection subobsinfos = typeinfo.getSubobjectInfos();
			if(subobsinfos!=null)
			{
				for(Iterator it=subobsinfos.iterator(); it.hasNext(); )
				{
					try
					{
						SubobjectInfo soinfo = (SubobjectInfo)it.next();
						String propname = (String)soinfo.getLinkInfo().getAttributeIdentifier();
						Method method = findGetMethod(object, propname, new String[]{"get"});
						
						Object value = method.invoke(object, new Object[0]);
						if(value!=null)
						{
							String xmlsoname = soinfo.getXMLTag()!=null? soinfo.getXMLTag(): propname;
							wi.addSubobject(xmlsoname, value);
							doneprops.add(propname);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
			
		Map props = introspector.getBeanProperties(object.getClass());
		if(props!=null)
		{
			for(Iterator it=props.keySet().iterator(); it.hasNext(); )
			{
				String propname = (String)it.next();
				
				if(!doneprops.contains(propname))
				{
					BeanProperty bp = (BeanProperty)props.get(propname);
					try
					{
						Object value = bp.getGetter().invoke(object, new Object[0]);
		
						if(value!=null)
						{
							if(BasicTypeConverter.isBuiltInType(bp.getType()))
							{
								wi.addAttribute(propname, ""+value);
							}
							else
							{
								wi.addSubobject(propname, value);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return wi;
	}
	
	/**
	 *  Get the property name.
	 *  @param info The info.
	 *  @return The property name.
	 */
	protected String getPropertyName(Object info)
	{
		String ret;
		if(info instanceof AttributeInfo)
		{
			ret = (String)((AttributeInfo)info).getAttributeIdentifier();
		}
		else if(info instanceof String)
		{
			ret = (String)info;
		}
		else
		{
			throw new RuntimeException("Unknown info type: "+info);
		}
		return ret;
	}
	
	/**
	 *  Find a get method with some prefix.
	 *  @param object The object.
	 *  @param name The name.
	 *  @param prefixes The prefixes to test.
	 */
	protected Method findGetMethod(Object object, String name, String[] prefixes)
	{
		Method method = null;
		for(int i=0; i<prefixes.length && method==null; i++)
		{
			String methodname = prefixes[i]+name.substring(0, 1).toUpperCase()+name.substring(1);
			try
			{
				method = object.getClass().getMethod(methodname, new Class[0]);
			}
			catch(Exception e)
			{
				// nop
			}
		}
		if(method==null)
			throw new RuntimeException("No getter found for: "+name);
		
		return method;
	}
}

