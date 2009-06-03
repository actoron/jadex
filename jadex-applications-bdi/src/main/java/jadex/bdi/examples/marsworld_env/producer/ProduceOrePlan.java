package jadex.bdi.examples.marsworld_env.producer;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


/**
 *  Inform the sentry agent about a new target.
 */
public class ProduceOrePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProduceOrePlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject target = (ISpaceObject)getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
		dispatchSubgoalAndWait(go_target);

		// Produce ore at the target.
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
		SyncResultListener	res	= new SyncResultListener();
		myself.addTask(new ProduceOreTask(target, res));
		res.waitForResult();
//		Number	ore	= (Number)res.waitForResult();
//		System.out.println("Produced ore at target: "+getAgentName()+", "+ore+" ore produced.");
	}
}
