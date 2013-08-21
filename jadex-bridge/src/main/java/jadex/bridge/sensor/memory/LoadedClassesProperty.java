package jadex.bridge.sensor.memory;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;

import java.lang.management.ManagementFactory;

/**
 *  Property for the number of loaded classes in the JVM.
 */
public class LoadedClassesProperty extends NFRootProperty<Integer, MemoryProperty.MemoryUnit>
{
	/** The name of the property. */
	public static final String LOADEDCLASSES = "loaded classes";
	
	/**
	 *  Create a new property.
	 */
	public  LoadedClassesProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(LOADEDCLASSES, int.class, null, 
			true, 5000, Target.Root));
	}
	
	/**
	 *  Measure the value.
	 */
	public Integer measureValue()
	{
		return new Integer(ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
	}
}
