package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.StringTokenizer;

/**
 *  Handles all incoming translation requests with
 *  an appropriate subgoal.
 */
public class ProcessTranslationRequestPlanE2 extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProcessTranslationRequestPlanE2()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		while(true)
		{
			IMessageEvent me = (IMessageEvent)waitForMessageEvent("request_translation");

			String request = (String)me.getParameter(SFipa.CONTENT).getValue();
			StringTokenizer stok	= new StringTokenizer(request, " ");
			int cnttokens = stok.countTokens();

			// Create a translation subgoal.
			if(cnttokens==3)
			{
				/*String action =*/ stok.nextToken();
				String dir	= stok.nextToken();
				String word = stok.nextToken();
				// Hack!!!

				IGoal goal = createGoal("translate");
				goal.getParameter("direction").setValue(dir);
				goal.getParameter("word").setValue(word);
				try
				{
					dispatchSubgoalAndWait(goal);
//					getLogger().info("Translated from "+goal.getName()+" "+
					getLogger().info("Translated from "+goal+" "+
						word+" - "+goal.getParameter("result").getValue());
						//word+" - "+goal.getResult());
				}
				catch(GoalFailureException e)
				{
					getLogger().info("Word is not in database: "+word);
				};
			}
			else
			{
				getLogger().warning("Request format not correct, needs: #" +
					"action direction word1 [word2]");
			}
		}
	}
}
