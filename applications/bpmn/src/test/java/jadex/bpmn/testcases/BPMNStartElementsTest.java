package jadex.bpmn.testcases;

import java.util.Map;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

public class BPMNStartElementsTest //extends TestCase
{
	@Test
	public void testStartActivities()
	{
//		System.err.println("starting platform");
		IPlatformConfiguration	config	= STest.getDefaultTestConfig();
//		config.setGui(true);
//		config.setDefaultTimeout(-1);
//		config.setValue("kernel_multi", false);
//		config.setValue("kernel_micro", true);
//		config.setValue("kernel_bpmn", true);
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(config);
		
		long timeout = Starter.getDefaultTimeout(null);
		
		IExternalAccess	platform	= fut.get(timeout);
		timeout	= Starter.getDefaultTimeout(platform.getId());
		
		IComponentManagementService	cms	= (IComponentManagementService)platform.searchService( new ServiceQuery<>(IComponentManagementService.class)).get(timeout);

		
		CreationInfo ci = new CreationInfo();
		ci.setConfiguration("Case A");
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut2	= cms.createComponent("jadex.bpmn.testcases.StartElements.bpmn2", ci);
		
//		new Future<>().get(-1);
		
		Map<String, Object> results = fut2.getSecondResult();
		if (!("A".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected A, got " + results.get("result") + ".");
		}
		
		ci = new CreationInfo();
		ci.setConfiguration("Case B");
		results = cms.createComponent("jadex.bpmn.testcases.StartElements.bpmn2", ci).getSecondResult();
		if (!("B".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected B, got " + results.get("result") + ".");
		}
	}
}
