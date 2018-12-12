package jadex.webservice.examples.rs.banking;

import java.util.Iterator;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.DefaultBeanIntrospector;
import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class BeanToHTMLMapper implements IValueMapper
{
	protected static final DefaultBeanIntrospector in = new DefaultBeanIntrospector();
	
	/**
	 * 
	 */
	public Object convertValue(Object result) throws Exception
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<html>");
		ret.append("<body>");
		
		ret.append("<h1>").append(SReflect.getUnqualifiedClassName(result.getClass())).append("</h1>");
		
		Map<String, BeanProperty> props = in.getBeanProperties(result.getClass(), true, false);
		for(Iterator<String> it=props.keySet().iterator(); it.hasNext(); )
		{
			String name = it.next();
			BeanProperty prop = props.get(name);
			if (prop.isReadable())
			{
	//			Method getter = props.get(name).getGetter();
	//			Object value = getter.invoke(result, new Object[0]);
				Object value = props.get(name).getPropertyValue(result);
			
				ret.append("<div>");
				ret.append(name).append(" = ");
				ret.append(SUtil.arrayToString(value));
				ret.append("</div>");
			}
		}
		
		ret.append("</body>");
		ret.append("</html>");
		return ret.toString();
	}
}
