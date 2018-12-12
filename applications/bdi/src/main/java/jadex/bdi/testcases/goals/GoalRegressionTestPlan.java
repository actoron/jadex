package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan checks goal info events, if goal processing
 *  was as expected.
 *  The expected results of goal processing are read from
 *  special goal parameters (test_state, test_process_states).
 */
public class GoalRegressionTestPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		IGoal	goal	= (IGoal)getReason();	// old
		IGoal	goal	= ((ChangeEvent<IGoal>)getReason()).getValue();	// new to differentiate from goals to be processed.
		Boolean	state	= (Boolean)goal.getParameter("test_state").getValue();
//		boolean[]	pstates	= (boolean[])goal.getParameter("test_process_states").getValue();
		String 	errors	= "";
		TestReport	report	= new TestReport(""+goal, "Test execution of goal "+goal);

		// Check state.
		if(state!=null && !goal.isSucceeded()==state.booleanValue())
		{
			errors	+=	"State was "+goal.isSucceeded()+" should be "+state+".\n";
		}

		// Check process states.
		/*if(pstates!=null)
		{
			RGoal	rgoal	= (RGoal)((ElementWrapper)goal).unwrap();
			ApplicableCandidateList	apl	= rgoal.getApplicableCandidateList();
			Object[]	hes	= apl.getExecutedCandidates().toArray(new ICandidateInfo[0]);
			// Todo: How to access execution results of single plans?
//			IHistoryEntry[]	hes	= goal.getHistoryEntries();
			if(hes.length==pstates.length)
			{
//				for(int i=0; i<hes.length; i++)
//				{
//					if(!hes[i].isSucceeded()==pstates[i])
//					{
//						errors	+=	"Process state "+i+" was "
//							+ hes[i].isSucceeded()
//							+ " should be " + pstates[i] + ".\n";
//					}
//				}
			}
			else
			{
				errors	+=	"Number of processes was "+hes.length
					+ " should be "+pstates.length+".\n";
				for(int i=0; i<hes.length; i++)
				{
					errors	+=	"Process "+i+" was "
						+ hes[i] + ".\n";
				}
			}
		}*/

		if(errors.equals(""))
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setReason(errors);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
