package jadex.platform.service.expressionscope;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Test searching and providing services with dynamic scope.
 */
public class ExpressionScopeTest
{
	@Test
	public void	testProvidedExpressionScopes()
	{
		ServiceScope	testscope	= ServiceScope.COMPONENT_ONLY;
		IExternalAccess	platform	= Starter.createPlatform(STest.getDefaultTestConfig(getClass())
//			.setLogging(true)
		).get();
		
		IExternalAccess	argagent	= platform.createComponent(new CreationInfo()
			.setFilenameClass(ArgumentScopeProviderAgent.class)
			.addArgument("scope", testscope)
		).get();
		ServiceScope	argscope	= argagent.scheduleStep(ia -> new Future<>(ia.getProvidedService("ess").getServiceId().getScope())).get();
		assertEquals("Test passing scope as argument.", testscope, argscope);
		
		IExternalAccess	pojoagent	= platform.addComponent(new PojoScopeProviderAgent(testscope)).get();
		ServiceScope	pojoscope	= pojoagent.scheduleStep(ia -> new Future<>(ia.getProvidedService("ess").getServiceId().getScope())).get();
		assertEquals("Test passing scope in POJO constructor.", testscope, pojoscope);
		
		IExternalAccess	defagent1	= platform.createComponent(new CreationInfo()
				.setFilenameClass(ArgumentScopeProviderAgent.class)
		).get();
		ServiceScope	defscope1	= defagent1.scheduleStep(ia -> 
		{
			System.out.println("sid: "+ia.getProvidedService("ess").getServiceId());
			return new Future<>(ia.getProvidedService("ess").getServiceId().getScope());
		}).get();
		assertEquals("Test default scope from no arg.", ServiceScope.PLATFORM, defscope1);
			
		IExternalAccess	defagent	= platform.addComponent(new PojoScopeProviderAgent(null)).get();
		ServiceScope	defscope	= defagent.scheduleStep(ia -> new Future<>(ia.getProvidedService("ess").getServiceId().getScope())).get();
		assertEquals("Test default scope from null value.", ServiceScope.PLATFORM, defscope);
		
		platform.killComponent().get();
	}

	@Test
	public void	testRequiredExpressionScopes()
	{
		IExternalAccess	platform	= Starter.createPlatform(STest.getDefaultTestConfig(getClass())).get();
		// ArgumentScopeUserAgent searches for services and stores scope from ServiceNotFoundException as result 
		ServiceScope	testscope	= ServiceScope.COMPONENT_ONLY;
		IExternalAccess	argagent	= platform.createComponent(new CreationInfo()
			.setFilenameClass(ArgumentScopeUserAgent.class)
			.addArgument("typescope", testscope)
			.addArgument("attrscope", testscope)
			.setSuspend(true)
		).get();
		IFuture<Map<String, Object>>	results	= argagent.waitForTermination();
		argagent.resumeComponent().get();
		Map<String, Object>	resmap	= results.get();
		assertEquals(testscope, resmap.get("typescope"));
		assertEquals(testscope, resmap.get("attrscope"));
	}
}
