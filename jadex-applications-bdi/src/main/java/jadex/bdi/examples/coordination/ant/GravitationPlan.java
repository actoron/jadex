package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
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
		Vector2Int target = (Vector2Int) getParameter("gravitation_center").getValue();
		System.out.println("ANT IS UNDER GRAVITATION CONTROL!!!!!!!!!!! --> GravitationCenter: " + ((Vector2Int) target).toString());
		
		Map params = new HashMap();
//		params.put(IAgentAction.OBJECT_ID, getExternalAccess().getAgentIdentifier().getLocalName());
		params.put(Space2D.POSITION, target);		
		SyncResultListener srl = new SyncResultListener();
		env.performSpaceAction("absorbObject", params, srl); 
		srl.waitForResult();

		//kill agent
		killAgent();
	}


}
