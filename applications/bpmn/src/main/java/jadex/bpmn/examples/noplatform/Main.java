package jadex.bpmn.examples.noplatform;

import jadex.base.Starter;
import jadex.bpmn.BpmnFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.Tuple2;

/**
 *  Shows how a BPMN agent can be executed without platform.
 */
public class Main
{
	/**
	 *  Main for running a BPMN agent without platform.
	 */
	public static void main(String[] args)
	{
		// Create necessary platform services (without platform)
		Tuple2<IExecutionService, IClockService> tup = Starter.createServices();
		
		// Create factory for loading BPMN agent
		BpmnFactory afac = new BpmnFactory("rootid");
		afac.setFeatures(BpmnFactory.NOPLATFORM_DEFAULT_FEATURES);
		
		// "jadex.bdiv3.examples.helloworld.HelloWorld.class"
		IExternalAccess agent = Starter.createAgent("jadex.bpmn.examples.helloworld.HelloWorld.bpmn2", afac, tup.getFirstEntity(), tup.getSecondEntity()).get();
		agent.waitForTermination().get();
		
		//SUtil.sleep(10000);
		System.out.println("main end");
	}
}

