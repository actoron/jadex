package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.InformWorkitemAdded;
import jadex.wfms.bdi.ontology.InformWorkitemRemoved;

public class SubscriptionUpdatePlan extends Plan
{
	public void body()
	{
		Object update = getParameter("update").getValue();
		
		if (update instanceof InformWorkitemAdded)
		{
			InformWorkitemAdded wiAdded = (InformWorkitemAdded) update;
			IInternalEvent wiAddEvt = createInternalEvent("workitem_added");
			wiAddEvt.getParameter("workitem").setValue(wiAdded.getWorkitem());
			dispatchInternalEvent(wiAddEvt);
		}
		else if (update instanceof InformWorkitemRemoved)
		{
			InformWorkitemRemoved wiRemoved = (InformWorkitemRemoved) update;
			IInternalEvent wiRemEvt = createInternalEvent("workitem_removed");
			wiRemEvt.getParameter("workitem").setValue(wiRemoved.getWorkitem());
			dispatchInternalEvent(wiRemEvt);
		}
	}
}
