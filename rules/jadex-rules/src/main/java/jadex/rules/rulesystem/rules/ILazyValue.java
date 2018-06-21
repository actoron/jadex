package jadex.rules.rulesystem.rules;

/**
 *  Interface for a value that is provided lazy, i.e. only when getValue() is called.
 */
public interface ILazyValue
{
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue();
}
