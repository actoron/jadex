package jadex.micro.examples.mandelbrot;

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
		//IExternalAccess platform = Starter.createPlatform().get();
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		//CreationInfo ci = new CreationInfo().setFilename("jadex/micro/examples/mandelbrot/Mandelbrot.application.xml");
		//CreationInfo ci = new CreationInfo().setFilenameClass(MandelbrotAgent.class).setConfiguration("all");
		CreationInfo ci = new CreationInfo().setFilenameClass(MandelbrotAgent.class);
		platform.createComponent(ci).get();
	}
}
