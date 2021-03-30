package jadex.bpmn.testcases;

import java.util.Map;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;

public class BPMNStartElementsTest //extends TestCase
{
	@Test
	public void testStartActivities()
	{
//		System.err.println("starting platform");
		IPlatformConfiguration	config	= STest.getLocalTestConfig(getClass());
//		config.setGui(true);
//		config.setDefaultTimeout(-1);
//		config.setValue("kernel_multi", false);
//		config.setValue("kernel_micro", true);
//		config.setValue("kernel_bpmn", true);
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(config);
		
		// Use larger timeout so we can reduce default timeout on build slave
		IExternalAccess	platform	= fut.get(Starter.getScaledDefaultTimeout(null, 5));
		
		CreationInfo ci = new CreationInfo();
		ci.setConfiguration("Case A");
		ci.setSuspend(true);
		
		IExternalAccess exta = platform.createComponent(ci.setFilename("jadex.bpmn.testcases.StartElements.bpmn2")).get();
		IFuture<Map<String, Object>> fresults = exta.waitForTermination();
		exta.resumeComponent();
		Map<String, Object> results = fresults.get();
		
		if (!("A".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected A, got " + results.get("result") + ".");
		}
		
		ci = new CreationInfo();
		ci.setConfiguration("Case B");
		exta = platform.createComponent(ci.setFilename("jadex.bpmn.testcases.StartElements.bpmn2")).get();
		fresults = exta.waitForTermination();
		exta.resumeComponent();
		results = fresults.get();

		if (!("B".equals(results.get("result"))))
		{
			throw new RuntimeException("BPMN start elements tests: Results do not match, expected B, got " + results.get("result") + ".");
		}
	}
	
	public static void main(String[] args)
	{
		while(true)
		{
			new BPMNStartElementsTest().testStartActivities();
			System.out.print(".");
		}
	}
}
