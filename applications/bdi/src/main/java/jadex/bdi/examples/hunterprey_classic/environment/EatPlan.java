package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.RequestEat;
import jadex.bdi.examples.hunterprey_classic.TaskInfo;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;

/**
 *  Handle eat requests by the environment.
 */
public class  EatPlan extends Plan
{
	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("a) eat: "+getAgentName());

		RequestEat re = (RequestEat)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

		// Wait until all tasks are processed by the environment.
		//waitForCondition(getCondition("emptylist"));
		waitForCondition("notasks");

//		System.out.println("b) eat: "+getAgentName());

		// Result can null when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(re);
			getParameter("result").setValue(done);
		}
		else
		{
			fail();
		}
	}

	/**
	 *  The plan body.
	 * /
	public void body()
	{
//		System.out.println("a) eat: "+getName());

		IMessageEvent req = (IMessageEvent)getInitialEvent();
		RequestEat re = (RequestEat)req.getContent();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

		// Wait until all tasks are processed by the environment.
		//waitForCondition(getCondition("emptylist"));
		waitForCondition("$beliefbase.environment.getTaskSize()==0");

//		System.out.println("b) eat: "+getName());

		// Result can null when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(re);
			sendMessage(req.createReply("inform_done", done));
		}
		else
		{
			//sendMessage(req.createReply("failure", "Eat action failed."));
			IMessageEvent rep = req.createReply("failure", "Eat action failed.");
			//rep.getParameter("language").setValue("plain-text");
			rep.getParameter("ontology").setValue(null);
			sendMessage(rep);
		}
	}*/
}
