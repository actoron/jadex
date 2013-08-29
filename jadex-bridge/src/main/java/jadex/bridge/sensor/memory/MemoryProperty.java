package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public abstract class MemoryProperty extends NFRootProperty<Long, MemoryUnit>
{
	/**
	 *  Create a new property.
	 */
	public MemoryProperty(String name, final IInternalAccess comp, long updaterate)
	{
		super(comp, new NFPropertyMetaInfo(name, long.class, MemoryUnit.class, 
			updaterate>0? true: false, updaterate, Target.Root));
	}
}
