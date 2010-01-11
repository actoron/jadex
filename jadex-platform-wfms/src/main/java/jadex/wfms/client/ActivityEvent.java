package jadex.wfms.client;

public class ActivityEvent
{
	private IClientActivity activity;
	
	public ActivityEvent(IClientActivity activity)
	{
		this.activity = activity;
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
}
