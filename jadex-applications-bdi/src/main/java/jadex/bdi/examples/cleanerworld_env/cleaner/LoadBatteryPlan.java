package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.adapter.base.envsupport.environment.IObjectTask;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


/**
 *  Go to the charging station and load the battery.
 */
public class LoadBatteryPlan extends Plan
{
	//-------- attributes --------

	/** The load task. */
	protected IObjectTask load;

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
			load = new LoadBatteryTask(station, res);
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			myself.addTask(load);
		}

//		getLogger().info("Loading finished.");
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(false));
	}

	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void aborted()
	{
		if(load!=null)
		{
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			myself.removeTask(load);
		}
	}
	
	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void failed()
	{
		if(load!=null)
		{
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			myself.removeTask(load);
		}
	}
}
