package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;

public class WasteFoundEventPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		IInternalEvent event = (IInternalEvent) getReason();
		b.getBelief("waste_target").setFact(event.getParameter("object_id").getValue());
		b.getBelief("waste_target_position").setFact(event.getParameter("position").getValue());
	}
}
