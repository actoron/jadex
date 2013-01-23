package jadex.android.classloading;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

public interface ITimeService
{
	public String getTime(long l);
	public void startJadexPlatform();
	
	public void setListener(Listener l);
	
	public interface Listener {
		public void platformStarted();
	}
}
