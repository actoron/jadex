package jadex.extension.rs;

import jadex.bridge.IInternalAccess;
import jadex.extension.rs.invoke.RestServiceWrapperInvocationHandler;

import java.lang.reflect.InvocationHandler;

public class RSFactoryDesktop extends RSFactory
{

	@Override
	public InvocationHandler createRSWrapperInvocationHandler(IInternalAccess agent, Class<?> impl)
	{
		return new RestServiceWrapperInvocationHandler(agent, impl);
	}

}
