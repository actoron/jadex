package jadex.android.standalone.metaservice;

import jadex.android.standalone.JadexClassLoading;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class JadexMetaService extends Service
{

	abstract class JadexMetaServiceStub extends Binder
	{
		public abstract ComponentName getComponentName();
		public abstract IBinder getBinderProxy();
	}

	private String customServiceClassName;
	private JadexClassLoading classLoading;
	private ClassLoader cl;
	private Service uService;
	private IBinder uServiceBinder;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		classLoading = new JadexClassLoading(this);
		cl = classLoading.getClassLoaderWithoutParent();
//		AndroidContextManager.getInstance().setAndroidContext(this);
	}
	

	@Override
	public IBinder onBind(Intent intent)
	{
		String cn = intent.getStringExtra(ActivityUsingMetaService.KEY_CUSTOM_SERVICE_CLASSNAME);

		if (cn == null)
		{
			throw new Error("Please fill in the ClassName of your Service in the Intent's KEY_CUSTOM_SERVICE_CLASSNAME field!");
		}

		this.customServiceClassName = cn;

		
		uService = createUserService();
		
		
		if (uService == null ){
			throw new Error("Problem loading " + customServiceClassName);
		}

		Thread.currentThread().setContextClassLoader(cl);
		uService.onCreate();
		uServiceBinder = uService.onBind(null);
		
		final ComponentName componentName = new ComponentName(this, uService.getClass());
		
		return new JadexMetaServiceStub()
		{

			@Override
			public ComponentName getComponentName()
			{
				return componentName;
			}


			@Override
			public IBinder getBinderProxy()
			{
				Class<?> iface1;
				IBinder result = null;
				try
				{
//					@SuppressWarnings("unchecked")
//					Class creatorClass = cl.loadClass("jadex.android.classloading.metaservice.ProxyCreator");
//					Object creator = creatorClass.newInstance();
					ProxyCreator creator = new ProxyCreator();
					iface1 = this.getClass().getClassLoader().loadClass("jadex.android.classloading.ITimeService");
					
//					Method method = creatorClass.getMethod("createBinderProxy", new Class[]{InvocationHandler.class, Class[].class});
					
//					result = (IBinder) method.invoke(creator, invokator, new Class[]{iface1, IBinder.class, IUserService.class});
					result = creator.createBinderProxy(invokator, new Class[]{iface1, IBinder.class, IUserService.class});
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				return result;
			}
			

		};
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
//		AndroidContextManager.getInstance().setAndroidContext(null);
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return super.onUnbind(intent);
	}

	private Service createUserService()
	{

		Service userService = null;
		try
		{
			@SuppressWarnings("unchecked")
			Class<?> serviceClass = cl.loadClass(customServiceClassName);
			Object newInstance = serviceClass.newInstance();

			if (newInstance instanceof Service)
			{
				userService = (Service) newInstance;
			}

		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		return userService;
	}

	private InvocationHandler invokator = new InvocationHandler()
	{

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			Method[] methods = uServiceBinder.getClass().getMethods();
			Method method2 = uServiceBinder.getClass().getMethod(method.getName(), method.getParameterTypes());
			return method2.invoke(uServiceBinder, args);
		}
	};
	

}
