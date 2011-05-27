package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;


/**
 *  Go to the charging station and load the battery.
 */
public class LoadBatteryPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Hack! Should be done with goal..
	    // todo: test if goal state (-> in_process) could be used 
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(true));

		// Move to station.
		IGoal findstation = createGoal("querychargingstation");
//		System.out.println("Findstation start: "+findstation);
		dispatchSubgoalAndWait(findstation);
		ISpaceObject station = (ISpaceObject)findstation.getParameter("result").getValue();
//		System.out.println("Findstation end: "+station);

		if(station!=null)
		{
			IGoal moveto = createGoal("achievemoveto");
			IVector2 location = (IVector2)station.getProperty(Space2D.PROPERTY_POSITION);
			moveto.getParameter("location").setValue(location);
//			System.out.println("Created: "+location+" "+this);
			dispatchSubgoalAndWait(moveto);
//			System.out.println("Reached: "+location+" "+this);

			SyncResultListener	res	= new SyncResultListener();
			Map props = new HashMap();
			props.put(LoadBatteryTask.PROPERTY_TARGET, station);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			Object taskid = space.createObjectTask(LoadBatteryTask.PROPERTY_TYPENAME, props, myself.getId());
			space.addTaskListener(taskid, myself.getId(), res);
			// Its important to wait for the task, as otherwise the plan is immediately finished and the maintaingoal is failed (one plan, no recur) 
			res.waitForResult();
//			load = new LoadBatteryTask(station, res);
//			myself.addTask(load);
		}

		getLogger().info("Loading finished.");
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(false));
	}
}
