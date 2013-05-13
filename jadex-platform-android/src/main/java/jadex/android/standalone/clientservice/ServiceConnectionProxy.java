package jadex.android.standalone.clientservice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnectionProxy implements ServiceConnection
{

	private ServiceConnection conn;
	private ComponentName originalComponent;

	public ServiceConnectionProxy(ServiceConnection conn, ComponentName originalComponent)
	{
		this.conn = conn;
		this.originalComponent = originalComponent;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		UniversalClientBinder binder = (UniversalClientBinder) service;
		conn.onServiceConnected(originalComponent, binder.getClientBinder());
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		conn.onServiceDisconnected(originalComponent);
		conn = null;
		originalComponent = null;
	}

}
