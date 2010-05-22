package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import java.util.Date;

/**
 * All SMAs are ready
 */
public class SmaReadyPlan extends Plan
{

	public void body()
	{
		try
		{
			startAtomic();
			IMessageEvent me = (IMessageEvent) getReason();
			String smaName = (String) me.getParameter("content").getValue();

			getBeliefbase().getBeliefSet("smaReady").addFact(smaName);

			if (getBeliefbase().getBeliefSet("smaReady").size() == getBeliefbase().getBeliefSet("smas").size()
				&& !((Boolean) getBeliefbase().getBelief("executionPhase").getFact()))
			{
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(true));
				System.out.println();
				System.out.println("---- Execution phase" + this.getComponentName() + "started! ----");
				System.out.println();
				
				dispatchTopLevelGoal(createGoal("startWorkflow"));
				endAtomic();
				aborted();
			} else
			{
				endAtomic();
			}
			
		} catch (Exception e)
		{
			System.out.println(this.getType());
			fail(e);
		}
	}
}
