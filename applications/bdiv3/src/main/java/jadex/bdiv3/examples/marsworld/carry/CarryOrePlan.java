package jadex.bdiv3.examples.marsworld.carry;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.carry.CarryBDI.CarryOre;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.Move;
import jadex.bdiv3.examples.marsworld.producer.ProduceOreTask;
import jadex.bdiv3.examples.marsworld.sentry.AnalyzeTargetTask;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;


/**
 *  Inform the sentry agent about a new target.
 */
@Plan
public class CarryOrePlan 
{
	//-------- attributes --------

	@PlanCapability
	protected CarryBDI carry;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected CarryOre goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{	
		ISpaceObject target = goal.getTarget();
		boolean	finished = false;
		MovementCapability capa = carry.getMoveCapa();
		
		while(!finished)
		{
			IEnvironmentSpace env = capa.getEnvironment();
			
			// Move to the target.
			Move move = capa.new Move(target.getProperty(Space2D.PROPERTY_POSITION));
			rplan.dispatchSubgoal(move).get();
	
			// Load ore at the target.
			ISpaceObject	myself	= capa.getMyself();
			
			Future<Void> fut = new Future<Void>();
			DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
//			myself.addTask(new LoadOreTask(target, true, res));
			Map props = new HashMap();
			props.put(LoadOreTask.PROPERTY_TARGET, target);
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.TRUE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
			Object	taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			env.addTaskListener(taskid, myself.getId(), lis);
			fut.get();
			
//			System.out.println("Loaded ore at target: "+getAgentName()+", "+ore+" ore loaded.");
			// Todo: use return value to determine finished state?
			finished = ((Number)target.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue()==0;
			if(((Number)myself.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue()==0)
				break;
	
			// Move to the homebase.
			move = capa.new Move(capa.getHomebasePosition());
			rplan.dispatchSubgoal(move).get();
			
			// Unload ore at the homebase.
			fut = new Future<Void>();
			lis = new DelegationResultListener<Void>(fut, true);
			props = new HashMap();
			props.put(LoadOreTask.PROPERTY_TARGET, capa.getHomebase());
			props.put(LoadOreTask.PROPERTY_LOAD, Boolean.FALSE);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
			taskid	= env.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
			env.addTaskListener(taskid, myself.getId(), lis);
			fut.get();
//			System.out.println("Unloaded ore at homebase: "+getAgentName()+", "+ore+" ore unloaded.");
		}
	}
}
