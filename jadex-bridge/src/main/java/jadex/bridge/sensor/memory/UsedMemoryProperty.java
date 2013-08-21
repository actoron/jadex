package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.commons.OperatingSystemMXBeanFacade;

/**
 *  The used physical memory.
 */
public class UsedMemoryProperty extends MemoryProperty
{
	/** The name of the property. */
	public static final String USEDMEMORY = "used memory";
	
	/**
	 *  Create a new property.
	 */
	public UsedMemoryProperty(final IInternalAccess comp)
	{
		super(USEDMEMORY, comp, 5000);
	}
	
	/**
	 *  Measure the value.
	 */
	public Long measureValue()
	{
		return new Long(OperatingSystemMXBeanFacade.getTotalPhysicalMemorySize() 
			- OperatingSystemMXBeanFacade.getFreePhysicalMemorySize());
	}
}
