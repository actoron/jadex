package jadex.android.standalone.clientservice;

import android.os.Binder;
import android.os.IBinder;

public class ClientBinderProxy extends Binder
{
	public static final String CLIENT_SERVICE_COMPONENT = "jadex.android.standalone.clientservice.CLIENT_SERVICE_CLASS";

	private IBinder clientBinder;

	public ClientBinderProxy(IBinder clientBinder)
	{
		this.clientBinder = clientBinder;
	}

	public IBinder getClientBinder()
	{
		return clientBinder;
	}

}
