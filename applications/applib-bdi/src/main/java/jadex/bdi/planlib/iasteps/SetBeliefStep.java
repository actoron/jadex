package jadex.bdi.planlib.iasteps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bdiv3x.runtime.IBeliefbase;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

public class SetBeliefStep implements IComponentStep<Void>
{
	protected Map beliefs;
	
	/**
	 *  Sets an agent's belief.
	 *  @param belief Name of the belief.
	 *  @param fact New fact of the belief.
	 */
	public SetBeliefStep(final String belief, final Object fact)
	{
		this.beliefs = new HashMap() {{
			put(belief, fact);
		}};
	}
	
	/**
	 *  Sets multiple agent beliefs.
	 *  @param beliefs The beliefs.
	 */
	public SetBeliefStep(Map beliefs)
	{
		this.beliefs = beliefs;
	}
	
	public IFuture<Void> execute(IInternalAccess ia)
	{
		IBeliefbase bb = ia.getFeature(IBDIXAgentFeature.class).getBeliefbase();
		for (Iterator it = beliefs.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			bb.getBelief((String) entry.getKey()).setFact(entry.getValue());
		}
		
		return IFuture.DONE;
	}
}
