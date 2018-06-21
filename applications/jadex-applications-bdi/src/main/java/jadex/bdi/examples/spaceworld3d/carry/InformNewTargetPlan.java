package jadex.bdi.examples.spaceworld3d.carry;

import jadex.application.EnvironmentService;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.rules.eca.ChangeInfo;

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
		ISpaceObject	target	= ((ChangeEvent<ChangeInfo<ISpaceObject>>)getReason()).getValue().getValue();
		
		// Todo: multiple spaces by name...
		AGRSpace agrs = (AGRSpace)EnvironmentService.getSpace(getAgent(), "myagrspace").get();
		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	sentries	= group.getAgentsForRole("sentry");
		
		if(sentries!=null)
		{
			IMessageEvent mevent = createMessageEvent("inform_target");
			mevent.getParameterSet(SFipa.RECEIVERS).addValues(sentries);
			mevent.getParameter(SFipa.CONTENT).setValue(target);
			sendMessage(mevent);
		}

//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}
}
