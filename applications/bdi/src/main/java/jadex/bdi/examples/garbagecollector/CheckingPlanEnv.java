package jadex.bdi.examples.garbagecollector;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Check the grid for garbage.
 */
public class CheckingPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 mypos = (IVector2)getBeliefbase().getBelief("pos").getFact();
		IVector2 newpos = computeNextPosition(mypos, size.getXAsInteger(), size.getYAsInteger());

//		System.out.println("Moving from "+mypos+" to: "+newpos);
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(newpos);
		dispatchSubgoalAndWait(go);
//		System.out.println("Moved to: "+newpos);
	}

	/**
	 *  Compute the next position.
	 */
	protected static IVector2 computeNextPosition(IVector2 pos, int sizex, int sizey)
	{
		// Go right in even lanes
//		if(pos==null)
//			System.out.println("testi2");
		if(pos.getXAsInteger()+1<sizex && pos.getYAsInteger()%2==0)
		{
			pos = new Vector2Int(pos.getXAsInteger()+1, pos.getYAsInteger());
		}
		// Go left in odd lanes
		else if(pos.getXAsInteger()-1>=0 && pos.getYAsInteger()%2!=0)
		{
			pos = new Vector2Int(pos.getXAsInteger()-1, pos.getYAsInteger());
		}
		// Go down else
		else
		{
			pos = new Vector2Int(pos.getXAsInteger(), (pos.getYAsInteger()+1)%sizey);
		}

		return pos;
	}

	/*public static void main(String[] args)
	{
		Position pos = new Position(0,0);
		while(true)
		{
			System.out.println(pos);
			pos = computeNextPosition(pos, 5);
		}
	}*/
}
