package jadex.bdi.testcases;

import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ITuple2Future;

/**
 *  Start hunter prey in a loop to find heisenbug.
 *
 */
public class AppLoop
{
	/**
	 *  start
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= STest.getDefaultTestConfig();
		config.getExtendedPlatformConfiguration().setDf(true);
		IExternalAccess	platform	= Starter.createPlatform(config, args).get();
		IComponentManagementService	cms	= platform.searchService(new ServiceQuery<IComponentManagementService>(IComponentManagementService.class)).get();
		for(int i=0; ; i++)
		{
			if(i%100==0)
				System.out.println(i);
			ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut
				= cms.createComponent("jadex/bdi/examples/hunterprey_classic/HunterPrey.application.xml", null);
			cms.destroyComponent(fut.getFirstResult()).get();
			fut.getSecondResult();
		}
	}
}
