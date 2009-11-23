package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;

/**
 *  Task for destroying a component.
 */
public class DestroyComponentTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		IComponentExecutionService ces = (IComponentExecutionService)instance.getComponentAdapter().getServiceContainer().getService(IComponentExecutionService.class);
		IComponentIdentifier cid = (IComponentIdentifier)context.getParameterValue("componentid");
		if(cid==null)
		{
			String name = (String)context.getParameterValue("name");
			cid = ces.createComponentIdentifier(name, true, null);
		}
		
//		System.out.println("Destroy component: "+cid.getName());
		ces.destroyComponent(cid, null);
	}
}
