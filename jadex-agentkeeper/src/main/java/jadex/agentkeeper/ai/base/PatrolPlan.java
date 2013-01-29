package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.ai.AbstractBeingBDI.AchieveMoveToSector;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;


/**
 * Patrol to random Points
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class PatrolPlan
{
	@PlanCapability
	protected AbstractBeingBDI	capa;

	@PlanPlan
	protected RPlan		rplan;

	protected Grid2D	environment;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public PatrolPlan()
	{
		// getLogger().info("Created: "+this);
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		environment = capa.getEnvironment();

		final Future<Void> ret = new Future<Void>();

		IVector2 rndpos = environment.getRandomGridPosition(Vector2Int.ZERO);

		moveToLocation(rndpos).addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}

	/**
	 * 
	 */
	protected IFuture<Void> moveToLocation(final IVector2 pos)
	{
		final Future<Void> ret = new Future<Void>();

		Vector2Int posi = new Vector2Int(pos.getXAsInteger(), pos.getYAsInteger());

		rplan.dispatchSubgoal(capa.new AchieveMoveToSector(posi))
			.addResultListener(new ExceptionDelegationResultListener<AbstractBeingBDI.AchieveMoveToSector, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveToSector mtg)
			{
				ret.setResult(null);
			}
		});

		return ret;
	}
}
