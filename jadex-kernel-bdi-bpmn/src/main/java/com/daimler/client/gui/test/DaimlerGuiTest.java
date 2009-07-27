package com.daimler.client.gui.test;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.BpmnXMLReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.tools.ExecutionControlPanel;

public class DaimlerGuiTest
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
			String filename = "com/daimler/client/gui/test/DataFetch.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/test_rule.bpmn";
//			String filename = "jadex/bpmn/examples/helloworld/SubProcess.bpmn"
//			String filename = "jadex/bpmn/examples/helloworld/UserInteraction.bpmn";
			
			MBpmnModel model = BpmnXMLReader.read(filename, null, null);
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
