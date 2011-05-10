package jadex.bridge.modelinfo;

import jadex.commons.SUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *  Default model value provider.
 */
public class ModelValueProvider implements IModelValueProvider
{
	//-------- constants --------
	
	/** Constant for no configuration selected. */
	public static final String ANY_CONFIG = "any_config";
	
	//-------- attributes --------
	
	/** The values. */
	protected Map values;
	
	//-------- constructors --------
	
	/**
	 *  Create a new provider.
	 */
	public ModelValueProvider()
	{
	}
	
	/**
	 *  Create a new provider.
	 */
	public ModelValueProvider(Object value)
	{
		this(SUtil.createHashMap(new Object[]{ANY_CONFIG}, new Object[]{value}));
	}
	
	/**
	 *  Create a new provider.
	 */
	public ModelValueProvider(Map values)
	{
		this.values = values;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue(String configname)
	{
		Object ret = null;
		if(values!=null)
		{
			ret = values.get(configname!=null && values.containsKey(configname)? configname: ANY_CONFIG);
		}
		return ret;
	}
	
	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		if(values==null)
			values = new HashMap();
		values.put(ANY_CONFIG, value);
	}
	
	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String configname, Object value)
	{
		if(values==null)
			values = new HashMap();
		values.put(configname, value);
	}

	/**
	 *  Get the values.
	 *  @return the values.
	 */
	public Map getValues()
	{
		return values;
	}

	/**
	 *  Set the values.
	 *  @param values The values to set.
	 */
	public void setValues(Map values)
	{
		this.values = values;
	}
}
