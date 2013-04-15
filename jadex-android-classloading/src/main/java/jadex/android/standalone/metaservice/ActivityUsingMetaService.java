package jadex.android.standalone.metaservice;

import jadex.android.standalone.metaservice.JadexMetaService.JadexMetaServiceStub;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public abstract class ActivityUsingMetaService extends Activity implements ServiceConnection
{

	public final static String customServiceClassName = null;
	public final static String KEY_CUSTOM_SERVICE_CLASSNAME = "KEY_CUSTOM_SERVICE_CLASSNAME";
	private ComponentName boundComponentName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags)
	{
		// String cn = service.getComponent().getClassName();
		String cn = service.getAction();

		// bind to meta service
		Intent intent = new Intent(this, JadexMetaService.class);
		intent.putExtra(KEY_CUSTOM_SERVICE_CLASSNAME, cn);
		return super.bindService(intent, this, flags);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		JadexMetaServiceStub metaService = (JadexMetaServiceStub) service;

		IBinder userStub = metaService.getBinderProxy();

		boundComponentName = metaService.getComponentName();
		onCustomServiceConnected(boundComponentName, userStub);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0)
	{
		onCustomServiceDisconnected(boundComponentName);
	}

	abstract protected void onCustomServiceConnected(ComponentName name, IBinder service);

	abstract protected void onCustomServiceDisconnected(ComponentName name);

}
