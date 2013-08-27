package jadex.micro.testcases.nfproperties;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;

public class FakeCpuLoadProperty extends SimpleValueNFProperty<Double, Void>
{
	public FakeCpuLoadProperty(IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo("fakecpuload", Double.class, Void.class, true, 10000, null));
	}

	public Double measureValue()
	{
		return Math.random() * 100.0;
	}
}
