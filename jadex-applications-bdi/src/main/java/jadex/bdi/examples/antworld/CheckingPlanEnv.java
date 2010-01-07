package jadex.bdi.examples.antworld;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Int;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.security.SecureRandom;

/**
 *  Ant walks randomly on the grid. When it reaches the destination it walk randomly to the next destination.
 *  Plan can be interrupted if Ant finds a food source or if it detects pheromones.
 */
public class CheckingPlanEnv extends Plan
{
	public static final String DESTINATION = "destination";
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 mypos = (IVector2)getBeliefbase().getBelief("pos").getFact();
//		IVector2 newpos = computeNextPosition(mypos, size.getXAsInteger(), size.getYAsInteger());
		IVector2 newpos = computeNextPositionRandomly(size.getXAsInteger(), size.getYAsInteger());
		
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		myself.setProperty(DESTINATION, newpos);
//		System.out.println("#CheckPlanEnv# Plan started with following params: from " + mypos + " to " + newpos);
		
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
		if(pos.getXAsInteger()+1<sizex && pos.getYAsInteger()%2==0)
		{
			pos = new Vector2Int(pos.getXAsInteger()+1, pos.getYAsInteger());
		}
		// Go left in odd lanes
		else if(pos.getXAsInteger()-1>=0 && pos.getYAsInteger()%2==1)
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

	/**
	 *  Compute the next position randomly.
	 */
	protected static IVector2 computeNextPositionRandomly(int sizex, int sizey)
	{		
		SecureRandom rand = new SecureRandom();
		// Compute new position randomly		
		int xvalue = rand.nextInt(sizex);
		int yvalue = rand.nextInt(sizey);
		return new Vector2Int(xvalue, yvalue);
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
