package jadex.android.standalone.metaservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import android.os.IBinder;

public interface IProxyCreator
{
	public IBinder createBinderProxy(InvocationHandler handler, Class... interfaces);
}
