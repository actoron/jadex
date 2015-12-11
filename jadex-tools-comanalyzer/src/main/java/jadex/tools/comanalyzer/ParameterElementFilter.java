package jadex.tools.comanalyzer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;


/**
 * The parameter element filter filters ParameterElement objects based on their
 * parameter values. Parameter values of elements can be matched against
 * required values, or recursively against other filters. When more than one
 * value and / or filter is specified for an attribute, only one of those has to
 * match.
 */
public class ParameterElementFilter implements IFilter, Serializable
{

	// -------- attributes ---------

	/** The required attribute values. */
	protected MultiCollection<String, Object> values;

	// -------- methods --------

	public ParameterElementFilter()
	{
		this.values = new MultiCollection<String, Object>();
	}

	/**
	 * Add a required attribute value. The attribute value is checked with
	 * equals().
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	public void addValue(String name, Object value)
	{
		values.add(name, value);
	}

	/**
	 * Removes an attribute value.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	public void removeValue(String name, Object value)
	{
		if(values.containsKey(name))
		{
			values.removeObject(name, value);
		}
	}

	/**
	 * Returns true if the filter contains the given attribute value.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @return true if the filter contains the given attribute value.
	 */
	public boolean containsValue(String name, Object value)
	{

		if(values.containsKey(name))
		{
			Collection<Object> vals = values.getCollection(name);
			if(vals.contains(value))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the filter has values.
	 */
	public boolean hasValues()
	{
		return !values.isEmpty();
	}

	/**
	 * Match an object against the filter.
	 * 
	 * @param object The object.
	 * @return True, if the filter matches.
	 * @throws Exception
	 */
	public boolean filter(Object object)
	{

		boolean ret = false;
		if(object instanceof ParameterElement)
		{
			ParameterElement probj = (ParameterElement)object;
			// Check, that all properties match.

			Iterator<String> vkeys = values.keySet().iterator();

			// Check all values; ret is AND
			ret = true;
			while(vkeys.hasNext() && ret)
			{
				// Todo: parameter sets???
				String vkey = (String)vkeys.next();
				if(!probj.hasParameter(vkey))
				{
					ret = false;
					break;
				}

				Object val = probj.getParameter(vkey);
				Object[] vals = values.get(vkey).toArray();

				// Has to match at least one; ret is OR.
				// included null values
				ret = false;
				for(int j = 0; j < vals.length && !ret; j++) // ret is OR
				{
					if(vals[j] == null)
					{
						ret = val == null;
					}
					else
					{
						ret = vals[j].equals(val);
					}
				}
			}


		}

		return ret;
	}

	/**
	 * Create a string representation of this filter.
	 * 
	 * @return A string representing this filter.
	 */
	public String toString()
	{

		StringBuffer sb = new StringBuffer();
		sb.append(SReflect.getInnerClassName(this.getClass()));
		sb.append("(values=[");
		if(values != null)
		{
			Object[] vkeys = values.getKeys();
			for(int i = 0; i < vkeys.length; i++)
			{
				sb.append(vkeys[i] + " = " + values.get(vkeys[i]));
				if(i < vkeys.length - 1)
				{
					sb.append(", ");
				}
			}
		}
		sb.append("])");
		return sb.toString();
	}

}
