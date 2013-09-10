package jadex.bridge.sensor.cpu;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;

/**
 *  The number of cores.
 */
public class CoreNumberProperty extends NFRootProperty<Integer, Void>
{
	/** The name of the property. */
	public static final String NAME = "cpu cores";
	
	/**
	 *  Create a new property.
	 */
	public CoreNumberProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(NAME, int.class, null, false, -1, Target.Root));
		setValue(new Integer(Runtime.getRuntime().availableProcessors()));
	}
	
	/**
	 *  Measure the value.
	 */
	public Integer measureValue()
	{
		return new Integer(Runtime.getRuntime().availableProcessors());
	}
}
