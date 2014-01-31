package jadex.android.standalone.clientservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jadex.android.CustomTestRunner;
import jadex.android.service.JadexPlatformManager;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.IBinder;

@RunWith(CustomTestRunner.class)
public class UniversalClientServiceTest
{

	private UniversalClientServiceBinder service;
	private UniversalClientService context;

	@Before
	public void setUp() throws Exception
	{
		JadexPlatformManager.getInstance().setAppClassLoader("", this.getClass().getClassLoader());
		context = new UniversalClientService();
		context.onCreate();
		service = context.new UniversalClientServiceBinder();

		MyService.init();
	};

	@Test
	public void testBindLifecycle()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.bindClientService(getIntent(), conn, Activity.BIND_AUTO_CREATE, new ApplicationInfo());
		assertTrue(conn.bound);
		service.unbindClientService(conn);
		assertFalse(conn.bound);
	}

	@Test
	public void testServiceConnection()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.bindClientService(getIntent(), conn, Activity.BIND_AUTO_CREATE, new ApplicationInfo());
		assertEquals(2, conn.getResult());
	}

	@Test
	public void testStartService()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.startClientService(getIntent(), new ApplicationInfo());
		assertTrue(MyService.created);
		assertTrue(service.isClientServiceStarted(getIntent()));
	}

	@Test
	public void testStopService()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.startClientService(getIntent(), new ApplicationInfo());
		service.stopClientService(getIntent());
		assertFalse(service.isClientServiceStarted(getIntent()));
		assertTrue(MyService.destroyed);
	}
	
	@Test
	public void testStartBindStopLifecycle()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.startClientService(getIntent(), new ApplicationInfo());
		service.bindClientService(getIntent(), conn, Activity.BIND_AUTO_CREATE, new ApplicationInfo());
		service.stopClientService(getIntent());
		assertTrue(conn.bound);
		assertFalse(service.isClientServiceStarted(getIntent()));
		assertFalse(MyService.destroyed);
	}

	@Test
	public void testStartBindUnbindLifecycle()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.startClientService(getIntent(), new ApplicationInfo());
		service.bindClientService(getIntent(), conn, Activity.BIND_AUTO_CREATE, new ApplicationInfo());
		service.unbindClientService(conn);
		assertFalse(conn.bound);
		assertTrue(service.isClientServiceStarted(getIntent()));
		assertFalse(MyService.destroyed);
	}
	
	@Test
	public void testStopNonRunningService()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.stopClientService(getIntent());
		assertFalse(service.isClientServiceStarted(getIntent()));
		assertFalse(MyService.destroyed);
	}
	
	@Test
	public void testUnbindNonBoundService()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.unbindClientService(conn);
		assertFalse(conn.bound);
	}
	
	@Test
	public void testBindUnbindCycle()
	{
		MyServiceConnection conn = new MyServiceConnection();
		service.bindClientService(getIntent(), conn, Activity.BIND_AUTO_CREATE, new ApplicationInfo());
		service.unbindClientService(conn);
		assertFalse(service.isClientServiceConnection(conn));
	}

	private Intent getIntent()
	{
		Intent i = new Intent(context, MyService.class);
		return i;
	}

	private static class MyBinder extends Binder
	{
		public int getResult()
		{
			return 2;
		}
	}

	private static class MyServiceConnection implements ServiceConnection
	{
		public boolean bound;
		private MyBinder service;
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			bound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			this.service = (MyBinder) service;
			bound = true;
		}

		public int getResult()
		{
			return service.getResult();
		}
	}

	public static class MyService extends Service
	{

		public static boolean created;
		public static boolean started;
		public static boolean destroyed;

		@Override
		public void onCreate()
		{
			super.onCreate();
			created = true;
		}

		public static void init()
		{
			created = started = destroyed = false;
		}

		@Override
		public void onStart(Intent intent, int startId)
		{
			super.onStart(intent, startId);
			started = true;
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			destroyed = true;
		}

		@Override
		public IBinder onBind(Intent intent)
		{
			return new MyBinder();
		}
	}
}
