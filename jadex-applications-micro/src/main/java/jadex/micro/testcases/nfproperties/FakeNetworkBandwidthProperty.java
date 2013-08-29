package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class FakeNetworkBandwidthProperty extends AbstractNFProperty<Long, MemoryUnit>
{
	long fakemem = (long) Math.round(Math.random() * 17179869184.0);
	
	public FakeNetworkBandwidthProperty()
	{
		super(new NFPropertyMetaInfo("fakenetworkbandwith", Long.class, MemoryUnit.class, false, -1, null));
	}
	
	public IFuture<Long> getValue(MemoryUnit unit)
	{
		long ret = fakemem;
		if(unit!=null)
		{
			ret = unit.convert(ret);
		}
		
		return new Future<Long>(ret);
	}
}
