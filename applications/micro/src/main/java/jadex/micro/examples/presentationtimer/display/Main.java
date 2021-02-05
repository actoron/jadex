package jadex.micro.examples.presentationtimer.display;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;

/**
 *  Starts the presentation timer.
 *  Allows for tracking the time of a presentation and warns
 *  in two phases when time runs low.
 *  First phase: display turns yellow
 *  Second phase: display turns red
 *  Time runs out: display blinks red
 */
public class Main
{
	public static boolean startedWithMain;

	public static void main(String[] args)
	{
		startedWithMain = true;

		Main main = new Main();
		main.init();

		new Thread()
		{
			Robot robot = null;

			public void run()
			{
				while(true)
				{
					try
					{
						if(robot == null)
							robot = new Robot();
						Thread.sleep(60 * 1000);
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyRelease(KeyEvent.VK_SHIFT);
						Thread.sleep(50);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					catch(AWTException e)
					{
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public void init()
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();

		config.setPlatformName("presentationtimer-*");

		config.setNetworkNames(new String[] { "jadexnetwork" });
		config.setNetworkSecrets(new String[] { "laxlax" });

		// config.setDht(true);

		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

		fut.then(access -> {
			IFuture<IExternalAccess> fut2 = access.createComponent(new CreationInfo().setName("CDDisplay").setFilename(CountdownAgent.class.getName() + ".class"));
			fut2.then((IExternalAccess created) -> 
			{
				System.out.println("CDDisplay Component created.");
				created.waitForTermination().then((Map<String, Object> terminated) -> {
					System.out.println("CDDisplay Component terminated!");
				});
			});
		});

	}
}
