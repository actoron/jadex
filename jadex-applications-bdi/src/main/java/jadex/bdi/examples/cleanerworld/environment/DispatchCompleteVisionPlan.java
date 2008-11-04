package jadex.bdi.examples.cleanerworld.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.cleanerworld.Environment;
import jadex.bdi.examples.cleanerworld.RequestCompleteVision;
import jadex.bdi.examples.cleanerworld.Vision;
import jadex.bdi.runtime.Plan;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchCompleteVisionPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DispatchCompleteVisionPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getCompleteVision();

		RequestCompleteVision rcv = (RequestCompleteVision)getParameter("action").getValue();
		rcv.setVision(v);
		Done done = new Done();
		done.setAction(rcv);
		getParameter("result").setValue(done);
	}

}
