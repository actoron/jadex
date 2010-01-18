package jadex.wfms.client;

public class ActivityEvent
{
	private String userName;
	private IClientActivity activity;
	
	public ActivityEvent(String userName, IClientActivity activity)
	{
		this.userName = userName;
		this.activity = activity;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
}
