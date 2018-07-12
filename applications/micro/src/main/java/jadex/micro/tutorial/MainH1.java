package jadex.micro.tutorial;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Main class for starting the chat
 *  from the command line.
 */
public class MainH1
{
	/**
	 *  Main method starts the platform
	 *  and creates a chat component.
	 */
	public static void main(String[] args)
	{
		// Start the platform with the arguments.
		IFuture<IExternalAccess>	platfut	= Starter.createPlatform(args);
		
		// Wait until the platform has started and retrieve the platform access.
		IExternalAccess	platform	= platfut.get();
		System.out.println("Started platform: "+platform.getId());
	}
}
