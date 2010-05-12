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

			// inform logger
			IInternalEvent logE = createInternalEvent("logInformation");
			logE.getParameter("id").setValue("negotiation phase");
			logE.getParameter("value").setValue(new Date().getTime());
			dispatchInternalEvent(logE);

			if (getBeliefbase().getBeliefSet("smaReady").size() == getBeliefbase().getBeliefSet("smas").size()
				&& !((Boolean) getBeliefbase().getBelief("executionPhase").getFact()))
			{
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(true));
				System.out.println();
				System.out.println("---- Execution phase started! ----");
				System.out.println();
				
				// inform logger
				IInternalEvent logE2 = createInternalEvent("logInformation");
				logE2.getParameter("id").setValue("execution phase");
				logE2.getParameter("value").setValue(new Long(new Date().getTime()).doubleValue());
				dispatchInternalEvent(logE2);
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
