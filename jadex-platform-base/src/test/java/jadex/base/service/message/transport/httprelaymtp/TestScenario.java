package jadex.base.service.message.transport.httprelaymtp;

import jadex.commons.SUtil;

import java.net.InetAddress;
import java.util.Date;
import java.util.Random;

/**
 *  Test with many clients.
 */
public class TestScenario
{
	/**
	 *  Main method for testing.
	 */
	public static void	main(String[] args) throws Exception
	{
		// Start some receivers.
		final String[]	ids	= new String[10];
		for(int i=0; i<ids.length; i++)
		{
			final String	id	= SUtil.createUniqueId(InetAddress.getLocalHost().getHostName(), 3);
			ids[i]	= id;
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						RelayClient.main(new String[]{id});
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		// Wait for clients to connect.
		Thread.sleep(3000);
		
		// Start some senders
		for(int i=0; i<ids.length; i++)
		{
			final String	id	= ids[i];
			new Thread(new Runnable()
			{
				public void run()
				{
					Random	rnd	= new Random();
					try
					{
						while(true)
						{
							Object	targetid	= ids[rnd.nextInt(ids.length)];
							Object	data	= "Message from "+id+" to "+targetid+" at "+new Date();
							SRelay.sendData(targetid, SRelay.DEFAULT_ADDRESS, data);
//							Thread.sleep(100);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
