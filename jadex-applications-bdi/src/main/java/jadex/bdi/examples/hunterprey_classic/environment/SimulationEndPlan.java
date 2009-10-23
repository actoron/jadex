package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IContextService;

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
			destroy[i] = createGoal("ams_destroy_agent");
			destroy[i].getParameter("agentidentifier").setValue(creatures[i].getAID());
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
//		// TO DO: How to suicide?
//		
//		IGoal kg = createGoal("ams_destroy_agent");
//		kg.getParameter("agentidentifier").setValue(getAgentIdentifier());
//		dispatchTopLevelGoal(kg);
//		
//		killAgent();

		IContextService	cs	= (IContextService) getScope().getServiceContainer().getService(IContextService.class);
		cs.deleteContext(getScope().getApplicationContext(), null);		
	}
}
