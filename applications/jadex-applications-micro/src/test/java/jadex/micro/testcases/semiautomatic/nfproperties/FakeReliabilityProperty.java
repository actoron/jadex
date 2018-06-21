package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;

/**
 *  Property simulating a random service reliability.
 *
 */
public class FakeReliabilityProperty extends SimpleValueNFProperty<Double, Void>
{
	/**
	 * Creates the property.
	 */
	public FakeReliabilityProperty(IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo("fakereliability", Double.class, Void.class, true, 10000, true, null));
	}
	
	/**
	 *  Measure the value.
	 */
	public Double measureValue()
	{
		return Math.random() * 100.0;
	}
}
