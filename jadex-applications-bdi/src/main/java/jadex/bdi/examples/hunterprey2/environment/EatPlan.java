package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.hunterprey2.Environment;
import jadex.bdi.examples.hunterprey2.RequestEat;
import jadex.bdi.examples.hunterprey2.TaskInfo;
import jadex.bdi.runtime.Plan;

/**
 *  Handle eat requests by the environment.
 */
public class  EatPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  EatPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("a) eat: "+getName());

		RequestEat re = (RequestEat)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

		// Wait until all tasks are processed by the environment.
		//waitForCondition(getCondition("emptylist"));
		waitForCondition("notasks");

//		System.out.println("b) eat: "+getName());

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
