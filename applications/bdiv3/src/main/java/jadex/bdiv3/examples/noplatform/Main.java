package jadex.bdiv3.examples.noplatform;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;
import jadex.noplatform.NoPlatformStarter;

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
		Tuple2<IExecutionService, IClockService> tup = NoPlatformStarter.createServices();
		
		// Create factory for loading BDI agent
		BDIAgentFactory afac = new BDIAgentFactory("rootid");
		afac.setFeatures(BDIAgentFactory.NOPLATFORM_DEFAULT_FEATURES);
		
		// Create the agent
		NoPlatformStarter.createAgent("jadex.bdiv3.examples.helloworld.HelloWorld.class", afac, tup.getFirstEntity(), tup.getSecondEntity()).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(IExternalAccess result)
			{
				System.out.println("created: "+result);
			}
		});
		
		SUtil.sleep(10000);
		System.out.println("main end");
	}
}
