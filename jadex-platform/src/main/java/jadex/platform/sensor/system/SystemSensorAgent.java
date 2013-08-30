package jadex.platform.sensor.system;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.cpu.CPULoadProperty;
import jadex.bridge.sensor.cpu.CoreNumberProperty;
import jadex.bridge.sensor.memory.LoadedClassesProperty;
import jadex.bridge.sensor.memory.MaxMemoryProperty;
import jadex.bridge.sensor.memory.MaxPermGenMemoryProperty;
import jadex.bridge.sensor.memory.UsedMemoryProperty;
import jadex.bridge.sensor.memory.UsedPermGenMemoryProperty;
import jadex.micro.annotation.Agent;

/**
 *  Agent that installs some top-level non-functional properties
 *  including the corresponding sensors.
 */
@Agent
@NFProperties(
{
	@NFProperty(CPULoadProperty.class),
	@NFProperty(CoreNumberProperty.class),
	@NFProperty(MaxMemoryProperty.class),
	@NFProperty(UsedMemoryProperty.class),
	@NFProperty(MaxPermGenMemoryProperty.class),
	@NFProperty(UsedPermGenMemoryProperty.class),
	@NFProperty(LoadedClassesProperty.class)
})
public class SystemSensorAgent
{
}
