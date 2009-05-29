package jadex.bdi.examples.marsworld_env.producer;

import jadex.adapter.base.agr.AGRSpace;
import jadex.adapter.base.agr.Group;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

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
		
		AGRSpace agrs = (AGRSpace)getScope().getApplicationContext().getSpace("myagrspace");
		Group group = agrs.getGroup("mymarsteam");
		IAgentIdentifier[]	sentries	= group.getAgentsForRole("sentry");
		
		IMessageEvent mevent = createMessageEvent("inform_target");
		for(int i=0; i<sentries.length; i++)
			mevent.getParameterSet(SFipa.RECEIVERS).addValue(sentries[i]);
		mevent.getParameter(SFipa.CONTENT).setValue(target);
		sendMessage(mevent);

//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}
}
