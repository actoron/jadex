package jadex.bdi.examples.marsworld_env.carry;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bdi.examples.marsworld_env.producer.ProduceOreTask;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


/**
 *  Inform the sentry agent about a new target.
 */
public class CarryOrePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public CarryOrePlan()
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
		boolean	finished	= false;

		while(!finished)
		{
			// Move to the target.
			IGoal go_target = createGoal("move.move_dest");
			go_target.getParameter("destination").setValue(target.getProperty(Space2D.POSITION));
			dispatchSubgoalAndWait(go_target);
	
			// Load ore at the target.
			ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
			SyncResultListener	res	= new SyncResultListener();
			myself.addTask(new LoadOreTask(target, true, res));
			Number	ore	= (Number)res.waitForResult();
			System.out.println("Loaded ore at target: "+getAgentName()+", "+ore+" ore loaded.");
			// Todo: use return value to determine finished state?
			finished	= ((Number)target.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue()==0;
			if(ore.intValue()==0)
				break;
	
			// Move to the homebase.
			IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			ISpaceObject	homebase	= env.getSpaceObjectsByType("homebase")[0];
			IGoal go_home = createGoal("move.move_dest");
			go_home.getParameter("destination").setValue(homebase.getProperty(Space2D.POSITION));
			dispatchSubgoalAndWait(go_home);
	
			// Unload ore at the homebase.
			res	= new SyncResultListener();
			myself.addTask(new LoadOreTask(homebase, false, res));
			res.waitForResult();
			System.out.println("Unloaded ore at homebase: "+getAgentName()+", "+ore+" ore unloaded.");
		}
	}
}
