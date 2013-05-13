package jadex.android.standalone.clientservice;

import android.os.IBinder;

public interface UniversalClientBinder
{
	public static final String CLIENT_SERVICE_COMPONENT = "jadex.android.standalone.clientservice.CLIENT_SERVICE_CLASS";

	public IBinder getClientBinder();
	

}
