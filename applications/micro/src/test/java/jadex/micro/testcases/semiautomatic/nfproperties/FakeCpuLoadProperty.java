package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;

/**
 *  Property simulating a random amount of CPU load.
 */
public class FakeCpuLoadProperty extends SimpleValueNFProperty<Double, Void>
{
	/**
	 * Creates the property.
	 */
	public FakeCpuLoadProperty(IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo("fakecpuload", Double.class, Void.class, true, 10000, true, null));
	}
	
	/**
	 *  Measure the value.
	 */
	public Double measureValue()
	{
		return Math.random() * 100.0;
	}
}
