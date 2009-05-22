package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Plan to be executed if the ant is under the influence of gravitation.
 */
public class GravitationPlan extends Plan
{
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		Vector2Int target = (Vector2Int) getParameter("gravitation_center").getValue();
		boolean hasGravitation = ((Boolean) getBeliefbase().getBelief("hasGravitation").getFact()).booleanValue();
		System.out.println("ANT IS UNDER GRAVITATION CONTROL!!!!!!!!!!! --> GravitationCenter: " + ((Vector2Int) target).toString());
		
		//increase absorption count of gravitation center
		Map params = new HashMap();
//		params.put(IAgentAction.OBJECT_ID, getExternalAccess().getAgentIdentifier().getLocalName());
		params.put(Space2D.POSITION, target);		
		SyncResultListener srl = new SyncResultListener();
		env.performSpaceAction("absorbObject", params, srl); 
		srl.waitForResult();

		//remove the destination sign of the agent
		params = new HashMap();
		params.put(ISpaceAction.OBJECT_ID, env.getOwnedObjects(getAgentIdentifier())[0].getId());
		params.put(UpdateDestinationAction.DESTINATION, target);		
		params.put(GravitationListener.FEELS_GRAVITATION,hasGravitation);
		srl = new SyncResultListener();
		env.performSpaceAction("updateDestination", params, srl); 
		srl.waitForResult();
		
		//remove agent from space
		env.destroySpaceObject(myself.getId());
		
		//remove ant agent from platform 
		killAgent();
	}


}
