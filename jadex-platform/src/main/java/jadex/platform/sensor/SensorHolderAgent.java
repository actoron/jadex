package jadex.platform.sensor;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  Component that holds the sensors in the platform.
 */
@Agent
@ComponentTypes(
{
	@ComponentType(name="cpusensor", filename="jadex/platform/sensor/cpu/CPUSensorAgent.class")
})
@Configurations(@Configuration(name="def", components=
{
	@Component(type="cpusensor")
}))
public class SensorHolderAgent
{
}
