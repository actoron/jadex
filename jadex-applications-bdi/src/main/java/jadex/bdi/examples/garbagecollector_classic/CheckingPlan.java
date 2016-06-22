package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Check the grid for garbage.
 */
public class CheckingPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("env").getFact();
		int size = env.getGridSize();
		Position mypos = (Position)getBeliefbase().getBelief("pos").getFact();
		// Todo: fix race condition in bdi init?
//		assert mypos.equals(env.getPosition(getComponentName()));
		Position newpos = computeNextPosition(mypos, size);

//		System.out.println("Moving from "+mypos+" to: "+newpos);
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(newpos);
		dispatchSubgoalAndWait(go);
//		System.out.println("Moved to: "+newpos);
	}

	/**
	 *  Compute the next position.
	 */
	protected static Position computeNextPosition(Position pos, int size)
	{
		if(pos.getX()+1<size && pos.getY()%2==0)
		{
			pos = new Position(pos.getX()+1, pos.getY());
		}
		else if(pos.getX()-1>=0 && pos.getY()%2!=0)
		{
			pos = new Position(pos.getX()-1, pos.getY());
		}
		else
		{
			pos = new Position(pos.getX(), (pos.getY()+1)%size);
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
