package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public abstract class MemoryProperty extends NFRootProperty<Long, MemoryProperty.MemoryUnit>
{
	/** The allowed units. */
	public static enum MemoryUnit{B, KB, MB, GB, TB}
	
	/**
	 *  Create a new property.
	 */
	public MemoryProperty(String name, final IInternalAccess comp, long updaterate)
	{
		super(comp, new NFPropertyMetaInfo(name, long.class, MemoryUnit.class, 
			updaterate>0? true: false, updaterate, Target.Root));
	}
	
	/**
	 *  Get the value.
	 */
	public IFuture<Long> getValue(MemoryUnit unit)
	{
		long ret = value;
		if(unit!=null)
		{
			if(MemoryUnit.KB.equals(unit))
			{
				ret = Math.round(ret/1024d);
			}
			else if(MemoryUnit.MB.equals(unit))
			{
				ret = Math.round(ret/1024d/1024);
			}
			else if(MemoryUnit.GB.equals(unit))
			{
				ret = Math.round(ret/1024d/1024/1024);
			}
			else if(MemoryUnit.TB.equals(unit))
			{
				ret = Math.round(ret/1024d/1024/1024/1024); //1024*1024*1024*1024; -> 0 :-(
			}
		}
		
		return new Future<Long>(ret);
	}
}
