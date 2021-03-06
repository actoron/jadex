package jadex.platform.service.expressionscope;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with expression publication scope.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IExpressionScopeService.class, scope=ServiceScope.EXPRESSION, scopeexpression="$pojoagent.getScope()"))
//@ProvidedServices(@ProvidedService(type=IExpressionScopeService.class, scope=ServiceScope.EXPRESSION, scopeexpression="$pojoagent.scope"))
public class PojoScopeProviderAgent	implements IExpressionScopeService
{
	/** The actual scope is given from outside. */
	protected ServiceScope	scope;
	
	/**
	 *  POJO constructor.
	 */
	public PojoScopeProviderAgent(ServiceScope scope)
	{
		this.scope	= scope;
	}
	
	/**
	 *  Accessor for scope value.
	 */
	// Hack!!! should not require public field or accessor?
	public ServiceScope getScope()
	{
		return scope;
	}
}
