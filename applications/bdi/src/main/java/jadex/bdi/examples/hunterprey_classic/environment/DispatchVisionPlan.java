package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.RequestVision;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchVisionPlan extends Plan
{
	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("Env: dispatching vision!!!");

		RequestVision rv = (RequestVision)getParameter("action").getValue();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getVision(rv.getCreature());
		rv.setVision(v);
		Done done = new Done();
		done.setAction(rv);
		getParameter("result").setValue(done);

		/*IMessageEvent req = (IMessageEvent)getInitialEvent();
		RequestVision rv = (RequestVision)req.getContent();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getVision(rv.getCreature());
		rv.setVision(v);
		Done done = new Done();
		done.setAction(rv);
		sendMessage(req.createReply("inform_done", done));*/
	}
}
