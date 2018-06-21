package jadex.bpmn.model;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.UnparsedExpression;

/**
 * 
 */
public class MContextVariable extends Argument
{
	/** Initial values for configurations */
	protected Map<String, UnparsedExpression> configinitialvalues;
	
	/**
	 *  Create a new argument.
	 */
	public MContextVariable()
	{
	}
	
	/**
	 *  Create a new argument.
	 */
	public MContextVariable(String name, String description, String classname, String defaultvalue)
	{
		super(name, description, classname, defaultvalue);
	}
	
	/**
	 *  Removes the value for a specific configuration.
	 *  
	 *  @param config The configuration.
	 *  @return The expression.
	 */
	public UnparsedExpression removeValue(String config)
	{
		return configinitialvalues != null? configinitialvalues.remove(config) : null;
	}
	
	/**
	 *  Get the value for a specific configuration.
	 *  
	 *  @param config The configuration.
	 *  @return The expression.
	 */
	public UnparsedExpression getValue(String config)
	{
		return configinitialvalues != null? configinitialvalues.get(config) != null? configinitialvalues.get(config) : this : this;
	}
	
	/**
	 *  Get the value for a specific configuration only.
	 *  
	 *  @param config The configuration.
	 *  @return The expression.
	 */
	public UnparsedExpression getConfigValue(String config)
	{
		return configinitialvalues != null? configinitialvalues.get(config) : null;
	}
	
	/**
	 *  Set the value for a specific configuration.
	 *  
	 *  @param config The configuration.
	 *  @return The expression.
	 */
	public void setValue(String config, UnparsedExpression value)
	{
		if (configinitialvalues == null)
		{
			configinitialvalues = new HashMap<String, UnparsedExpression>();
		}
		configinitialvalues.put(config, value);
	}
}
