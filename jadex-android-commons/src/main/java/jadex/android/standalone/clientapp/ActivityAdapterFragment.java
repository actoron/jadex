package jadex.android.standalone.clientapp;

import android.content.Intent;
import android.content.ServiceConnection;


public class ActivityAdapterFragment extends android.support.v4.app.Fragment
{

	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return getActivity().bindService(service, conn, flags);
	}
	
	public void unbindService(ServiceConnection conn) {
		getActivity().unbindService(conn);
	}
	
	public void setProgressBarIndeterminateVisibility(boolean indeterminate) {
		getActivity().setProgressBarIndeterminate(indeterminate);
	}
	
	protected void runOnUiThread(Runnable runnable) {
		getActivity().runOnUiThread(runnable);
	}
}
