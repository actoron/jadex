package jadex.wfms.listeners;

public class LogEvent
{
	private String message; 
	
	public LogEvent(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
