package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;


/**
 *  Go to the charging station and load the battery.
 */
public class LoadBatteryPlan extends Plan
{
	//-------- attributes --------

	/** The load task. */
	protected Object taskid;

	//-------- methods --------

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
			props.put(LoadBatteryTask.PROPERTY_LISTENER, res);
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			taskid = space.createObjectTask(LoadBatteryTask.PROPERTY_TYPENAME, props, myself.getId());
//			load = new LoadBatteryTask(station, res);
//			myself.addTask(load);
		}

//		getLogger().info("Loading finished.");
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(false));
	}

	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void aborted()
	{
		if(taskid!=null)
		{
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
			space.removeObjectTask(taskid, myself.getId());
		}
	}
	
	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void failed()
	{
		if(taskid!=null)
		{
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
			space.removeObjectTask(taskid, myself.getId());
		}
	}
}
