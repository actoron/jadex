package jadex.bridge.nonfunctional;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public abstract class SimpleValueNFProperty<T, U> extends AbstractNFProperty<T, U>
{
	/** The current value. */
	protected T value;
	
	/** The component. */
	protected IInternalAccess comp;
	
	/**
	 *  Create a new property.
	 */
	public SimpleValueNFProperty(final IInternalAccess comp, final NFPropertyMetaInfo mi)
	{
		super(mi);
		this.comp = comp;
		
		if(mi.isDynamic())
		{
			IResultListener<Void> res = new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					setValue(measureValue());
					comp.waitForDelay(mi.getUpdateRate()).addResultListener(this);
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			};
			
			comp.waitForDelay(mi.getUpdateRate()).addResultListener(res);
		}
		else
		{
			setValue(measureValue());
		}
	}

	/**
	 *  Get the value.
	 */
	public IFuture<T> getValue(U unit)
//	public IFuture<T> getValue(Class<U> unit)
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
	
	/**
	 *  Measure the value.
	 */
	public abstract T measureValue();
}
