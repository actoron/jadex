package jadex.bpmn.examples.helloworld;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.BpmnXMLReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.tools.ExecutionControlPanel;

public class Test
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			// Load model.
			
//			String filename = "jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/test_parallel.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/test2.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/all_activities.bpmn"
//			String filename = "jadex/bpmn/examples/helloworld/XOR.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/Exception.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/test_rule.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/SubProcess.bpmn"
			String filename = "jadex/bpmn/examples/helloworld/UserInteraction.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/ForLoop.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/Scopes.bpmn";
			
			MBpmnModel model = new BpmnModelLoader().loadBpmnModel(filename, null);
			BpmnInstance instance = new BpmnInstance(model);
			BpmnExecutor exe = new BpmnExecutor(instance, true);
			ExecutionControlPanel.createBpmnFrame("test", instance, exe);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
