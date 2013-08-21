package jadex.platform.sensor.cpu;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.cpu.CPULoadProperty;
import jadex.bridge.sensor.cpu.CoreNumberProperty;
import jadex.bridge.sensor.memory.MaxMemoryProperty;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.management.MBeanServerConnection;
//import java.lang.management.OperatingSystemMXBean;
//import com.sun.management.OperatingSystemMXBean.*;

/**
 * 
 */
@Agent
@NFProperties(
{
	@NFProperty(name="cpuload", type=CPULoadProperty.class),//, target=Target.Root))
	@NFProperty(name="cpucores", type=CoreNumberProperty.class),
	@NFProperty(name="maxmem", type=MaxMemoryProperty.class),
})
public class CPUSensorAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The cpu load property for exposing it to nf automatically. */
	//@NFProp()
	//protected double cpuload;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body() throws Exception
	{
		MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
//		final com.sun.management.OperatingSystemMXBean osb = ManagementFactory.newPlatformMXBeanProxy(mbsc, 
//			ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);
		
		Class<?> cl= Class.forName("com.sun.management.OperatingSystemMXBean");
		final Object osb = ManagementFactory.newPlatformMXBeanProxy(mbsc, 
			ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, cl);
		final Method m = osb.getClass().getMethod("getSystemCpuLoad", new Class[0]);
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				try
				{
	//				System.out.println(osb.getSystemLoadAverage());
	//				double load = osb.getSystemCpuLoad();
					double load = ((Double)m.invoke(osb, new Object[0])).doubleValue();
					CPULoadProperty cp = (CPULoadProperty)agent.getNfProperty(CPULoadProperty.CPULOAD);
					cp.setValue(new Double(load));
					
//					System.out.println(load);
					agent.scheduleStep(this, 5000);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
				return IFuture.DONE;
			}
		};
		
		agent.scheduleStep(step, 5000);
	}
	
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
