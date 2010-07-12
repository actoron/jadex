package deco4mas.examples.agentNegotiation.sa.coordination.capability.trustSubject;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Contract;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Execution;

/**
 * publish execution
 */
public class PublishExecutionPlan extends Plan
{

	public void body()
	{
//		IInternalEvent contractEvent = (IInternalEvent) getReason();
//		Contract contract = (Contract) contractEvent.getParameter("contract").getValue();
//		Execution execution = contract.getExecution();
//		if (execution != null)
//		{
//			IComponentIdentifier id = this.getComponentIdentifier();
//			if (execution.getSa().equals(id) && contract.isModified())
//			{
//				contract.setModified();
//				IInternalEvent execOccur = createInternalEvent("executionOccur");
//				execOccur.getParameter("task").setValue("executionOccur");
//				execOccur.getParameter("execution").setValue(execution);
//				dispatchInternalEvent(execOccur);
//			}
//		}
	}
}
