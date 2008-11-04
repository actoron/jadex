package jadex.bdi.examples.puzzle;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


/**
 *  Play the game until a solution is found.
 */
public class PlayGamePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		System.out.println("Now puzzling:");
		long start = getTime();
		IGoal play = createGoal("makemove");
		play.getParameter("depth").setValue(new Integer(0));
		try
		{
			dispatchSubgoalAndWait(play);
//			System.out.println("Found a solution: "
//				+board.getMoves().size()+" "+board.getMoves());
		}
//		catch(GoalFailureException gfe)
		catch(Exception gfe)
		{
			System.out.println("No solution found :-( "+gfe);
		}
		
		long end = getTime();
		System.out.println("Needed: "+(end-start)+" millis.");

		killAgent();
	}
}
