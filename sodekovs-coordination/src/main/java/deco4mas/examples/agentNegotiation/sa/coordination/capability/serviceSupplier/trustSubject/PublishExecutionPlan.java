package deco4mas.examples.agentNegotiation.sa.coordination.capability.serviceSupplier.trustSubject;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.common.dataObjects.ExecutedService;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustEvent;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustExecutionInformation;

/**
 * publish execution
 */
public class PublishExecutionPlan extends Plan
{

	public void body()
	{
		IInternalEvent contractEvent = (IInternalEvent) getReason();
		ExecutedService execution = (ExecutedService) contractEvent.getParameter("execution").getValue();
		if (execution != null)
		{
			IComponentIdentifier id = this.getComponentIdentifier();
			if (execution.getSa().getName().equals(id.getName()))
			{
				TrustExecutionInformation info = null;
				if (execution.isCorrect())
				{
					info = new TrustExecutionInformation(execution.getSa(), execution.getSma(), execution.getServiceType(),
						TrustEvent.SuccessfullRequest, execution.getTime());
				} else
				{
					info = new TrustExecutionInformation(execution.getSa(), execution.getSma(), execution.getServiceType(),
						TrustEvent.FailedRequest, execution.getTime());
				}
				IInternalEvent execOccur = createInternalEvent("executionOccur");
				execOccur.getParameter("information").setValue(info);
				dispatchInternalEvent(execOccur);
			}
		}
	}
}
