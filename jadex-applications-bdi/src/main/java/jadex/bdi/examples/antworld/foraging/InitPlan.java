package jadex.bdi.examples.antworld.foraging;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.Plan;

/**
 *  Init the ant agent.
 */
public class InitPlan extends Plan
{
	
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		//add property change listener
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		myself.addPropertyChangeListener(new GravitationListener());
//		System.out.println("Moved to: "+newpos);
	}

}
