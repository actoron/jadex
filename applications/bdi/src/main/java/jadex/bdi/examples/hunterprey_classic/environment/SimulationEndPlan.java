package jadex.bdi.examples.hunterprey_classic.environment;

import java.util.Map;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;


/**
 * 
 */
public class SimulationEndPlan extends Plan
{

	public void body()
	{
		Environment en = (Environment)getBeliefbase().getBelief("environment").getFact();
		Creature[] creatures = en.getCreatures();
		Future<Void>	destroyed	= new Future<Void>();
		IResultListener<Map<String, Object>>	lis	= new CounterResultListener<Map<String, Object>>(creatures.length, new DelegationResultListener<Void>(destroyed));
		for(int i = 0; i < creatures.length; i++)
		{
			// System.out.println(creatures[i].getAID());
			en.removeCreature(creatures[i]);
			getAgent().getExternalAccess(creatures[i].getAID()).killComponent().addResultListener(lis);
		}
		
		destroyed.get();
		getAgent().getExternalAccess(getScope().getComponentIdentifier().getParent()).killComponent();
	}
}
