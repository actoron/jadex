package jadex.platform.service.sensor;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.commons.SNonAndroid;
import jadex.commons.SNonAndroid.JadexOSBean;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
@NFProperties(@NFProperty(name="cpuload", type=CPULoadProperty.class))
public class CPUSensorAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The cpu load property for exposing it to nf automatically. */
	//@NFProp()
	//protected double cpuload;
	
	/**
	 * 
	 */
	@AgentBody
	public void body() throws Exception
	{
		final JadexOSBean osb = SNonAndroid.getOSBean();
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println(osb.getSystemLoadAverage());
				double load = osb.getSystemCpuLoad();
				CPULoadProperty cp = (CPULoadProperty)agent.getNfProperty("cpuload");
				cp.setLoad(load);
				
				System.out.println(load);
				agent.scheduleStep(this, 5000);
				return IFuture.DONE;
			}
		};
		
		agent.scheduleStep(step, 5000);
	}
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		System.out.println("cpu usage: "+getCPUUsage());
//	}
}
