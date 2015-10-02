package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;

import org.junit.Test;

public class BPMNStartElementsTest //extends TestCase
{
	@Test
	public void testStartActivities() throws MalformedURLException
	{
//		System.err.println("starting platform");
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
//				"-kernels", "\"all\"",	// Required for old hudson build, otherwise wrong bdi kernel is used as dependencies are not in correct order
				"-simulation", "true",
				"-asyncexecution", "true",
				"-libpath", "new String[]{\""+SUtil.findBuildDir(new File("../jadex-applications-bpmn")).toURI().toURL().toString()+"\"}",
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
//				"-niotcptransport", "false",
//				"-tcptransport", "true",
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
