package sodekovs.marsworld.sentry;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Inform the sentry agent about a new target.
 */
public class AnalyzeTargetPlan extends Plan {
	/**
	 * The plan body.
	 */
	public void body() {
		ISpaceObject target = (ISpaceObject) getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
		dispatchSubgoalAndWait(go_target);

		// Analyse the target.
		try {
			ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
			SyncResultListener res = new SyncResultListener();
			Map props = new HashMap();
			props.put(AnalyzeTargetTask.PROPERTY_TARGET, target);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			IEnvironmentSpace space = (IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact();
			Object taskid = space.createObjectTask(AnalyzeTargetTask.PROPERTY_TYPENAME, props, myself.getId());
			space.addTaskListener(taskid, myself.getId(), res);

			res.waitForResult();
			// System.out.println("Analyzed target: "+getAgentName()+", "+ore+" ore found.");w
			if (((Number) target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue() > 0)
				callProducerAgent(target);
						
		} catch (Exception e) {
			e.printStackTrace();
			// Fails for one agent, when two agents try to analyze the same target at once.
		}
	}

	/**
	 * Sending a location to the Producer Agent. Therefore it has first to be looked up in the DF.
	 * 
	 * @param target
	 */
	private void callProducerAgent(ISpaceObject target) {
		// System.out.println("Calling some Production Agent...");
		
//		if (producers != null && producers.length > 0) {
//			int sel = (int) (Math.random() * producers.length); // todo: Select not randomly
//			// System.out.println("Found agents: "+producers.length+" selected: "+sel);
//
//			RequestProduction rp = new RequestProduction(target);
//			IMessageEvent mevent = createMessageEvent("request_producer");
//			//send to closest producer?
//			//Confer WalkingStrategyEnum for Mapping of int values to semantics.
////			int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			int walkingStrategyProperty = (Integer) ((IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//			if (walkingStrategyProperty == 2) {
////				mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(getClosestProducerAgent()));				
//				mevent.getParameterSet(SFipa.RECEIVERS).addValue(getClosestProducerAgent());
//			}else{
//				mevent.getParameterSet(SFipa.RECEIVERS).addValue(producers[sel]);				
//			}
//
//			mevent.getParameter(SFipa.CONTENT).setValue(rp);
////			System.out.println("#Sentr.AnaTargPlan#");
//			sendMessage(mevent);
			// System.out.println("Sentry Agent: sent location to: "+producers[sel].getName());
//		}
		
		/*****************************************************/		
		//NEW_NEW_NEW_NEW_NEW_NEW
		//HACK: is not send to closest....
		//Setting parameter for MasDynamics		
		System.out.println("#AnalyzeTargetPlan-Sentry# Latest analyzed target: " + target);
		//putting latest target to belief that is observed for MasDyn			
//		this.getBeliefbase().getBeliefSet("latest_analyzed_target").addFact(target);
		/*****************************************************/
		
//		IMPlan mplan = ((IMPlanbase)getPlanbase().getModelElement()).getPlan("callProducerAgentPlan");		
//		IPlan plan = getPlanbase().createPlan(mplan);
//		plan.getParameter("latest_analyzed_target").setValue(target);
//		plan.startPlan(); 
		
		IVector2 position = (IVector2) target.getProperty("position");
		CoordinationSpaceData data = new CoordinationSpaceData(position.getXAsDouble(), position.getYAsDouble());
		
		IInternalEvent ievent = createInternalEvent("callProducerEvent");
		ievent.getParameter("latest_analyzed_target").setValue(data);		
		dispatchInternalEvent(ievent);
	}

//	private IComponentIdentifier getClosestProducerAgent() {
//		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);		
//		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
//		ISpaceObject nearestProducer = space.getNearestObject(myPos, null, "producer");
//
//		return space.getOwner(nearestProducer.getId()).getName();
//
//	}
}
