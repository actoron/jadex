package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.commons.OperatingSystemMXBeanFacade;

/**
 *  The used physical memory.
 */
public class UsedMemoryProperty extends MemoryProperty
{
	/** The name of the property. */
	public static final String NAME = "used memory";
	
	/**
	 *  Create a new property.
	 */
	public UsedMemoryProperty(final IInternalAccess comp)
	{
		super(NAME, comp, 5000);
	}
	
	/**
	 *  Measure the value.
	 */
	public Long measureValue()
	{
		return Long.valueOf(OperatingSystemMXBeanFacade.getTotalPhysicalMemorySize() 
			- OperatingSystemMXBeanFacade.getFreePhysicalMemorySize());
	}
}
