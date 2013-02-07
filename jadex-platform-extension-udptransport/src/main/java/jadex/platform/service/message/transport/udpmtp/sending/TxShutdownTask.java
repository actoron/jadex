package jadex.platform.service.message.transport.udpmtp.sending;

import java.net.InetSocketAddress;

/**
 *  Dummy task for shutting down the sender thread.
 *
 */
public class TxShutdownTask implements ITxTask
{
	/**
	 * 
	 */
	public InetSocketAddress getResolvedReceiver()
	{
		return null;
	}
	
	/**
	 * 
	 */
	public byte[][] getPackets()
	{
		return null;
	}

	public short[] getTxPacketIds()
	{
		return null;
	}

	/**
	 * 
	 */
	public int getPriority()
	{
		return Integer.MIN_VALUE;
	}

	/**
	 * 
	 */
	public void transmissionFailed(String reason)
	{
	}
}
