package jadex.commons;

/**
 *  Interface that can be used for validation, e.g., of text in a textfield.
 */
public interface IValidator 
{
	/**
	 *  Return true when the given object is valid.
	 */
	public boolean isValid(Object object);
}
