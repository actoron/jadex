package jadex.bdi.examples.marsworld.carry;

import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;
import jadex.extension.envsupport.EnvironmentService;
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
		IChangeEvent	reason	= (IChangeEvent)getReason();
		ISpaceObject	target	= (ISpaceObject)reason.getValue();
		
		// Todo: multiple spaces by name...
		AGRSpace agrs = (AGRSpace)EnvironmentService.getSpace(getInterpreter()).get();
//			((IExternalAccess)getScope().getParentAccess()).getExtension("myagrspace").get();
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
