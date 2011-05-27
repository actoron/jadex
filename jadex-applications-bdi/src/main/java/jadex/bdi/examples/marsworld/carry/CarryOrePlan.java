package jadex.bdi.examples.marsworld.carry;

import jadex.bdi.examples.marsworld.producer.ProduceOreTask;
import jadex.bdi.examples.marsworld.sentry.AnalyzeTargetTask;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;

import java.util.HashMap;
import java.util.Map;


/**
 *  Inform the sentry agent about a new target.
 */
public class CarryOrePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		ISpaceObject target = (ISpaceObject)getParameter("target").getValue();
		boolean	finished	= false;

		while(!finished)
		{
			IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			
			// Move to the target.
			IGoal go_target = createGoal("move.move_dest");
			go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
			dispatchSubgoalAndWait(go_target);
	
			// Load ore at the target.
			ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
			SyncResultListener	res	= new SyncResultListener();
//			myself.addTask(new LoadOreTask(target, true, res));
			Map props = new HashMap();
			props.put(LoadOreTask.PROPERTY_TARGET, target);
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.TRUE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			Object	taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			env.addTaskListener(taskid, myself.getId(), res);
			
			res.waitForResult();
//			System.out.println("Loaded ore at target: "+getAgentName()+", "+ore+" ore loaded.");
			// Todo: use return value to determine finished state?
			finished	= ((Number)target.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue()==0;
			if(((Number)myself.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue()==0)
				break;
	
			// Move to the homebase.
			ISpaceObject	homebase	= env.getSpaceObjectsByType("homebase")[0];
			IGoal go_home = createGoal("move.move_dest");
			go_home.getParameter("destination").setValue(homebase.getProperty(Space2D.PROPERTY_POSITION));
			dispatchSubgoalAndWait(go_home);
	
			// Unload ore at the homebase.
			res	= new SyncResultListener();
			props = new HashMap();
			props.put(LoadOreTask.PROPERTY_TARGET, homebase);
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.FALSE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			env.addTaskListener(taskid, myself.getId(), res);
//			myself.addTask(new LoadOreTask(homebase, false, res));
			res.waitForResult();
//			System.out.println("Unloaded ore at homebase: "+getAgentName()+", "+ore+" ore unloaded.");
		}
	}
}
