package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.wfms.bdi.ontology.InformActivityAdded;
import jadex.wfms.bdi.ontology.InformActivityRemoved;
import jadex.wfms.bdi.ontology.InformUserActivityAdded;
import jadex.wfms.bdi.ontology.InformUserActivityRemoved;
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
			IGoal wiAddedGoal = createGoal("add_workitem");
			wiAddedGoal.getParameter("workitem").setValue(wiAdded.getWorkitem());
			dispatchSubgoalAndWait(wiAddedGoal);
		}
		else if (update instanceof InformWorkitemRemoved)
		{
			InformWorkitemRemoved wiRemoved = (InformWorkitemRemoved) update;
			IGoal wiRemovedGoal = createGoal("remove_workitem");
			wiRemovedGoal.getParameter("workitem").setValue(wiRemoved.getWorkitem());
			dispatchSubgoalAndWait(wiRemovedGoal);
		}
		else if (update instanceof InformActivityAdded)
		{
			InformActivityAdded acAdded = (InformActivityAdded) update;
			IGoal acAddedGoal = createGoal("add_activity");
			acAddedGoal.getParameter("activity").setValue(acAdded.getActivity());
			dispatchSubgoalAndWait(acAddedGoal);
		}
		else if (update instanceof InformActivityRemoved)
		{
			InformActivityRemoved acRemoved = (InformActivityRemoved) update;
			IGoal acRemovedGoal = createGoal("remove_activity");
			acRemovedGoal.getParameter("activity").setValue(acRemoved.getActivity());
			dispatchSubgoalAndWait(acRemovedGoal);
		}
		else if (update instanceof InformUserActivityAdded)
		{
			InformUserActivityAdded uacAdded = (InformUserActivityAdded) update;
			IGoal uacAddedGoal = createGoal("add_user_activity");
			uacAddedGoal.getParameter("user_name").setValue(uacAdded.getUserName());
			uacAddedGoal.getParameter("activity").setValue(uacAdded.getActivity());
			dispatchSubgoalAndWait(uacAddedGoal);
		}
		else if (update instanceof InformUserActivityRemoved)
		{
			InformUserActivityRemoved uacAdded = (InformUserActivityRemoved) update;
			IGoal uacRemovedGoal = createGoal("remove_user_activity");
			uacRemovedGoal.getParameter("user_name").setValue(uacAdded.getUserName());
			uacRemovedGoal.getParameter("activity").setValue(uacAdded.getActivity());
			dispatchSubgoalAndWait(uacRemovedGoal);
		}
	}
}
