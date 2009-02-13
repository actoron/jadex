package jadex.adapter.base.appdescriptor;

import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *  Converter for parameter sets.
 */
public class ParameterSetConverter implements Converter
{
	//-------- methods --------
	
	/**
	 *  Test if class is handled by converter.
	 *  @param clazz The class.
	 */
	public boolean canConvert(Class clazz)
	{
		return ParameterSet.class.isAssignableFrom(clazz);
	}

	/**
	 *  Write a value.
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		ParameterSet ps = (ParameterSet)value;
		writer.addAttribute("name", ps.getName());
		List vals = ps.getValues();
		if(vals!=null)
		{
			for(int i=0; i<vals.size(); i++)
			{
				writer.startNode("value");
				writer.setValue((String)vals.get(i));
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
		ParameterSet ps = new ParameterSet();
		ps.setName(reader.getAttribute("name"));
		
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			ps.addValue(reader.getValue());
			reader.moveUp();
		}
		
		return ps;
	}

}