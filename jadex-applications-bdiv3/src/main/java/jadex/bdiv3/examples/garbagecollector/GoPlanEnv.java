package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.garbagecollector.GarbageCollectorBDI.Go;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 *  Go to a specified position.
 */
@Plan
public class GoPlanEnv 
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageCollectorBDI collector;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected Go goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Grid2D env = collector.getEnvironment();
		IVector2 target = goal.getPosition();
		ISpaceObject myself = collector.getMyself();
		
		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
		{
			String dir = null;
			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			IVector1 md = env.getShortestDirection(mypos.getX(), target.getX(), true);
			if(md.getAsInteger()==1)
			{
				dir = GoAction.RIGHT;
			}
			else if(md.getAsInteger()==-1)
			{
				dir = GoAction.LEFT;
			}
			else
			{
				md = env.getShortestDirection(mypos.getY(), target.getY(), false);
				if(md.getAsInteger()==1)
				{
					dir = GoAction.DOWN;
				}
				else if(md.getAsInteger()==-1)
				{
					dir = GoAction.UP;
				}
			}

			System.out.println("Wants to go: "+dir+" "+mypos+" "+target);
			
			Future<Void> fut = new Future<Void>();
			DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
			Map params = new HashMap();
			params.put(GoAction.DIRECTION, dir);
			params.put(ISpaceAction.OBJECT_ID, env.getAvatar(collector.getAgent().getComponentDescription()).getId());
			env.performSpaceAction("go", params, lis); 
			fut.get();
			
			System.out.println("after go");
		}
	}
}

