package jadex.xml.bean;


import jadex.commons.collection.LRU;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
	public Map getBeanProperties(Class clazz)
	{
		Map ret = (Map)beaninfos.get(clazz);
		
		if(ret==null)
		{
			try
			{
				BeanInfo bi = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] pds = bi.getPropertyDescriptors();
				HashMap props = new HashMap();
	            for(int k=0; k<pds.length; k++) 
	            {
					PropertyDescriptor pd = pds[k];
					Method setter = pd.getWriteMethod();
					Method getter = pd.getReadMethod();
					if(setter != null && getter != null) 
					{
						Class[] setter_param_type = setter.getParameterTypes();
						if (setter_param_type.length==1) 
						{
							props.put(pd.getName(), new BeanProperty(pd.getName(), pd.getPropertyType(), getter, setter, setter_param_type[0]));
						}
					}
				}
				
	            beaninfos.put(clazz, ret);
				ret = props;
			}
			catch(Exception e)
			{	
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
}