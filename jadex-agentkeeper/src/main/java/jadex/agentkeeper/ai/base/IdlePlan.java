package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.util.ISpaceObjectStrings;
import jadex.agentkeeper.util.ISpaceStrings;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * Plan to let the Agent hang around.
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 *
 */
public class IdlePlan
{
	@PlanCapability
	protected AbstractBeingBDI	capa;

	@PlanPlan
	protected RPlan		rplan;
	
	ISpaceObject spaceObject;
	
	/**
	 * The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		spaceObject = capa.getMySpaceObject();

		final Future<Void> ret = new Future<Void>();

		setRandomIdleStatus(Math.random()).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}

	private IFuture<Void>  setRandomIdleStatus(double random)
	{
		final Future<Void> ret = new Future<Void>();
		
	
		String status = random > 0.5f ? "Idle" : "Dance";
		
		spaceObject.setProperty(ISpaceObjectStrings.PROPERTY_STATUS, status);
		
		long waittime = (long)(5000/(Double)capa.getEnvironment().getProperty(ISpaceStrings.GAME_SPEED) * random);
		
		
		rplan.waitFor(waittime).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void exceptionOccurred(Exception exception)
			{

				exception.printStackTrace();
				
			}
			public void customResultAvailable(Void result)
			{
				ret.setResult(null);
			}
		});
		
		
		
		return ret;
		
	}
}
