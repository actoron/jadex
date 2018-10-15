package jadex.quickstart.cleanerworld;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.quickstart.cleanerworld.gui.EnvironmentGui;

/**
 *  Main class for starting a cleanerworld scenario
 */
public class Main
{
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
		conf.addComponent("jadex/quickstart/cleanerworld/single/CleanerBDIAgent.class");

		// Start a Jadex platform (asynchronously in background).
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(conf);
		
		// IFuture.get() will block until background startup is complete.
		// Without this, errors might not get shown.
		fut.get();
		
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
