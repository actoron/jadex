package jadex.simulation.examples.marsworld.producer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.simulation.examples.marsworld.RequestCarry;
import jadex.simulation.examples.marsworld.RequestProduction;

/**
 *  The main plan for the Producer Agent. <br>
 *  first the Agent waits for an incoming request.
 *  It can be called to move home or to a given location.
 *  Being called to a location it will dispatch a subgoal to produce
 *  the ore there look up available carry agents and call one to collect it.
 */
public class ProducerPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProducerPlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  Method body.
	 */
	public void body()
	{
		while(true)
		{			
			// Wait for a request.
			IMessageEvent req = waitForMessageEvent("request_production");

			ISpaceObject ot = ((RequestProduction)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
			IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			ISpaceObject target = env.getSpaceObject(ot.getId());

			//Call Carry agent before. Does it save time?
			//Confer WalkingStrategyEnum for Mapping of int values to semantics.
//			int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			int walkingStrategyProperty = (Integer) ((IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			if (walkingStrategyProperty == 2) {
				callCarryAgent(target);
			}
			
			// Producing ore here.
			IGoal produce_ore = createGoal("produce_ore");
			produce_ore.getParameter("target").setValue(target);
			dispatchSubgoalAndWait(produce_ore);

			//System.out.println("Production of ore has finished....");
			//System.out.println("Calling Carry Agent....");
			callCarryAgent(target);
		}
	}

	/**
	 *  Call carry agents to location.
	 *  @param target	The target to call carries to.
	 */
	protected void callCarryAgent(ISpaceObject target)
	{
		AGRSpace agrs = (AGRSpace)((IExternalAccess)getScope().getParentAccess()).getExtension("myagrspace").get(this);		
		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	carriers	= group.getAgentsForRole("carrier");
		
		if(carriers!=null && carriers.length>0)
		{
//			System.out.println("Carry Agent: Found Carry Agents: "+carriers.length);

			RequestCarry rc = new RequestCarry();
			rc.setTarget(target);
			IMessageEvent mevent = createMessageEvent("request_carries");
			//Get closest carrier agent ?
			//Confer WalkingStrategyEnum for Mapping of int values to semantics.
//			int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			int walkingStrategyProperty = (Integer) ((IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
			if (walkingStrategyProperty == 2) {
//				mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(getClosestCarrierAgent()));
				mevent.getParameterSet(SFipa.RECEIVERS).addValue(getClosestCarrierAgent());
			}else{
				mevent.getParameterSet(SFipa.RECEIVERS).addValues(carriers);	
			}		
			mevent.getParameter(SFipa.CONTENT).setValue(rc);
			sendMessage(mevent);
		}
	}
	
	private IComponentIdentifier getClosestCarrierAgent() {
		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);
		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
		ISpaceObject nearestCarrier = space.getNearestObject(myPos, null, "carry");
		IComponentIdentifier[] ret =  new IComponentIdentifier[1];
//		ret[0] = space.getOwner(nearestCarrier.getId()).getName();		
//		return ret;
		return  space.getOwner(nearestCarrier.getId()).getName();				
	}
}
