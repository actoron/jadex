package jadex.bdi.examples.antworld;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bdi.runtime.Plan;

/**
 *  Init the ant agent. Should an ant know its nest on start-up?
 */
public class InitPlan extends Plan
{
	
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		//add property change listener
		Space2D env = (Space2D) getBeliefbase().getBelief("env").getFact();
		ISpaceObject[] nests = env.getSpaceObjectsByType("nest");
		getBeliefbase().getBeliefSet("nests").addFact(nests[0]);		
//		myself.setProperty("id", myself.getId());
//		System.out.println("#InitPlan# Called: for ant: " + myself.getId());
//		myself.addPropertyChangeListener(new GravitationListener());
//		System.out.println("Moved to: "+newpos);
	}

}
