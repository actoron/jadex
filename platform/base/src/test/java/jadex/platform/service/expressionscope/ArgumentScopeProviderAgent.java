package jadex.platform.service.expressionscope;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with expression publication scope.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(name="ess", type=IExpressionScopeService.class, scope=ServiceScope.EXPRESSION, scopeexpression="$args.scope"))
public class ArgumentScopeProviderAgent	implements IExpressionScopeService
{
}
