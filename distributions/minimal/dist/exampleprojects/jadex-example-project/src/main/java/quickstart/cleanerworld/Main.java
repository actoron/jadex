package quickstart.cleanerworld;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.future.IFuture;
import jadex.quickstart.cleanerworld.gui.EnvironmentGui;

/**
 *  Main class for starting a cleanerworld scenario
 */
public class Main
{
	/** Use higher values (e.g. 2.0) for faster cleaner movement and lower values (e.g. 0.5) for slower movement. */
	protected static double	CLOCK_SPEED	= 1;
	
	/**
	 *  Main method for starting the scenario.
	 *  @param args	ignored for now.
	 */
	public static void main(String[] args)
	{
		// Start from minimal configuration
		IPlatformConfiguration	conf	= PlatformConfigurationHandler.getMinimal();
		
		// Simulation service for switching execution modes.
		conf.getExtendedPlatformConfiguration().setSimul(true);
		
		// Optional: open JCC for runtime tools.
//		conf.setGui(true);
		
		// Set logging level to provider better debugging output for agents.
		conf.setLoggingLevel(Level.WARNING);

		// Add BDI kernel (required when running BDI agents)
		conf.setValue("kernel_bdi", true);
        
        // Add your cleaner agent(s)
		conf.addComponent("quickstart/cleanerworld/SimpleCleanerAgent.class");

		// Start a Jadex platform (asynchronously in background).
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(conf);
		
		// IFuture.get() will block until background startup is complete.
		// Without this, errors might not get shown.
		fut.get();
		
		// Apply the chosen clock speed.
		IClockService	cs	= fut.get().searchService(new ServiceQuery<>(IClockService.class)).get();
		cs.setClock(IClock.TYPE_CONTINUOUS, fut.get().searchService(new ServiceQuery<>(IThreadPoolService.class)).get());
		cs.setDilation(CLOCK_SPEED);

		// Open world view window on Swing Thread
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new EnvironmentGui().setVisible(true);
			}
		});
	}
}
