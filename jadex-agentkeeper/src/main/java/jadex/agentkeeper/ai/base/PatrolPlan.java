package jadex.agentkeeper.ai.base;

import java.util.Iterator;
import java.util.List;

import jadex.agentkeeper.ai.creatures.orc.OrcBDI;
import jadex.agentkeeper.ai.creatures.orc.OrcBDI.AchieveMoveToSector;
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
 * Patrol along the patrol points.
 */
public class PatrolPlan
{
	@PlanCapability
	protected OrcBDI	capa;

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
		System.out.println("rndpos " + rndpos);
		System.out.println("rndpos " + rndpos.getClass().getName());

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

		rplan.dispatchSubgoal(capa.new AchieveMoveToSector(posi)).addResultListener(
				new ExceptionDelegationResultListener<OrcBDI.AchieveMoveToSector, Void>(ret)
				{
					public void customResultAvailable(AchieveMoveToSector mtg)
					{
						ret.setResult(null);
					}
				});


		return ret;
	}
}
