package jadex.examples.presentationtimer.remotecontrol.ui;

import jadex.examples.presentationtimer.common.ICountdownService;
import jadex.examples.presentationtimer.common.State;


public class CDListItem
{
	private ICountdownService	service;

	private String				time;

	private State				status;

	public CDListItem(ICountdownService service)
	{
		this.service = service;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public State getStatus()
	{
		return status;
	}

	public void setStatus(State status)
	{
		this.status = status;
	}

	public ICountdownService getService()
	{
		return service;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CDListItem) {
			return ((CDListItem)obj).getService().equals(service);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return service.hashCode();
	}

}
