package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

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
				"-libpath", "new String[]{\""+new File("../jadex-applications-bpmn/target/classes").toURI().toURL().toString()+"\"}",
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
		
		long timeout	= BasicService.getLocalDefaultTimeout();
		ISuspendable	sus	= 	new ThreadSuspendable();
		
		IExternalAccess	platform	= fut.get(sus, timeout);
		
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getServiceUpwards(platform, IComponentManagementService.class).get(sus, timeout);

		
		CreationInfo ci = new CreationInfo();
		ci.setConfiguration("Case A");
		
		Map<String, Object> results = cms.createComponent("jadex.bpmn.testcases.StartElements.bpmn2", ci).getSecondResult(sus);
		if (!("A".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected A, got " + results.get("result") + ".");
		}
		
		ci = new CreationInfo();
		ci.setConfiguration("Case B");
		results = cms.createComponent("jadex.bpmn.testcases.StartElements.bpmn2", ci).getSecondResult(sus);
		if (!("B".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected B, got " + results.get("result") + ".");
		}
	}
}
