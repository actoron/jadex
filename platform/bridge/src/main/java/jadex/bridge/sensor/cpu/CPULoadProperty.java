package jadex.bridge.sensor.cpu;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.bridge.sensor.unit.PercentUnit;
import jadex.commons.OperatingSystemMXBeanFacade;

/**
 *  The cpu load property.
 */
public class CPULoadProperty extends NFRootProperty<Double, Void>
{
	/** The name of the property. */
	public static final String NAME = "cpu load";
	
	/**
	 *  Create a new property.
	 */
	public CPULoadProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(NAME, double.class, PercentUnit.class, true, 3000, true, Target.Root));
	}
	
	/**
	 *  Measure the value.
	 */
	public Double measureValue()
	{
		Double ret = OperatingSystemMXBeanFacade.getSystemCpuLoad();
		//System.out.println("measure value on cpu prop: "+ret);
		return ret;
	}
}

