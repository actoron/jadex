package jadex.bridge.nonfunctional;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class SimpleValueNFProperty<T, U> extends AbstractNFProperty<T, U>
{
	/** The current value. */
	protected T value;
	
	/**
	 *  Create a new property.
	 */
	public SimpleValueNFProperty(NFPropertyMetaInfo mi)
	{
		super(mi);
	}

	/**
	 *  Get the value.
	 */
	public IFuture<T> getValue(Class<U> unit)
	{
		return new Future<T>(value);
	}
	
	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(T value)
	{
		this.value = value;
	}
}
