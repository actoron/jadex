package jadex.bdi.planlib.protocols;


/**
 *  Information about an auction.
 *  
 *  todo: support privat and public auctions allowing results/offers being seen/hidden
 */
public class AuctionDescription
{
	//-------- attributes --------
	
	/** The auction starttime. */
	protected long starttime;
	
	/** The auction round timeout. */
	protected long roundtimeout;
	
	/** The auction content. */
	protected Object topic;

	//-------- constructors --------

	/**
	 *  Create a new auction description.
	 */
	public AuctionDescription()
	{		
	}
	
	/**
	 *  Create a new auction description.
	 *  @param starttime The start time.
	 *  @param roundtimeout The timeout of a single auction round.
	 *  @param topic The auction topic.
	 */
	public AuctionDescription(long starttime, long roundtimeout, Object topic)
	{
		this.starttime = starttime;
		this.roundtimeout = roundtimeout;
		this.topic = topic;
	}

	//-------- methods --------
	
	/**
	 *  Get the round timeout.
	 *  @return The round timeout.
	 */
	public long getRoundTimeout()
	{
		return roundtimeout;
	}

	/**
	 *  Set the round timeout.
	 *  @param roundtimeout The round timeout to set.
	 */
	public void setRoundTimeout(long roundtimeout)
	{
		this.roundtimeout = roundtimeout;
	}

	/**
	 *  Get the start time.
	 *  @return The start time.
	 */
	public long getStarttime()
	{
		return starttime;
	}

	/**
	 *  Set the start time.
	 *  @param starttime The starttime to set.
	 */
	public void setStarttime(long starttime)
	{
		this.starttime = starttime;
	}

	/**
	 *  Get the auction topic.
	 *  @return The topic.
	 */
	public Object getTopic()
	{
		return topic;
	}

	/**
	 *  Set the auction topic.
	 *  @param topic The topic to set.
	 */
	public void setTopic(Object topic)
	{
		this.topic = topic;
	}
}
