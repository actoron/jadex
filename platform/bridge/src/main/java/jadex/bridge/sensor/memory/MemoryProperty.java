package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.bridge.sensor.unit.MemoryUnit;

/**
 *  Abstract base memory property.
 */
public abstract class MemoryProperty extends NFRootProperty<Long, MemoryUnit>
{
	/**
	 *  Create a new property.
	 */
	public MemoryProperty(String name, final IInternalAccess comp, long updaterate)
	{
		super(comp, new NFPropertyMetaInfo(name, long.class, MemoryUnit.class, 
			updaterate>0? true: false, updaterate, true, Target.Root));
	}
}
