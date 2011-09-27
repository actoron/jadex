package jadex.bridge.modelinfo;


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
	protected Map<String, Object> values;
	
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
	public ModelValueProvider(Map<String, Object> values)
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
			values = new HashMap<String, Object>();
		values.put(ANY_CONFIG, value);
	}
	
	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String configname, Object value)
	{
		if(values==null)
			values = new HashMap<String, Object>();
		values.put(configname, value);
	}

	/**
	 *  Get the values.
	 *  @return the values.
	 */
	public Map<String, Object> getValues()
	{
		return values;
	}

	/**
	 *  Set the values.
	 *  @param values The values to set.
	 */
	public void setValues(Map<String, Object> values)
	{
		this.values = values;
	}
}
