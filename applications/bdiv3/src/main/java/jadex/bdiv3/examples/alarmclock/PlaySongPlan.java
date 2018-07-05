package jadex.bdiv3.examples.alarmclock;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.alarmclock.AlarmclockBDI.PlaySongGoal;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;


/**
 *  Plan for playing a song.
 */
@Plan
public class PlaySongPlan
{
	//-------- attributes --------

	/** The media player. */
	protected Player	player;
	
	/** The plan reason. */
	@PlanReason
	protected PlaySongGoal	goal;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body(IInternalAccess agent)
	{
		final URL song = (URL)goal.getSong();
		IThreadPoolService tp = (IThreadPoolService)agent.getFeature(IRequiredServicesFeature.class).getService("tpservice").get();
		final ClassLoader cl = agent.getClassLoader();
		
		final Future<Void>	fut	= new Future<Void>();
		tp.execute(new Runnable()
		{
			public void run()
			{
				InputStream in = null;
				if (song == null)
					fut.setException(new IllegalArgumentException("No song specified."));
				else
				{
					try
					{
						in = new BufferedInputStream(song.openStream());
					}
					catch(Exception e)
					{
						try
						{
							in = SUtil.getResource(song.getPath(), cl);
						}
						catch(Exception ex)
						{
							fut.setException(e);
						}
					}
						
					if(in!=null)
					{
						try
						{
							AudioDevice dev = FactoryRegistry.systemRegistry().createAudioDevice();
							player = new Player(in, dev);
							player.play();
							fut.setResultIfUndone(null);	// Already set by interpreter, when plan aborted while waiting.
						}
						catch(Exception e)
						{
	//						e.printStackTrace();
							fut.setExceptionIfUndone(e);	// Already set by interpreter, when plan aborted while waiting.
						}
					}
				}
			}
		});
			
		try
		{
			fut.get();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new PlanFailureException();
		}
	}

	/**
	 *  Cleanup, when the user "pressed the button".
	 */
	@PlanAborted
	@PlanFailed
	public void	aborted()
	{
		if(player!=null)
		{
			player.close();
		}
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		try
		{
			final URL song = new URL("file", null, "C:\\projects\\jadex_v2\\jadex-applications-bdi\\src\\main\\java\\jadex\\bdi\\examples\\alarmclock\\alarm.mp3");
			InputStream in = new BufferedInputStream(song.openStream());
			AudioDevice dev = FactoryRegistry.systemRegistry().createAudioDevice();
			Player player = new Player(in, dev);
			player.play();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
