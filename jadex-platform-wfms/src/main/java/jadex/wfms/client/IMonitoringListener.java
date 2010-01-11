package jadex.wfms.client;

public interface IMonitoringListener
{
	/**
	 * This method is invoked when a process finishes.
	 * @param event the finished process event
	 */
	public void processFinished(ProcessFinishedEvent event);
	
	/**
	 * This method is invoked on new log messages.
	 * @param message the new log message
	 */
	public void logMessage(LogEvent event);
}
