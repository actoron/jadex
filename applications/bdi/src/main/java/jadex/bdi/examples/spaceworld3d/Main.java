package jadex.bdi.examples.spaceworld3d;

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
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()).get();
		CreationInfo ci = new CreationInfo().setFilename("jadex/bdi/examples/spaceworld3d/SpaceWorld3d.application.xml").addArgument("opengl", false);
		platform.createComponent(ci).get();
	}
}
