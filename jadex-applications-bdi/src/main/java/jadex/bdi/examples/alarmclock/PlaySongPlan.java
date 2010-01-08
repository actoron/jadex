package jadex.bdi.examples.alarmclock;

import jadex.bdi.runtime.Plan;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.protocol.URLDataSource;

/**
 *  Plan for playing a song.
 */
public class PlaySongPlan extends Plan
{
	//-------- attributes --------

	/** The media player. */
	protected Player	player;

	/** Some counter. */
	protected static int cnt;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		try
		{
			final URL song = (URL)getParameter("song").getValue();
			
			URLDataSource	uds	= new URLDataSource(song)
			{
				public String getContentType()
				{
					// Hack!!! When content type is unknown try some known file types.
					String	ct	= super.getContentType();
					if(ct.equals("content.unknown"))
					{
						String	urlstring	= song.getFile();
//						if(urlstring.endsWith(".mid"))
//							ct	= "audio.mid";
						if(urlstring.endsWith(".mp3"))
						{
							ct = "audio.mpeg";
						}
						else if(urlstring.endsWith(".wav"))
						{
							ct = "audio.x-wav";
						}
						else
						{
							System.out.println("Unknown audio format of file (use mp3/wav): " + urlstring);
						}
					}
					return ct;
				}
			};
			uds.connect();
			
			player	= Manager.createPlayer(uds);
			//Player	player	= Manager.createPlayer(song);
			final SyncResultListener lis = new SyncResultListener();
			player.addControllerListener(new ControllerListener()
			{
				public void controllerUpdate(ControllerEvent event)
				{
					if(event instanceof EndOfMediaEvent)
					{
						try
						{
							lis.resultAvailable(this, null);
						}
						catch(Exception e) {}
					}
				}
			});
			player.start();

			waitForExternalCondition(lis);
		}
		catch(NoPlayerException e)
		{
			e.printStackTrace();
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			fail(e);
			//throw new RuntimeException(sw.toString());
		}
		catch(IOException e)
		{
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			fail(e);
			//throw new RuntimeException(sw.toString());
		}
	}

	/**
	 *  Cleanup, when the user "pressed the button".
	 */
	public void	aborted()
	{
		if(player !=null)
			player.stop();
	}

	/**
	 * The failed method is called on plan failure/abort.
	 */
	public void failed()
	{
		aborted();
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			final URL url = new URL("file", null, "C:\\projects\\jadex\\jadex_v2\\bdi\\applications\\src\\jadex\\bdi\\examples\\alarmclock\\alarm.mp3");

			URLDataSource	uds	= new URLDataSource(url)
			{
				public String getContentType()
				{
					// Hack!!! When content type is unknown try some known file types.
					String	ct	= super.getContentType();
					if(ct.equals("content.unknown"))
					{
						String	urlstring	= url.getFile();
//						if(urlstring.endsWith(".mid"))
//							ct	= "audio.mid";
						if(urlstring.endsWith(".mp3"))
						{
							ct = "audio.mpeg";
						}
						else if(urlstring.endsWith(".wav"))
						{
							ct = "audio.x-wav";
						}
						else
						{
							System.out.println("Unknown audio format of file (use mp3/wav): " + urlstring);
						}
					}
					return ct;
				}
			};
			uds.connect();
			
			Player player	= Manager.createPlayer(uds);
			//Player	player	= Manager.createPlayer(song);
			player.addControllerListener(new ControllerListener()
			{
				public void controllerUpdate(ControllerEvent event)
				{
					if(event instanceof EndOfMediaEvent)
					{
						try
						{
						}
						catch(Exception e) {}
					}
				}
			});
			player.start();

		}
		catch(NoPlayerException e)
		{
			e.printStackTrace();
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			//throw new RuntimeException(sw.toString());
		}
		catch(IOException e)
		{
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			//throw new RuntimeException(sw.toString());
		}
	}
}
