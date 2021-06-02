package jadex.micro.examples.noplatform;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.Tuple2;
import jadex.micro.MicroAgentFactory;

/**
 *  Shows how a micro agent can be executed without platform.
 */
public class Main
{
	/**
	 *  Main for running a BDI agent without platform.
	 */
	public static void main(String[] args)
	{
		// Create necessary platform services (without platform)
		Tuple2<IExecutionService, IClockService> tup = Starter.createServices();
		
		// Create factory for loading micro agent
		MicroAgentFactory afac = new MicroAgentFactory("rootid");
		afac.setFeatures(MicroAgentFactory.NOPLATFORM_DEFAULT_FEATURES);
		
		IExternalAccess agent = Starter.createAgent("jadex.micro.examples.helloworld.PojoHelloWorldAgent.class", afac, tup.getFirstEntity(), tup.getSecondEntity()).get();
		agent.waitForTermination().get();
		
		//SUtil.sleep(10000);
		System.out.println("main end");
	}
}

