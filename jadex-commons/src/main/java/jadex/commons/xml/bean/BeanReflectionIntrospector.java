package jadex.commons.xml.bean;


import jadex.commons.collection.LRU;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class BeanReflectionIntrospector implements IBeanIntrospector
{
	protected LRU beaninfos;
	
	/**
	 * 
	 */
	public Map getBeanProperties(Class clazz)
	{
		Map ret = (Map)(beaninfos!=null? beaninfos.get(clazz): null);
		
		if(ret==null)
		{
			Method[] ms = clazz.getMethods();
			HashMap getters = new HashMap();
			ArrayList setters = new ArrayList();
            for(int i=0; i<ms.length; i++) 
            {
            	String method_name=ms[i].getName();
            	if((method_name.startsWith("is") || method_name.startsWith("get"))
            		&& ms[i].getParameterTypes().length==0) 
            	{
            		getters.put(method_name, ms[i]);
            	}
            	else if(method_name.startsWith("set") && ms[i].getParameterTypes().length==1) 
            	{
            		setters.add(ms[i]);
            	}
            }
            
            ret = new HashMap();
            Iterator it=setters.iterator();
            
            while(it.hasNext()) 
            {
				Method setter = (Method)it.next();
				String setter_name= setter.getName();
				String property_name = setter_name.substring(3);
				Method getter = (Method)getters.get("get" + property_name);
				if(getter==null)
					getter = (Method)getters.get("is" + property_name);
				
				if(getter!=null) 
				{
					Class[] setter_param_type = setter.getParameterTypes();
					String property_java_name = Character.toLowerCase(property_name.charAt(0))+property_name.substring(1);
					ret.put(property_java_name, new BeanProperty(property_java_name, getter.getReturnType(), getter, setter, setter_param_type[0]));
				}
			}
            
            if(beaninfos==null)
            	beaninfos = new LRU(200);
            beaninfos.put(clazz, ret);
		}
            
		return ret;
	}
}