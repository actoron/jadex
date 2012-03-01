package jadex.base.service.message.transport.httprelaymtp.io;

import jadex.base.service.message.transport.httprelaymtp.SRelay;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 *  Test unblocking waiting http receive.
 */
public class HttpTest
{
	public static void	main(String[]	args)	throws Exception
	{
		URL	url	= new URL(SRelay.DEFAULT_ADDRESS.substring(6)+"?id="+URLEncoder.encode("httptest", "UTF-8"));
		final HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		con.setUseCaches(false);
		final InputStream	in	= con.getInputStream();
		
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(2000);
					System.out.println(System.currentTimeMillis()+": trying to exit");
					
//					con.disconnect();	// Hangs until next ping :-(
					
//					in.close();	// Hangs until next ping :-(
					
					// Use sun.net.www.http.HttpClient.closeServer() if available.
					Field	f	= con.getClass().getDeclaredField("http");
					f.setAccessible(true);
					Object	client	= f.get(con);
					client.getClass().getMethod("closeServer", new Class[0]).invoke(client, new Object[0]);
					
					System.out.println(System.currentTimeMillis()+": exited");
				}
				catch(Exception e)
				{
					System.out.println(System.currentTimeMillis()+": exception.");
					e.printStackTrace();
				}
			}
		}).start();
		
		while(true)
		{
			// Read message type.
			int	b	= in.read();
			if(b==-1)
			{
				throw new IOException("Stream closed");
			}
			else if(b==SRelay.MSGTYPE_PING)
			{
				System.out.println(System.currentTimeMillis()+": Received ping");
			}
			else if(b==SRelay.MSGTYPE_AWAINFO)
			{
				System.out.println(System.currentTimeMillis()+": Received awa messge");
				HttpReceiver.readMessage(in);
			}
			else if(b==SRelay.MSGTYPE_DEFAULT)
			{
				System.out.println(System.currentTimeMillis()+": Received normal messge");
				HttpReceiver.readMessage(in);
			}
		}
	}
}
