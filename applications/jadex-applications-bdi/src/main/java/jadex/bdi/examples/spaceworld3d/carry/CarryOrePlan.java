package jadex.bdi.examples.spaceworld3d.carry;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.spaceworld3d.producer.ProduceOreTask;
import jadex.bdi.examples.spaceworld3d.sentry.AnalyzeTargetTask;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space3d.Space3D;


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
			go_target.getParameter("destination").setValue(target.getProperty(Space3D.PROPERTY_POSITION));
			dispatchSubgoalAndWait(go_target);
	
			// Load ore at the target.
			ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
//			myself.addTask(new LoadOreTask(target, true, res));
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(LoadOreTask.PROPERTY_TARGET, target);
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.TRUE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			Object	taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			Future<Void> fut = new Future<Void>();
			env.addTaskListener(taskid, myself.getId(), new DelegationResultListener<Void>(fut));
			fut.get();
			
//			System.out.println("Loaded ore at target: "+getAgentName()+", "+ore+" ore loaded.");
			// Todo: use return value to determine finished state?
			finished	= ((Number)target.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue()==0;
			if(((Number)myself.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue()==0)
				break;
	
			// Move to the homebase.
			ISpaceObject	homebase	= env.getSpaceObjectsByType("homebase")[0];
			IGoal go_home = createGoal("move.move_dest");
			go_home.getParameter("destination").setValue(homebase.getProperty(Space3D.PROPERTY_POSITION));
			dispatchSubgoalAndWait(go_home);
	
			// Unload ore at the homebase.
			props = new HashMap<String, Object>();
			props.put(LoadOreTask.PROPERTY_TARGET, homebase);
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.FALSE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			env.addTaskListener(taskid, myself.getId(), new DelegationResultListener<Void>(fut));
			fut.get();
//			myself.addTask(new LoadOreTask(homebase, false, res));
//			System.out.println("Unloaded ore at homebase: "+getAgentName()+", "+ore+" ore unloaded.");
		}
	}
}
