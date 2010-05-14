package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;

public class SimulationEndPlan extends Plan {

	public void body()
	{
		Environment en = (Environment) getBeliefbase().getBelief("environment").getFact();
		Creature[] creatures = en.getCreatures();
		IGoal[]	destroy	= new IGoal[creatures.length];
		for(int i=0; i<creatures.length; i++)
		{
//			System.out.println(creatures[i].getAID());
			en.removeCreature(creatures[i]);
			destroy[i] = createGoal("cms_destroy_component");
			destroy[i].getParameter("componentidentifier").setValue(creatures[i].getAID());
			dispatchSubgoal(destroy[i]);
		}
		
		for(int i=0; i<creatures.length; i++)
		{
			try
			{
				waitForGoal(destroy[i]);
			}
			catch(GoalFailureException gfe)
			{
				gfe.printStackTrace();
			}
		}
		
//		// kill via gui		

		IComponentManagementService	ces	= (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
		ces.destroyComponent(getScope().getParent().getComponentIdentifier());
	}
}
