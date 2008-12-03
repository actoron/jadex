package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Environment;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class SimulationEndPlan extends Plan {

	public void body() {

		Environment en = (Environment) getBeliefbase().getBelief("simulation_engine").getFact();
		Creature[] creatures = en.getCreatures();
		for(int i=0; i<creatures.length; i++)
		{
			try
			{
//				System.out.println(creatures[i].getAID());
				en.removeCreature(creatures[i]);
				IGoal kg = createGoal("ams_destroy_agent");
				kg.getParameter("agentidentifier").setValue(creatures[i].getAID());
				//dispatchTopLevelGoalAndWait(kg);
				dispatchSubgoalAndWait(kg);
			}
			catch(GoalFailureException gfe) 
			{
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
		
	}

}
