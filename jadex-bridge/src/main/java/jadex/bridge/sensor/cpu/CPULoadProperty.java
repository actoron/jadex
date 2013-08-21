package jadex.bridge.sensor.cpu;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.commons.OperatingSystemMXBeanFacade;

/**
 *  The cpu load property.
 */
public class CPULoadProperty extends NFRootProperty<Double, Void>
{
	/** The name of the property. */
	public static final String CPULOAD = "cpu load";
	
	/**
	 *  Create a new property.
	 */
	public CPULoadProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(CPULOAD, double.class, null, true, 10000, Target.Root));
	}
	
	/**
	 *  Measure the value.
	 */
	public Double measureValue()
	{
		return OperatingSystemMXBeanFacade.getSystemCpuLoad();
	}
}

