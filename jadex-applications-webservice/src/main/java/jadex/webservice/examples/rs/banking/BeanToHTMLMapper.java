package jadex.webservice.examples.rs.banking;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import jadex.commons.traverser.BeanProperty;
import jadex.commons.traverser.BeanReflectionIntrospector;
import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class BeanToHTMLMapper implements IValueMapper
{
	protected static BeanReflectionIntrospector in = new BeanReflectionIntrospector();
	
	/**
	 * 
	 */
	public Object convertValue(Object result) throws Exception
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<html>");
		ret.append("<body>");
		
		ret.append("<h1>").append("Object Info for: ").append(result.getClass()).append("</h1>");
		
		Map<String, BeanProperty> props = in.getBeanProperties(result.getClass(), false);
		for(Iterator<String> it=props.keySet().iterator(); it.hasNext(); )
		{
			String name = it.next();
			Method getter = props.get(name).getGetter();
			Object value = getter.invoke(result, new Object[0]);
		
			ret.append("<div>");
			ret.append(name).append(" = ").append(""+value);
			ret.append("</div>");
		}
		
		ret.append("</body>");
		ret.append("</html>");
		return ret.toString();
	}
}
