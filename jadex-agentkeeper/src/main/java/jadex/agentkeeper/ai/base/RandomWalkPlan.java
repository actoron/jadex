package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.ai.AbstractBeingBDI.AchieveMoveToSector;
import jadex.agentkeeper.ai.AbstractBeingBDI.PerformIdle;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Wander around randomly.
 *  
 *  @author Philip Willuweit p.willuweit@gmx.de
 */
public class RandomWalkPlan 
{
	@PlanCapability
	protected AbstractBeingBDI				capa;

	@PlanPlan
	protected RPlan					rplan;

	@PlanReason
	protected PerformIdle	goal;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public RandomWalkPlan()
	{
		//getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

//	/**
//	 *  The plan body.
//	 */
//	public void body()
//	{
//		IVector2	dest	= ((Space2D)getBeliefbase().getBelief("environment").getFact()).getRandomPosition(Vector2Int.UNIT);
//		IGoal	moveto	= createGoal("move_dest");
//		moveto.getParameter("destination").setValue(dest);
//		dispatchSubgoalAndWait(moveto);
//	}
	
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IVector2	dest	= capa.getEnvironment().getRandomGridPosition(Vector2Int.UNIT);
		
		Vector2Int intdest = new Vector2Int(dest.getXAsInteger(), dest.getYAsInteger());
		
		rplan.dispatchSubgoal(capa.new AchieveMoveToSector(intdest))
			.addResultListener(new ExceptionDelegationResultListener<AbstractBeingBDI.AchieveMoveToSector, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveToSector amt)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
