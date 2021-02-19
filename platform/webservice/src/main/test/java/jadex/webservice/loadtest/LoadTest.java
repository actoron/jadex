package jadex.webservice.loadtest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *	Test concurrent access to a given web page.
 */
public class LoadTest
{
	//-------- attributes --------
	
	/** The number of accesses to go. */
	protected int	cnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a load test.
	 */
	public LoadTest(final URL url, final int cnt, int threads)
	{
		this.cnt	= cnt;
		final byte[]	buf	= new byte[8192];
		final long	start	= System.currentTimeMillis();
		for(int i=0; i<threads; i++)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						while(true)
						{
							boolean	exit	= false;
							boolean	print	= false;
							synchronized(LoadTest.this)
							{
								print	= LoadTest.this.cnt==1;
								exit	= LoadTest.this.cnt==0;
								LoadTest.this.cnt	= LoadTest.this.cnt>0 ? LoadTest.this.cnt-1 : 0;
							}
							
							if(exit)
							{
								break;
							}
							
							url.openConnection();
							InputStream	is	= url.openStream();
							while(is.read(buf)!=-1)
							{
							}
							is.close();
							
							if(print)
							{
								long	end	= System.currentTimeMillis();
								System.out.println("Took "+(end-start)+" millis for "+cnt+" requests.");
							}
						}
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	/**
	 *  Main for starting.
	 */
	public static void	main(String[] args) throws IOException
	{
//		new LoadTest(new URL("http://www2.activecomponents.org/download/resources/alex-OptiPlex-755.png"), 100000, 500);
		new LoadTest(new URL("http://localhost:8080/eventsystem/receiveRawEvent?arg0=kid+3&mediatype=text%2Fplain"), 10000, 500);
	}
}
