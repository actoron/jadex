package jadex.wfms.client;

public interface IWorkitemListener
{
	public void workitemAdded(WorkitemQueueChangeEvent event);
	
	public void workitemRemoved(WorkitemQueueChangeEvent event);
	
	public IClient getClient();
}
