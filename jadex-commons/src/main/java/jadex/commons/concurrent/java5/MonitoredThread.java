package jadex.commons.concurrent.java5;

public class MonitoredThread extends Thread
{
	protected MonitoredThreadPoolExecutor origin;
	
	protected int number;
	
	protected long departure = Long.MAX_VALUE;
	
	public MonitoredThread(Runnable r, MonitoredThreadPoolExecutor origin)
	{
		super(r);
		this.origin = origin;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public void setNumber(int num)
	{
		number = num;
	}
	
	public long getDeparture()
	{
		return departure;
	}
	
	public void setDeparture(long departure)
	{
		this.departure = departure;
	}
	
	public void borrow()
	{
		
	}
	
	public boolean isBlocked()
	{
		State threadstate = getState();
		return State.BLOCKED == threadstate || State.WAITING == threadstate || State.TIMED_WAITING == threadstate;
	}
}
