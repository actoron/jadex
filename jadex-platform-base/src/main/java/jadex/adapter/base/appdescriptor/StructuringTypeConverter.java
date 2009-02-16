package jadex.adapter.base.appdescriptor;

import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *  Converter for structuring type.
 */
public class StructuringTypeConverter implements Converter
{
	//-------- methods --------
	
	/**
	 *  Test if class is handled by converter.
	 *  @param clazz The class.
	 */
	public boolean canConvert(Class clazz)
	{
		return StructuringType.class.isAssignableFrom(clazz);
	}

	/**
	 *  Write a value.
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
		MarshallingContext context)
	{
		StructuringType st = (StructuringType)value;
		writer.addAttribute("name", st.getName());
		writer.addAttribute("type", st.getType());
		Map props = st.getProperties();
		if(props!=null)
		{
			for(Iterator it=props.keySet().iterator(); it.hasNext();)
			{
				String propname = (String)it.next();
				writer.startNode("property");
				writer.addAttribute("name", propname);
				writer.setValue((String)props.get(propname));
				writer.endNode();
			}
		}
	}

	/**
	 *  Read a value.
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
		UnmarshallingContext context)
	{
		StructuringType st = new StructuringType();
		st.setName(reader.getAttribute("name"));
		st.setType(reader.getAttribute("type"));
		
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			st.addProperty(reader.getAttribute("name"), reader.getValue());
			reader.moveUp();
		}
		
		return st;
	}

}