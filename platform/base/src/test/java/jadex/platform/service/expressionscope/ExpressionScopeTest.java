package jadex.platform.service.expressionscope;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 *  Test searching and providing services with dynamic scope.
 */
public class ExpressionScopeTest
{
	@Test
	public void	testPojoExpressionScopes()
	{
		IExternalAccess	platform	= Starter.createPlatform(STest.getDefaultTestConfig(getClass())).get();
		
		// Test passing scope as argument.
		platform.createComponent(new CreationInfo()
			.setFilenameClass(ArgumentScopeProviderAgent.class)
			.addArgument("scope", ServiceScope.GLOBAL)
		).get();
		// TODO: test searching for service with different scopes
		
		// Test passing scope in POJO constructor
		platform.addComponent(new PojoScopeProviderAgent(ServiceScope.GLOBAL)).get();
		// TODO: test searching for service with different scopes
	}
}
