package jadex.base.relay;

import java.net.InetAddress;
import java.util.Date;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class RelaySenderClient
{
	/**
	 *  Main method.
	 */
	public static void	main(String[] args) throws Exception
	{
		try
		{
			while(true)
			{
				String	targetid	= args.length>0 ? args[0] : InetAddress.getLocalHost().getHostName();
				Object	obj	= new Date();
				System.out.println("Sending to: "+targetid);
				SRelay.sendData(targetid, SRelay.DEFAULT_ADDRESS, obj);
				Thread.sleep(1000);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception: "+e);
		}

		System.out.println("Terminated");
	}
}
