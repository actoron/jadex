package jadex.base.gui;

import java.util.Map;

/**
 *  The element string generator converts an element (as map resp. encodable
 *  representation) to a string representation.
 */
public class ElementStringGenerator implements IRepresentationConverter
{
	//-------- attributes ---------

	/** The indent width. */
	protected int indentwidth;

	//-------- constructors ---------

	/**
	 *  Create a new generator.
	 */
	public ElementStringGenerator()
	{
		this(4);
	}

	/**
	 *  Create a new generator.
	 */
	public ElementStringGenerator(int indentwidth)
	{
		this.indentwidth = indentwidth;
	}

	//-------- methods ---------

	/**
	 *  Convert the map of attributes to a formatted string.
	 */
	public String convert(Map element)
	{
		return convert(element, 0);
	}

	/**
	 *  Convert the map of attributes to a formatted string.
	 */
	protected String convert(Map element, int indent)
	{
		StringBuffer buf = new StringBuffer();

		for(int i=0; i<indent*indentwidth && indent==0; i++)
			buf.append(" ");
		String clazz = (String)element.get("class");
		clazz = clazz==null? "class=n/a": clazz;
		String name = (String)element.get("name");
		name = name==null? "name=n/a": name;
		buf.append(clazz+" "+name+"\n");

		String[] keys = (String[])element.keySet().toArray(new String[element.keySet().size()]);
		for(int i=0; i<keys.length; i++)
		{
			if(!(keys[i].equals("name") || keys[i].equals("class")))
			{
				for(int j=0; j<(indent+1)*indentwidth; j++)
					buf.append(" ");
				if(element.get(keys[i]) instanceof Map)
				{
					buf.append(keys[i]+": "+convert((Map)element.get(keys[i]), indent+1));
				}
				else
				{
					buf.append(keys[i]+": "+element.get(keys[i])+"\n");
				}
			}
		}
		return buf.toString();
	}

}
