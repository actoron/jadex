package jadex.extension.rs;

import java.lang.reflect.InvocationHandler;

import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;

/**
 * Factory for instantiating the platform-specific RestServiceWrapperInvocationHandler.
 */
public abstract class RSFactory
{
	private static RSFactory INSTANCE = null;
	
	public static RSFactory getInstance() 
	{
		if(INSTANCE == null) 
		{
			Class<?> clazz = null;
			clazz = SReflect.classForName0("jadex.extension.rs.RSFactoryAndroid", null);
			if(clazz == null) 
			{
				clazz = SReflect.classForName0("jadex.extension.rs.invoke.RSFactoryDesktop", null);
			}
			if(clazz != null) 
			{
				try
				{
					INSTANCE = (RSFactory) clazz.newInstance();
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return INSTANCE;
	}

	public abstract InvocationHandler createRSWrapperInvocationHandler(IInternalAccess agent, Class<?> impl);
}
