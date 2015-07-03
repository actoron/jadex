package jadex.bdi.examples.cleanerworld_classic.environment;

import jadex.bdi.examples.cleanerworld_classic.Environment;
import jadex.bdi.examples.cleanerworld_classic.RequestCompleteVision;
import jadex.bdi.examples.cleanerworld_classic.Vision;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchCompleteVisionPlan extends Plan
{
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
