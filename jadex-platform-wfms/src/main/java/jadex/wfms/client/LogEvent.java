package jadex.wfms.client;

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
