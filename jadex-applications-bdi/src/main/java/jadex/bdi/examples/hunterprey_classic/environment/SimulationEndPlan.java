package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;

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

		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getService(getAgent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		cms.destroyComponent(getScope().getComponentIdentifier().getParent());
	}
}
