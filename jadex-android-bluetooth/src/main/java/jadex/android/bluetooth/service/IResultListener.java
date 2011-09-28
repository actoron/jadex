package jadex.android.bluetooth.service;

public interface IResultListener {
	void resultAvailable(Object result);
	
	void exceptionOccurred(Exception exception);
}
