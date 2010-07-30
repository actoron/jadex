package deco4mas.examples.agentNegotiation.common.trustInformation;

public class TrustInformation
{
	private static Integer id = 0;
	private Integer identNumber;
	protected TrustEvent event;
	protected Long time;
	protected Object monitor = new Object();

	public TrustInformation(TrustEvent event, Long time)
	{
		synchronized (id)
		{
			this.event = event;
			this.time = time;
			identNumber = id;
			id++;
		}
	}

	public TrustEvent getEvent()
	{
		return event;
	}

	public Long getTime()
	{
		return time;
	}

	public Integer getId()
	{
		return identNumber;
	}

	public Object getMonitor()
	{
		return monitor;
	}

}