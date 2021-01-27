package jadex.bdiv3.examples.booktrading;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 *  Main for starting the example programmatically.
 *  
 *  To start the example via this Main.java Jadex platform 
 *  as well as examples must be in classpath.
 */
public class Main 
{
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		CreationInfo ci = new CreationInfo().setFilename("jadex/bdiv3/examples/booktrading/BookTradingServices.application.xml");
		platform.createComponent(ci).get();
	}

//	/**
//	 *  Main for reproducing termination heisenbug.
//	 */
//	public static void main(String[] args)
//	{
//		IPlatformConfiguration	config	= PlatformConfigurationHandler.getDefaultNoGui();
//		config.setDefaultTimeout(300000);
//		CreationInfo ci = new CreationInfo().setFilename("jadex/bdiv3/examples/booktrading/BookTradingServices.application.xml");
//		
//		IExternalAccess	platform	= Starter.createPlatform(config).get();
//		
//		while(true)
//		{
//			List<IFuture<IExternalAccess>>	sellers	= new ArrayList<>();
//			
//			// Start many agents
//			for(int i=0; i<10; i++)
//			{
//				sellers.add(platform.createComponent(ci));				
//			}
//			
//			// Wait for all agents started
//			sellers.stream().forEach(seller -> seller.get());
//			
//			// Kill all agents
//			sellers.stream().forEach(seller -> seller.get().killComponent());
//			
//			// Wait for all agents killed
//			sellers.stream().forEach(seller ->
//			{
//				try
//				{
//					seller.get().killComponent().get();
//				}
//				catch(ComponentTerminatedException e)
//				{
//					// Only interested in timeout exception
//				}
//			});
//		}
//	}
}
