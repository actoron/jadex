package jadex.commons.xml.bean;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class BeanInfoIntrospector implements IBeanIntrospector
{
	/**
	 * 
	 */
	public Map getBeanProperties(Class clazz)
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
			
			return props;
		}
		catch(Exception e)
		{	
			throw new RuntimeException(e);
		}
	}
}