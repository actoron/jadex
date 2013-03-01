package jadex.platform.service.message.transport.udpmtp.sending;


/**
 *  Dummy task for shutting down the sender thread.
 *
 */
public class TxShutdownTask extends TxPacket
{
	public TxShutdownTask()
	{
		super(null, null);
	}
}
