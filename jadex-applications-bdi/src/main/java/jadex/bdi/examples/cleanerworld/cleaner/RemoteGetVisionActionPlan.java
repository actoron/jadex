package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.cleanerworld.Cleaner;
import jadex.bdi.examples.cleanerworld.Location;
import jadex.bdi.examples.cleanerworld.RequestVision;
import jadex.bdi.examples.cleanerworld.Vision;
import jadex.bdi.examples.cleanerworld.Waste;
import jadex.bdi.runtime.IGoal;


/**
 *  Get the vision.
 */
public class RemoteGetVisionActionPlan extends RemoteActionPlan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Cleaner cl = new Cleaner((Location)getBeliefbase().getBelief("my_location").getFact(), getAgentName(),
			(Waste)getBeliefbase().getBelief("carriedwaste").getFact(),
			((Number)getBeliefbase().getBelief("my_vision").getFact()).doubleValue(),
			((Number)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue());

		RequestVision rv = new RequestVision();
		rv.setCleaner(cl);
		IGoal	result	= requestAction(rv);

		Vision vision = ((RequestVision)(((Done)result.getParameter("result").getValue()).getAction())).getVision();
		getParameter("vision").setValue(vision);
	}
}
