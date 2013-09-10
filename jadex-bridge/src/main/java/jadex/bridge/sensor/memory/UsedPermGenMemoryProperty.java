package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

/**
 * 
 */
public class UsedPermGenMemoryProperty extends MemoryProperty
{
	/** The name of the property. */
	public static final String NAME = "used permgen memory";
	
	/**
	 *  Create a new property.
	 */
	public UsedPermGenMemoryProperty(final IInternalAccess comp)
	{
		super(NAME, comp, 5000);
	}
	
	/**
	 *  Measure the value.
	 */
	public Long measureValue()
	{
		final MemoryPoolMXBean pool = getPermGenMemoryPool();
		return pool!=null? pool.getUsage().getUsed(): -1;
	}
	
	/**
	 *  Get the perm gen pool.
	 */
	protected static MemoryPoolMXBean getPermGenMemoryPool()
	{
		MemoryPoolMXBean ret = null;
		for(final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans())
		{
			if(memoryPool.getName().endsWith("Perm Gen"))
			{
				ret = memoryPool;
				break;
			}
		}
		return ret;
	}
}
