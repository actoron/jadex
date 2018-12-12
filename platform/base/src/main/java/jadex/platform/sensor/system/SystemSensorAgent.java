package jadex.platform.sensor.system;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.sensor.cpu.CPULoadProperty;
import jadex.bridge.sensor.cpu.CoreNumberProperty;
import jadex.bridge.sensor.mac.MacAddressProperty;
import jadex.bridge.sensor.memory.LoadedClassesProperty;
import jadex.bridge.sensor.memory.MaxMemoryProperty;
import jadex.bridge.sensor.memory.MaxPermGenMemoryProperty;
import jadex.bridge.sensor.memory.UsedMemoryProperty;
import jadex.bridge.sensor.memory.UsedPermGenMemoryProperty;
import jadex.bridge.sensor.time.ComponentUptimeProperty;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;

/**
 *  Agent that installs some top-level non-functional properties
 *  including the corresponding sensors.
 */
@Agent
@NFProperties(
{
	@NFProperty(MacAddressProperty.class),
	@NFProperty(CPULoadProperty.class),
	@NFProperty(CoreNumberProperty.class),
	@NFProperty(MaxMemoryProperty.class),
	@NFProperty(UsedMemoryProperty.class),
	@NFProperty(MaxPermGenMemoryProperty.class),
	@NFProperty(UsedPermGenMemoryProperty.class),
	@NFProperty(LoadedClassesProperty.class),
	@NFProperty(ComponentUptimeProperty.class)
})
@Properties(@NameValue(name="system", value="true"))
public class SystemSensorAgent
{
}
