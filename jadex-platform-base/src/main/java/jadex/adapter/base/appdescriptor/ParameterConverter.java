package jadex.adapter.base.appdescriptor;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *  Converter for parameters.
 */
public class ParameterConverter implements Converter
{
	//-------- methods --------
	
	/**
	 *  Test if class is handled by converter.
	 *  @param clazz The class.
	 */
	public boolean canConvert(Class clazz)
	{
		return Parameter.class.isAssignableFrom(clazz);
	}

	/**
	 *  Write a value.
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		Parameter p = (Parameter)value;
		writer.addAttribute("name", p.getName());
		writer.setValue(p.getValue());
	}

	/**
	 *  Read a value.
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		Parameter p = new Parameter();
		p.setName(reader.getAttribute("name"));
		p.setValue(reader.getValue());
		return p;
	}

}