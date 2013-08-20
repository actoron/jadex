package jadex.bridge.sensor.cpu;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;

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
		super(comp, new NFPropertyMetaInfo(CPULOAD, double.class, null, true, -1, Target.Root));
	}
}

