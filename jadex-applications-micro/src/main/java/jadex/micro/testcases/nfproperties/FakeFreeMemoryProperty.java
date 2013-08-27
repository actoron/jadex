package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.sensor.memory.MemoryProperty;
import jadex.bridge.sensor.memory.MemoryProperty.MemoryUnit;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class FakeFreeMemoryProperty extends AbstractNFProperty<Long, MemoryProperty.MemoryUnit>
{
	long fakemem = (long) Math.round(Math.random() * 17179869184.0);
	
	public FakeFreeMemoryProperty()
	{
		super(new NFPropertyMetaInfo("fakecpu", Long.class, MemoryProperty.MemoryUnit.class, false, -1, null));
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
