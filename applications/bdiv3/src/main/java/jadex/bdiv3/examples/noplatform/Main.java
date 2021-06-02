package jadex.bdiv3.examples.noplatform;

import jadex.base.Starter;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.Tuple2;

/**
 *  Shows how a BDI agent can be executed without platform.
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
		
		// Create factory for loading BDI agent
		BDIAgentFactory afac = new BDIAgentFactory("rootid");
		afac.setFeatures(BDIAgentFactory.NOPLATFORM_DEFAULT_FEATURES);
		
		// "jadex.bdiv3.examples.helloworld.HelloWorld.class"
		IExternalAccess agent = Starter.createAgent("jadex.bdiv3.examples.puzzle.SokratesAgent.class", afac, tup.getFirstEntity(), tup.getSecondEntity()).get();
		agent.waitForTermination().get();
		
		//SUtil.sleep(10000);
		System.out.println("main end");
	}
}

//NoPlatformStarter.createAgent("jadex.bdiv3.examples.puzzle.SokratesAgent.class", afac, tup.getFirstEntity(), tup.getSecondEntity()).addResultListener(new IResultListener<IExternalAccess>()
//{
//	public void exceptionOccurred(Exception exception)
//	{
//		exception.printStackTrace();
//	}
//	
//	public void resultAvailable(IExternalAccess result)
//	{
//		System.out.println("created: "+result);
//	}
//});
