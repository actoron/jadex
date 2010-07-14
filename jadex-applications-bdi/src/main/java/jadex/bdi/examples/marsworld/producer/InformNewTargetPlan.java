package jadex.bdi.examples.marsworld.producer;

import jadex.base.fipa.SFipa;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.agr.AGRSpace;
import jadex.application.space.agr.Group;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

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
		IChangeEvent	reason	= (IChangeEvent)getReason();
		ISpaceObject	target	= (ISpaceObject)reason.getValue();
		
		AGRSpace agrs = (AGRSpace)((IApplicationExternalAccess)getScope().getParent()).getSpace("myagrspace");
		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	sentries	= group.getAgentsForRole("sentry");
		
		IMessageEvent mevent = createMessageEvent("inform_target");
		mevent.getParameterSet(SFipa.RECEIVERS).addValues(sentries);
		mevent.getParameter(SFipa.CONTENT).setValue(target);
		sendMessage(mevent);

//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}
}
