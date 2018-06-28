package jadex.extension.rs;

import jadex.bridge.IInternalAccess;
import jadex.extension.rs.invoke.RestServiceWrapperInvocationHandlerAndroid;

import java.lang.reflect.InvocationHandler;

/**
 * Factory for instantiating the Android RestServiceWrapperInvocationHandler.
 */
public class RSFactoryAndroid extends RSFactory
{

	@Override
	public InvocationHandler createRSWrapperInvocationHandler(IInternalAccess agent, Class<?> impl)
	{
		return new RestServiceWrapperInvocationHandlerAndroid(agent, impl);
	}

}
