package jadex.base.service.message.transport.httprelaymtp.benchmark;



/**
 *  Base class for relay benchmarks.
 */
public abstract class AbstractRelayBenchmark	extends AbstractBenchmark
{
	//-------- constants --------
	
	/** The address of the relay servlet under test. */
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS;
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/";
	public static String	ADDRESS = "http://grisougarfield.dyndns.org:52339/relay/";
	
	/** The size of the messages. */
	public static final int	SIZE	= 1234;
	
	//-------- template methods --------
	
	/**
	 *  Also print transfer rate.
	 */
	protected void printResults(long time, int count)
	{
		super.printResults(time, count);
		System.out.println("Average transfer rate: "+(SIZE*count*100L/time)/100.0+" kB/s");
	}
	
	//-------- main --------
	
	/**
	 *  Start all relay benchmarks.
	 */
	public static void main(String[] args) throws Exception
	{
		AbstractBenchmark.main(new String[]
		{
			ReceivingBenchmark.class.getName(),
			KeepaliveSendingBenchmark.class.getName(),
			CustomSendingBenchmark.class.getName(),
			SendingBenchmark.class.getName()
		});
	}
}
