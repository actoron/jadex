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
	@NFProperty(name="cpuload", type=CPULoadProperty.class),//, target=Target.Root))
	@NFProperty(name="cpucores", type=CoreNumberProperty.class),
	@NFProperty(name="maxmem", type=MaxMemoryProperty.class),
	@NFProperty(name="usedmem", type=UsedMemoryProperty.class),
	@NFProperty(name="maxpermgen", type=MaxPermGenMemoryProperty.class),
	@NFProperty(name="usedpermgen", type=UsedPermGenMemoryProperty.class),
	@NFProperty(name="loadedclasses", type=LoadedClassesProperty.class)
})
public class SystemSensorAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The cpu load property for exposing it to nf automatically. */
	//@NFProp()
	//protected double cpuload;
	
//	 */
//	@AgentBody
//	public void body() throws Exception
//	{
//		IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				try
//				{
//	//				System.out.println(osb.getSystemLoadAverage());
//					double load = OperatingSystemMXBeanFacade.getSystemCpuLoad();
//					CPULoadProperty cp = (CPULoadProperty)agent.getNfProperty(CPULoadProperty.CPULOAD);
//					cp.setValue(new Double(load));
//					
//					MaxMemoryProperty p = (MaxMemoryProperty)agent.getNfProperty(MaxMemoryProperty.MAXMEMORY);
//					
////					System.out.println(load);
//					agent.scheduleStep(this, 5000);
//				}
//				catch(Exception e)
//				{
//					throw new RuntimeException(e);
//				}
//				return IFuture.DONE;
//			}
//		};
//		
//		agent.scheduleStep(step, 5000);
//	}
	
//	@AgentKilled
//	public void killed()
//	{
//		System.out.println("killed: "+agent.getComponentIdentifier());
//	}
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		System.out.println("cpu usage: "+getCPUUsage());
//	}
}
