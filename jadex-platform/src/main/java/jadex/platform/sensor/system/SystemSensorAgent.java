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
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent
@NFProperties(
{
	@NFProperty(name="cpuload", type=CPULoadProperty.class),
	@NFProperty(name="cpucores", type=CoreNumberProperty.class),
	@NFProperty(name="maxmem", type=MaxMemoryProperty.class),
	@NFProperty(name="usedmem", type=UsedMemoryProperty.class),
	@NFProperty(name="maxpermgen", type=MaxPermGenMemoryProperty.class),
	@NFProperty(name="usedpermgen", type=UsedPermGenMemoryProperty.class),
	@NFProperty(name="loadedclasses", type=LoadedClassesProperty.class)
})
public class SystemSensorAgent
{
}
