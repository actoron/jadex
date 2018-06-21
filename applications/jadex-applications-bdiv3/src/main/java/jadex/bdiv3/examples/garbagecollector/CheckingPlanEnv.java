package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.garbagecollector.GarbageCollectorBDI.Go;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Check the grid for garbage.
 */
@Plan
public class CheckingPlanEnv
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageCollectorBDI collector;
	
	@PlanAPI
	protected IPlan rplan;
	
//	@PlanReason
//	protected Check goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Space2D env = collector.getEnvironment();
		IVector2 size = env.getAreaSize();
		IVector2 mypos = collector.getPosition();
		IVector2 newpos = computeNextPosition(mypos, size.getXAsInteger(), size.getYAsInteger());

//		System.out.println("Moving from "+mypos+" to: "+newpos);
		Go go = collector.new Go(newpos);
		rplan.dispatchSubgoal(go).get();
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
