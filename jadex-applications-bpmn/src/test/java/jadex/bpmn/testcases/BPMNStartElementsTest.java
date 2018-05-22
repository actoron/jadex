package jadex.bpmn.testcases;

import java.util.Map;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

public class BPMNStartElementsTest //extends TestCase
{
	@Test
	public void testStartActivities()
	{
//		System.err.println("starting platform");
		String projectroot = new String("jadex-applications-bpmn");
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(STest.getDefaultTestConfig(), new String[]{"-platformname", "testcases_*",
//				"-kernels", "\"all\"",	// Required for old hudson build, otherwise wrong bdi kernel is used as dependencies are not in correct order
				"-simulation", "true",
				"-asyncexecution", "true",
				"-libpath", SUtil.getOutputDirsExpression(projectroot, true),
//				"-logging", "true", // path.toString().indexOf("bdibpmn")!=-1 ? "true" : "false",
				"-logging_level", "java.util.logging.Level.WARNING",
//				"-debugfutures", "true",
//				"-nostackcompaction", "true",
				"-gui", "false",
				"-awareness", "false",
				"-saveonexit", "false",
				"-welcome", "false",
				"-autoshutdown", "false",
				"-opengl", "false",
				"-cli", "false",
				"-superpeerclient", "false", // TODO: fails on shutdown due to auto restart
//				"-deftimeout", "-1",
				"-printpass", "false",});
		
		long timeout = Starter.getLocalDefaultTimeout(null);
		
		IExternalAccess	platform	= fut.get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
		
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);

		
		CreationInfo ci = new CreationInfo();
		ci.setConfiguration("Case A");
		
		Map<String, Object> results = cms.createComponent("jadex.bpmn.testcases.StartElements.bpmn2", ci).getSecondResult();
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
