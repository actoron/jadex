package jadex.wfms.listeners;

public interface ILogListener
{
	/**
	 * This method is invoked on new log messages.
	 * @param message the new log message
	 */
	public void logMessage(LogEvent event);
}
