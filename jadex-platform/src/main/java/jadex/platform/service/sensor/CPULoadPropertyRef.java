/**
 * 
 */
package jadex.platform.service.sensor;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *
 */
public class CPULoadPropertyRef extends AbstractNFProperty<Double, Void>
{
	/** The value source. */
	protected INFPropertyProvider source;
	
	/** The component of the ref. */
	protected IExternalAccess comp;
	
	/**
	 *  Create a new property.
	 */
	public CPULoadPropertyRef(INFPropertyProvider source, IExternalAccess comp)
	{
		super(new NFPropertyMetaInfo(CPULoadProperty.CPULOAD, double.class, null, true, -1, Target.Root));
		this.source = source;
		this.comp = comp;
	}

	/**
	 *  Returns the current value of the property, performs unit conversion if necessary.
	 *  
	 *  @param type Type of the value.
	 *  @param unit Unit of the returned value.
	 *  
	 *  @return The current value of the property.
	 */
//	public IFuture<Double> getValue(Class<Void> unit)
	public IFuture<Double> getValue(Void unit)
	{
		final Future<Double> ret = new Future<Double>();
		IFuture<Double> fut = source.getNFPropertyValue(getName());
		fut.addResultListener(new IResultListener<Double>()
		{
			public void resultAvailable(Double result)
			{
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				comp.removeNFProperty(CPULoadProperty.CPULOAD);
				ret.setException(exception);
			}
		});
		return ret;
	}
}
