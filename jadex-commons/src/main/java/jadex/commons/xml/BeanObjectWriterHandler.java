package jadex.commons.xml;

import jadex.commons.SReflect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 */
public class BeanObjectWriterHandler implements IObjectWriterHandler
{
	protected IBeanIntrospector introspector = new ReflectionIntrospector();
	
	/**
	 *  Get attributes of an object as name value pairs.
	 */
	public Object[] getAttributesAndSubobjects(Object object, TypeInfo typeinfo)
	{
		Map attrs = new HashMap();
		Map subobs = new HashMap();
		
		if(typeinfo!=null)
		{
			// Attributes can be found via attribute infos.
			
			Collection attrinfos = typeinfo.getAttributeInfos();
			if(attrinfos!=null)
			{
				for(Iterator it=attrinfos.iterator(); it.hasNext(); )
				{
					try
					{
						BeanAttributeInfo attrinfo = (BeanAttributeInfo)it.next();
						String propname = attrinfo.getAttributeName();
						String methodname = "get"+propname;
						Method method = object.getClass().getMethod(methodname, new Class[0]);
						if(method==null)
						{
							methodname = "is"+propname;
							method = object.getClass().getMethod(methodname, new Class[0]);
						}
						if(method==null)
						{
							throw new RuntimeException("No getter found for: "+propname);
						}
						Object value = method.invoke(object, new Object[0]);
						
						String xmlattrname = attrinfo.getXMLAttributeName()!=null? attrinfo.getXMLAttributeName(): propname;
						attrs.put(xmlattrname, ""+value);
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
						BeanSubobjectInfo soinfo = (BeanSubobjectInfo)it.next();
						String propname = soinfo.getAttributeName();
						String methodname = "get"+propname;
						Method method = object.getClass().getMethod(methodname, new Class[0]);
						if(method==null)
						{
							throw new RuntimeException("No getter found for: "+propname);
						}
						Object value = method.invoke(object, new Object[0]);
						
						String xmlattrname = soinfo.getXMLAttributeName()!=null? soinfo.getXMLAttributeName(): propname;
						attrs.put(xmlattrname, value);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else
		{	
			Map props = introspector.getBeanProperties(object.getClass());
			if(props!=null)
			{
				for(Iterator it=props.keySet().iterator(); it.hasNext(); )
				{
					String propname = (String)it.next();
					BeanProperty bp = (BeanProperty)props.get(propname);
					try
					{
						Object value = bp.getGetter().invoke(object, new Object[0]);
		
						if(BasicTypeConverter.isBuiltInType(bp.getType()))
						{
							attrs.put(propname, ""+value);
						}
						else
						{
							subobs.put(propname, value);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return new Object[]{attrs, subobs};
	}
	
	/**
	 *  Get the tag for an object.
	 */
	public String getTag(Object object, TypeInfo typeinfo)
	{
		// Hack! use typeinfo
		return SReflect.getInnerClassName(object.getClass());
	}
}
