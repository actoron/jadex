package jadex.commons.xml;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class BeanObjectWriterHandler implements IObjectWriterHandler
{
	//-------- attributes --------
	
	/** The bean introspector. */
	protected IBeanIntrospector introspector = new ReflectionIntrospector();
	
	//-------- methods --------
	
	/**
	 *  Get attributes of an object as name value pairs.
	 */
	public Object[] getAttributesContentAndSubobjects(Object object, TypeInfo typeinfo)
	{
		// todo: handle proper attribute -> string conversion
		
		Map attrs = new HashMap();
		String content = null;
		Map subobs = new HashMap();
		Set doneprops = new HashSet();
		
		if(typeinfo!=null)
		{
			// Content
			
			BeanAttributeInfo cinfo = (BeanAttributeInfo)typeinfo.getContentInfo();
			if(cinfo!=null)
			{
				try
				{
					String propname = cinfo.getAttributeName();
					Method method = findGetMethod(object, propname, new String[]{"get", "is"});
					Object value = method.invoke(object, new Object[0]);
					if(value!=null)
					{
						content = ""+value;
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
					try
					{
						BeanAttributeInfo attrinfo = (BeanAttributeInfo)it.next();
						String propname = attrinfo.getAttributeName();
						Method method = findGetMethod(object, propname, new String[]{"get", "is"});
						
						Object value = method.invoke(object, new Object[0]);
						if(value!=null)
						{
							String xmlattrname = attrinfo.getXMLAttributeName()!=null? attrinfo.getXMLAttributeName(): propname;
							attrs.put(xmlattrname, ""+value);
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
			
			Map subobsinfos = typeinfo.getSubobjectInfos();
			if(subobsinfos!=null)
			{
				for(Iterator it=subobsinfos.values().iterator(); it.hasNext(); )
				{
					try
					{
						SubobjectInfo soinfo = (SubobjectInfo)it.next();
						String propname = (String)soinfo.getAttribute();
						Method method = findGetMethod(object, propname, new String[]{"get"});
						
						Object value = method.invoke(object, new Object[0]);
						if(value!=null)
						{
							String xmlsoname = soinfo.getXMLAttributeName()!=null? soinfo.getXMLAttributeName(): propname;
							subobs.put(xmlsoname, value);
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
								attrs.put(propname, ""+value);
							}
							else
							{
								subobs.put(propname, value);
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
		
		return new Object[]{attrs, content, subobs};
	}
	
	/**
	 * 
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
