package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The maximum memory.
 */
public class MaxMemoryProperty extends NFRootProperty<Long, MaxMemoryProperty.MemoryUnit>
{
	public static enum MemoryUnit{B, KB, MB, GB, TB}
	
	/** The name of the property. */
	public static final String MAXMEMORY = "max memory";
	
	/**
	 *  Create a new property.
	 */
	public MaxMemoryProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(MAXMEMORY, long.class, MemoryUnit.class, true, -1, Target.Root));
//		value = Runtime.getRuntime().maxMemory();
//		value = Runtime.getRuntime().totalMemory();
		
		com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		value = os.getTotalPhysicalMemorySize();
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