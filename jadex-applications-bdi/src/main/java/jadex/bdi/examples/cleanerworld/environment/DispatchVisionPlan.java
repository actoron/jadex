package jadex.bdi.examples.cleanerworld.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.cleanerworld.Cleaner;
import jadex.bdi.examples.cleanerworld.Environment;
import jadex.bdi.examples.cleanerworld.RequestVision;
import jadex.bdi.examples.cleanerworld.Vision;
import jadex.bdi.runtime.Plan;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchVisionPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DispatchVisionPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		RequestVision rv = (RequestVision)getParameter("action").getValue();
		Cleaner cl = rv.getCleaner();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getVision(cl);

		rv.setVision(v);
		Done done = new Done();
		done.setAction(rv);
		getParameter("result").setValue(done);
	}
}
