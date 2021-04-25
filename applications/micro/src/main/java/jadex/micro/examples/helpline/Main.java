package jadex.micro.examples.helpline;

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
		CreationInfo ci = new CreationInfo().setFilename("jadex/micro/examples/helpline/Helpline.application.xml");
		platform.createComponent(ci).get();
	}
}
