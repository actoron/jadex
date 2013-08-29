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
 * 
 */
@Agent
@NFProperties(
{
	@NFProperty(type=CPULoadProperty.class),//, target=Target.Root))
	@NFProperty(type=CoreNumberProperty.class),
	@NFProperty(type=MaxMemoryProperty.class),
	@NFProperty(type=UsedMemoryProperty.class),
	@NFProperty(type=MaxPermGenMemoryProperty.class),
	@NFProperty(type=UsedPermGenMemoryProperty.class),
	@NFProperty(type=LoadedClassesProperty.class)
})
public class SystemSensorAgent
{
}
