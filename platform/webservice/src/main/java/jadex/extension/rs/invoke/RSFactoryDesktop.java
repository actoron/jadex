package jadex.extension.rs.invoke;

import java.lang.reflect.InvocationHandler;

import jadex.bridge.IInternalAccess;
import jadex.extension.rs.RSFactory;

/**
 * Factory for instantiating the Java SE RestServiceWrapperInvocationHandler.
 */
public class RSFactoryDesktop extends RSFactory
{

	@Override
	public InvocationHandler createRSWrapperInvocationHandler(IInternalAccess agent, Class<?> impl)
	{
		return new RestServiceWrapperInvocationHandler(agent, impl);
	}

}
