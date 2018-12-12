package jadex.bridge.component.streams;

import jadex.commons.transformation.annotations.Alias;

/**
 * 
 */
@Alias("jadex.base.service.message.streams.AckInfo")
public class AckInfo
{
	/** The start seqno. */
	protected int startseqno;
	
	/** The end seqno. */
	protected int endseqno;
	
	/** The stop flag. */
	protected boolean stop;

	/**
	 *  Create a new ack info.
	 */
	public AckInfo()
	{
	}

	/**
	 *  Create a new ack info.
	 */
	public AckInfo(int startseqno, int endseqno, boolean stop)
	{
		this.startseqno = startseqno;
		this.endseqno = endseqno;
		this.stop = stop;
	}

	/**
	 *  Get the startSequenceNumber.
	 *  @return The startSequenceNumber.
	 */
	public int getStartSequenceNumber()
	{
		return startseqno;
	}

	/**
	 *  Set the startSequenceNumber.
	 *  @param startSequenceNumber The startSequenceNumber to set.
	 */
	public void setStartSequenceNumber(int startSequenceNumber)
	{
		this.startseqno = startSequenceNumber;
	}

	/**
	 *  Get the endSequenceNumber.
	 *  @return The endSequenceNumber.
	 */
	public int getEndSequenceNumber()
	{
		return endseqno;
	}

	/**
	 *  Set the endSequenceNumber.
	 *  @param endSequenceNumber The endSequenceNumber to set.
	 */
	public void setEndSequenceNumber(int endSequenceNumber)
	{
		this.endseqno = endSequenceNumber;
	}

	/**
	 *  Get the stop.
	 *  @return The stop.
	 */
	public boolean isStop()
	{
		return stop;
	}

	/**
	 *  Set the stop.
	 *  @param stop The stop to set.
	 */
	public void setStop(boolean stop)
	{
		this.stop = stop;
	}
	
}