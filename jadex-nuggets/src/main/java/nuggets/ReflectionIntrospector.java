/*
 * ReflectionIntrospector.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Mar 22, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** ReflectionIntrospector 
 * @author walczak
 * @since  Mar 22, 2006
 */
public class ReflectionIntrospector implements IBeanIntrospector
{

	/** 
	 * @param clazz
	 * @return a map of properties
	 * @see nuggets.IBeanIntrospector#getBeanProperties(java.lang.Class)
	 */
	public Map getBeanProperties(Class clazz)
	{
		try
		{
			Method[] ms = clazz.getMethods();
//          ------------------ precalculate method bodies --------------------------       
			// get the properties
			HashMap getters = new HashMap();
			ArrayList setters = new ArrayList();
            for(int k=0; k<ms.length; k++) 
            {
            	String method_name=ms[k].getName();
            	if (method_name.startsWith("is") || method_name.startsWith("get")) getters.put(method_name, ms[k]);
            	else if (method_name.startsWith("set")) setters.add(ms[k]);
            }
            
            HashMap props=new HashMap();
            Iterator it=setters.iterator();
            
            while(it.hasNext()) {
				Method setter = (Method)it.next();
				String setter_name= setter.getName();
				String property_name = setter_name.substring(3);
				Method getter = (Method)getters.get("get" + property_name);
				if (getter==null) getter=(Method)getters.get("is" + property_name);
				if (getter != null) {
					Class[] setter_param_type = setter.getParameterTypes();
					if (setter_param_type.length==1) {
						String property_java_name = Character.toLowerCase(property_name.charAt(0))+property_name.substring(1);
						props.put(property_java_name, new BeanProperty(property_java_name, getter.getReturnType(), getter, setter, setter_param_type[0]));
					}
				}
			}
			
			return props;
		}
		catch(Exception e)
		{	
			throw new PersistenceException(e.getMessage());
		}
	}

}


/* 
 * $Log$
 * Revision 1.2  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.1  2006/03/22 17:17:00  walczak
 * added an reflective introspector
 *
 */