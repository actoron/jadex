package sodekovs.marsworld.producer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * The main plan for the Producer Agent. <br>
 * first the Agent waits for an incoming request. It can be called to move home or to a given location. Being called to a location it will dispatch a subgoal to produce the ore there look up available
 * carry agents and call one to collect it.
 */
public class ProducerPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public ProducerPlan() {
		getLogger().info("Created: " + this);
	}

	// -------- methods --------

	/**
	 * Method body.
	 */
	public void body() {
		while (true) {
			// Wait for a request.
			// IMessageEvent req = waitForMessageEvent("request_production");

			// // Wait for a request, i.e. corresponding belief is changed
			// waitForFactChanged("latest_analyzed_target");
			//
			//
			// ISpaceObject ot = ((RequestProduction)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
			// IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			// ISpaceObject target = env.getSpaceObject(ot.getId());
			//
			// //Call Carry agent before. Does it save time?
			// //Confer WalkingStrategyEnum for Mapping of int values to semantics.
			// // int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			// int walkingStrategyProperty = (Integer) ((IEnvironmentSpace)
			// getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			// if (walkingStrategyProperty == 2) {
			// callCarryAgent(target);
			// }
			//
			// // Producing ore here.
			// IGoal produce_ore = createGoal("produce_ore");
			// produce_ore.getParameter("target").setValue(target);
			// dispatchSubgoalAndWait(produce_ore);
			//
			// //System.out.println("Production of ore has finished....");
			// //System.out.println("Calling Carry Agent....");
			// callCarryAgent(target);
			
			
			
			// Wait for a request, i.e. corresponding belief is changed
//			waitForFactAdded("latest_analyzed_target");
//			ISpaceObject[] targets = (ISpaceObject[]) getBeliefbase().getBeliefSet("latest_analyzed_target").getFacts();
//			ISpaceObject latestTarget = targets[targets.length-1];
//			System.out.println("#ProcuderPlan# Received latest analyzed target:  " + latestTarget);
			
			//Waiting for internal event, which is dispatched after MASDynamics has transmitted the latest_analyzed_target (from the sentry)
			IInternalEvent event = waitForInternalEvent("latestAnalyzedTargetEvent");
			CoordinationSpaceData data = (CoordinationSpaceData) event.getParameter("latest_analyzed_target").getValue();
			System.out.println("#ProducerPlan# Received latest analyzed target:  " + data);

//			// Call Carry agent before. Does it save time?
//			// Confer WalkingStrategyEnum for Mapping of int values to semantics.
//			// int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			int walkingStrategyProperty = (Integer) ((IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			if (walkingStrategyProperty == 2) {
//				callCarryAgent(target);
//			}
//			target.
			
			// Producing ore here.
			ContinuousSpace2D env = (ContinuousSpace2D)getBeliefbase().getBelief("move.environment").getFact();
			IVector2 position = new Vector2Double(data.getX(), data.getY());
			ISpaceObject latestTarget = env.getNearestObject(position, null, "target");
			
			IGoal produce_ore = createGoal("produce_ore");
			produce_ore.getParameter("target").setValue(latestTarget);
			dispatchSubgoalAndWait(produce_ore);

			// System.out.println("Production of ore has finished....");
			// System.out.println("Calling Carry Agent....");
//			callCarryAgent(target);
			
//			getBeliefbase().getBeliefSet("latest_produced_target").addFact(latestTarget);
			
			IInternalEvent ievent = createInternalEvent("callCarryEvent");
			ievent.getParameter("latest_produced_target").setValue(data);		
			dispatchInternalEvent(ievent);

		}
	}

//	/**
//	 * Call carry agents to location.
//	 * 
//	 * @param target
//	 *            The target to call carries to.
//	 */
//	protected void callCarryAgent(ISpaceObject target) {
//		AGRSpace agrs = (AGRSpace) ((IExternalAccess) getScope().getParentAccess()).getExtension("myagrspace").get(this);
//		Group group = agrs.getGroup("mymarsteam");
//		IComponentIdentifier[] carriers = group.getAgentsForRole("carrier");
//
//		if (carriers != null && carriers.length > 0) {
//			// System.out.println("Carry Agent: Found Carry Agents: "+carriers.length);
//
//			RequestCarry rc = new RequestCarry();
//			rc.setTarget(target);
//			IMessageEvent mevent = createMessageEvent("request_carries");
//			// Get closest carrier agent ?
//			// Confer WalkingStrategyEnum for Mapping of int values to semantics.
//			// int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			int walkingStrategyProperty = (Integer) ((IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			if (walkingStrategyProperty == 2) {
//				// mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(getClosestCarrierAgent()));
//				mevent.getParameterSet(SFipa.RECEIVERS).addValue(getClosestCarrierAgent());
//			} else {
//				mevent.getParameterSet(SFipa.RECEIVERS).addValues(carriers);
//			}
//			mevent.getParameter(SFipa.CONTENT).setValue(rc);
//			sendMessage(mevent);
//		}
//	}
//
//	private IComponentIdentifier getClosestCarrierAgent() {
//		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);
//		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
//		ISpaceObject nearestCarrier = space.getNearestObject(myPos, null, "carry");
//		IComponentIdentifier[] ret = new IComponentIdentifier[1];
//		// ret[0] = space.getOwner(nearestCarrier.getId()).getName();
//		// return ret;
//		return space.getOwner(nearestCarrier.getId()).getName();
//	}
}
