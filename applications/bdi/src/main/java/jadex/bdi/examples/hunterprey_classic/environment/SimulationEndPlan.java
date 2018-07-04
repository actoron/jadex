package jadex.bdi.examples.hunterprey_classic.environment;

import java.util.Map;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
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
		IComponentManagementService	cms	= getAgent().getComponentFeature(IRequiredServicesFeature.class)
			.searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		
		Environment en = (Environment)getBeliefbase().getBelief("environment").getFact();
		Creature[] creatures = en.getCreatures();
		Future<Void>	destroyed	= new Future<Void>();
		IResultListener<Map<String, Object>>	lis	= new CounterResultListener<Map<String, Object>>(creatures.length, new DelegationResultListener<Void>(destroyed));
		for(int i = 0; i < creatures.length; i++)
		{
			// System.out.println(creatures[i].getAID());
			en.removeCreature(creatures[i]);
			cms.destroyComponent(creatures[i].getAID()).addResultListener(lis);
		}
		
		destroyed.get();
		cms.destroyComponent(getScope().getComponentIdentifier().getParent());
	}
}
