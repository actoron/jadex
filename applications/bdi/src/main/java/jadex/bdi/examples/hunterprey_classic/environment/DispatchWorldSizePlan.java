package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.RequestWorldSize;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;

/**
 *  The dispatch world size plan sends back the size of the world.
 */
public class DispatchWorldSizePlan extends Plan
{
	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		/*IMessageEvent req = (IMessageEvent)getInitialEvent();
		RequestWorldSize rws = (RequestWorldSize)req.getContent();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		rws.setWidth(env.getWidth());
		rws.setHeight(env.getHeight());
		Done done = new Done();
		done.setAction(rws);
		sendMessage(req.createReply("inform_done", done));*/

		RequestWorldSize rws = (RequestWorldSize)getParameter("action").getValue();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		rws.setWidth(env.getWidth());
		rws.setHeight(env.getHeight());
		Done done = new Done();
		done.setAction(rws);
		getParameter("result").setValue(done);
	}
}
