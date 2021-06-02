package jadex.quickstart.cleanerworld;

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
// This file and all other files in same directory are copied to jadex-example-project.zip
// Lines immediately starting with a '//' comment (like this line) are removed for dist
// Lines with 'conf.addComponent' are removed for dist
// After the 'Add your cleaner...' line, an 'addComponent' line is added for the default SimpleCleanerAgent for dist
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
		
//		// Simulation service for switching execution modes.
//		conf.getExtendedPlatformConfiguration().setSimul(true);
		
		// Optional: open JCC for runtime tools.
//		conf.setGui(true);
		
		// Set logging level to provider better debugging output for agents.
		conf.setLoggingLevel(Level.WARNING);

		// Add BDI kernel (required when running BDI agents)
		conf.setValue("kernel_bdi", true);

		// Add your cleaner agent(s)
//		conf.addComponent("jadex/quickstart/cleanerworld/SimpleCleanerAgentZero.class");

//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentA0.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentA1.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentA2.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentA3.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentA4.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentB1.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentB2.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentB3.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentC0.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentC1.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentC2.class");
		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentD1.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentD2.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgentD3a.class");
//		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgent.class");
		
//		conf.addComponent("quickstart/cleanerworld/multi/messaging/SimpleMessagingCleanerAgent.class");
//		conf.addComponent("quickstart/cleanerworld/multi/messaging/SimpleMessagingCleanerAgent.class");
//		conf.addComponent("quickstart/cleanerworld/multi/messaging/SimpleMessagingCleanerAgent.class");
//		conf.addComponent("quickstart/cleanerworld/multi/messaging/SimpleMessagingCleanerAgent.class");

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
