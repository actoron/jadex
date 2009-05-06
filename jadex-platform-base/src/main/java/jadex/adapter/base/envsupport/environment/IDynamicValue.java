package jadex.adapter.base.envsupport.environment;

/**
 * An interface for dynamic properties.
 */
public interface IDynamicValue
{
	/**
	 * Evaluates and returns the current value.
	 * @return value
	 */
	public Object getValue();
}
