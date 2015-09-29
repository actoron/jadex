package jadex.bdi.examples.puzzle;

import jadex.android.puzzle.ui.GuiProxy;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Play the game until a solution is found.
 */
public class PlayGamePlan extends Plan
{
	
	private GuiProxy proxy;
	public PlayGamePlan()
	{
		proxy = (GuiProxy) getBeliefbase().getBelief("gui_proxy").getFact();
	}
	/**
	 *  The plan body.
	 */
	public void body()
	{
		proxy.showMessage("Now puzzling:");
		long	start	= getTime();
		long	startmem	= Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		IGoal play = createGoal("makemove");
		play.getParameter("depth").setValue(Integer.valueOf(0));
		try
		{
			dispatchSubgoalAndWait(play);
//			proxy.showMessage("Found a solution: "
//				+board.getMoves().size()+" "+board.getMoves());
		}
//		catch(GoalFailureException gfe)
		catch(Exception gfe)
		{
			proxy.showMessage("No solution found :-( "+gfe);
		}
		
		long end = getTime();
		proxy.showMessage("Needed: "+(end-start)+" millis for " + getBeliefbase().getBelief("triescnt").getFact() + "moves");
		if(getBeliefbase().containsBelief("endmem"))
		{
			Long	endmem	= (Long) getBeliefbase().getBelief("endmem").getFact();
			if(endmem!=null)
			{
				proxy.showMessage("Needed: "+(((endmem.longValue()-startmem)*10/1024)/1024)/10.0+" Mb.");
			}
		}

		killAgent();
	}
}
