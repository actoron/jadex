package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;

public class DestinationReachedEventPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		// TODO: split
		if (b.getBelief("waste_target").getFact() != null)
		{
			IInternalEvent evt = createInternalEvent("reached_waste_event");
			dispatchInternalEvent(evt);
		}
		else
		{
			getBeliefbase().getBelief("waste_search_waypoint").setFact(null);
		}
	}
}
