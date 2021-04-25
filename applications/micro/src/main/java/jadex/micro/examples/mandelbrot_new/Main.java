package jadex.micro.examples.mandelbrot_new;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
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
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()
//			.setLogging(true)
//			.setValue("security.debug", true)
			, args
		).get();
//		CreationInfo ci = new CreationInfo().setFilenameClass(MandelbrotAgent.class).setConfiguration("nocalcs");
		CreationInfo ci = new CreationInfo().setFilenameClass(MandelbrotAgent.class).setConfiguration("pools");
		//ci.addArgument("scope", ServiceScope.NETWORK);
		platform.createComponent(ci).get();
	}
}
