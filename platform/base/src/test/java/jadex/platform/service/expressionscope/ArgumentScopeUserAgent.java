package jadex.platform.service.expressionscope;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that uses services with expression search scope.
 */
@Agent
@RequiredServices(@RequiredService(name="typescope", type=IExpressionScopeService.class, scope=ServiceScope.EXPRESSION, scopeexpression="$args.typescope"))
public class ArgumentScopeUserAgent
{
	@OnService(requiredservice = @RequiredService(type=IExpressionScopeService.class, scope=ServiceScope.EXPRESSION, scopeexpression="$args.attrscope"))
	IExpressionScopeService	attrscope;
	
	/**
	 *  Try to find services.
	 */
	@OnStart
	public IFuture<Void>	body(IInternalAccess agent)
	{
		try
		{
			agent.getService("typescope").get();
		}
		catch(ServiceNotFoundException snfe)
		{
			agent.getResults().put("typescope", snfe.getQuery().getScope());
		}
		try
		{
			agent.getService("jadex.platform.service.expressionscope.IExpressionScopeService").get();
		}
		catch(ServiceNotFoundException snfe)
		{
			agent.getResults().put("attrscope", snfe.getQuery().getScope());
		}
		return IFuture.DONE;
	}
}
