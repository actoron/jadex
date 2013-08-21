package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.commons.OperatingSystemMXBeanFacade;

/**
 *  The maximum physical memory.
 */
public class MaxMemoryProperty extends MemoryProperty
{
	/** The name of the property. */
	public static final String MAXMEMORY = "max memory";
	
	/**
	 *  Create a new property.
	 */
	public MaxMemoryProperty(final IInternalAccess comp)
	{
		super(MAXMEMORY, comp, -1);
	}
	
	/**
	 *  Measure the value.
	 */
	public Long measureValue()
	{
		return new Long(OperatingSystemMXBeanFacade.getTotalPhysicalMemorySize());
	}
}