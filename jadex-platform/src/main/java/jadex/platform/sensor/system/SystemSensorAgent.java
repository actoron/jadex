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
	@NFProperty(value=CPULoadProperty.class),//, target=Target.Root))
	@NFProperty(value=CoreNumberProperty.class),
	@NFProperty(value=MaxMemoryProperty.class),
	@NFProperty(value=UsedMemoryProperty.class),
	@NFProperty(value=MaxPermGenMemoryProperty.class),
	@NFProperty(value=UsedPermGenMemoryProperty.class),
	@NFProperty(value=LoadedClassesProperty.class)
})
public class SystemSensorAgent
{
}
