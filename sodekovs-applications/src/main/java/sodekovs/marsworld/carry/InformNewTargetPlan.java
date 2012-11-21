package sodekovs.marsworld.carry;


import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Inform the sentry agent about a new target.
 */
public class InformNewTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		IChangeEvent	reason	= (IChangeEvent)getReason();
//		ISpaceObject	target	= (ISpaceObject)reason.getValue();
					
		//to closest sentry
//		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);
//		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
//		ISpaceObject nearestSentry = space.getNearestObject(myPos, null, "sentry");
//
//		if(space.getOwner(nearestSentry.getId()).getName()!=null)
//		{
//			IMessageEvent mevent = createMessageEvent("inform_target");
//			mevent.getParameterSet(SFipa.RECEIVERS).addValue(space.getOwner(nearestSentry.getId()).getName());
////			mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(space.getOwner(nearestSentry.getId()).getName()));
//			mevent.getParameter(SFipa.CONTENT).setValue(target);
////			System.out.println("#Carry.NewInfTarget#");			
////			sendMessage(mevent);
//		}
		
		/*****************************************************/		
		//NEW_NEW_NEW_NEW_NEW_NEW
		//HACK: is not send to closest....
		//Setting parameter for MasDynamics		
		Object[] myTargets = getBeliefbase().getBeliefSet("move.my_targets").getFacts();
		ISpaceObject latestTarget = (ISpaceObject) myTargets[myTargets.length-1];
		System.out.println("#NewInfTarget-Carry# Currently detected target: " + latestTarget);
		
		IInternalEvent ievent = createInternalEvent("callSentryEvent");
		ievent.getParameter("latest_target").setValue(latestTarget);		
		dispatchInternalEvent(ievent);
		
		//putting latest target to belief that is observed for MasDyn			
//		this.getBeliefbase().getBeliefSet("latest_target").addFact(latestTarget);
		/*****************************************************/

//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}

}
