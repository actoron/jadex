package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.commons.Tuple;

/** Records found waste bins.
 */
public class ChargingStationFoundEventPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		IInternalEvent event = (IInternalEvent) getReason();
		Tuple t = new Tuple(event.getParameter("charging_station_id").getValue(), event.getParameter("position").getValue());
		b.getBeliefSet("charging_stations").addFact(t);
	}
}
