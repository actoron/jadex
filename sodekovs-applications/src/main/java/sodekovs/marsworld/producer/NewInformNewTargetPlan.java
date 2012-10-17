package sodekovs.marsworld.producer;

import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

/**
 *  Inform the sentry agent about a new target.
 */
public class NewInformNewTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IChangeEvent	reason	= (IChangeEvent)getReason();
		ISpaceObject	target	= (ISpaceObject)reason.getValue();
	
//		System.out.println("PRODUCER INFORMING SENTRY.......");
		
		//to closest sentry
		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);
		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
		ISpaceObject nearestSentry = space.getNearestObject(myPos, null, "sentry");		
		
		IMessageEvent mevent = createMessageEvent("inform_target");
		mevent.getParameterSet(SFipa.RECEIVERS).addValue(space.getOwner(nearestSentry.getId()).getName());
//		mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(space.getOwner(nearestSentry.getId()).getName()));
		mevent.getParameter(SFipa.CONTENT).setValue(target);
		
		
		
		/*****************************************************/		
		//NEW_NEW_NEW_NEW_NEW_NEW
		//Setting parameter for MasDynamics		
		System.out.println("#NewInfTarget# Currently detected target: " + target);
		//putting latest target to belief that is observed for MasDyn
		Object t = target.getId();
		this.getBeliefbase().getBelief("latest_target").setFact(target.getId());
		/*****************************************************/
		
		
		
		//OLD_OLD_OLD_OLD_OLD
		sendMessage(mevent);
		
		
		
		
	}
}
