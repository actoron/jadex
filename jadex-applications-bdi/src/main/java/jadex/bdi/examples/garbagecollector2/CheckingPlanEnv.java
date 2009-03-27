package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.garbagecollector.Position;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		Position mypos = (Position)getBeliefbase().getBelief("pos").getFact();
		Position newpos = computeNextPosition(mypos, size.getXAsInteger(), size.getYAsInteger());

//		System.out.println("Moving from "+mypos+" to: "+newpos);
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(newpos);
		dispatchSubgoalAndWait(go);
//		System.out.println("Moved to: "+newpos);
	}

	/**
	 *  Compute the next position.
	 */
	protected static Position computeNextPosition(Position pos, int sizex, int sizey)
	{
		if(pos.getX()+1<sizex && pos.getY()%2==0)
		{
			pos = new Position(pos.getX()+1, pos.getY());
		}
		else if(pos.getX()-1>=0 && pos.getY()%2==1)
		{
			pos = new Position(pos.getX()-1, pos.getY());
		}
		else
		{
			pos = new Position(pos.getX(), (pos.getY()+1)%sizey);
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
