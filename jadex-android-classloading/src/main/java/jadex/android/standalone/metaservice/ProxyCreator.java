package jadex.android.standalone.metaservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import android.os.IBinder;

public class ProxyCreator implements IProxyCreator
{

	@Override
	public IBinder createBinderProxy(InvocationHandler handler, Class... interfaces)
	{
//		Class<?>[] classes = new Class<?>[interfaces.length+2];
//		int i = 0;
//		for (; i < interfaces.length; i++)
//		{
//			classes[i] = interfaces[i];
//		}
//		classes[i] = IUserService.class;
//		classes[++i] = IBinder.class;
		IBinder result = (IBinder) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, handler);
		
		return result;
	}

}
