/*
 * BeanInfoIntrospector.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Mar 22, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/** BeanInfoIntrospector 
 * @author walczak
 * @since  Mar 22, 2006
 */
public class BeanInfoIntrospector implements IBeanIntrospector
{

	/** 
	 * @param clazz
	 * @return a map from property names to bean propeties
	 * @see nuggets.IBeanIntrospector#getBeanProperties(java.lang.Class)
	 */
	public Map getBeanProperties(Class clazz)
	{
		try
		{
			BeanInfo bi = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
//          ------------------ precalculate method bodies --------------------------       
			// get the properties
			HashMap props = new HashMap();
            for(int k=0; k<pds.length; k++) 
            {
				PropertyDescriptor pd = pds[k];
				Method setter = pd.getWriteMethod();
				Method getter = pd.getReadMethod();
				if (setter != null && getter != null) {
					Class[] setter_param_type = setter.getParameterTypes();
					if (setter_param_type.length==1) {
						props.put(pd.getName(), new BeanProperty(pd.getName(), pd.getPropertyType(), getter, setter, setter_param_type[0]));
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