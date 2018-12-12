package jadex.bdi.examples.puzzle;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


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
		long	start	= getTime();
		long	startmem	= Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		IGoal play = createGoal("makemove");
		play.getParameter("depth").setValue(Integer.valueOf(0));
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
		if(getBeliefbase().containsBelief("endmem"))
		{
			Long	endmem	= (Long) getBeliefbase().getBelief("endmem").getFact();
			if(endmem!=null)
			{
				System.out.println("Needed: "+(((endmem.longValue()-startmem)*10/1024)/1024)/10.0+" Mb.");
			}
		}

		killAgent();
	}
}
