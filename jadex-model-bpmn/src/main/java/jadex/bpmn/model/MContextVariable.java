package jadex.bpmn.model;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.HashMap;
import java.util.Map;

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
		return configinitialvalues != null? configinitialvalues.get(config) : this;
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
