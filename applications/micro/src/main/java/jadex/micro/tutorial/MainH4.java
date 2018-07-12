package jadex.micro.tutorial;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

/**
 *  Main class for starting the chat
 *  from the command line.
 */
public class MainH4
{
	/**
	 *  Main method starts the platform
	 *  and creates a chat component.
	 */
	public static void main(String[] args)
	{
		// Merge arguments and default arguments.
		String[]	defargs	= new String[]
		{
			"-gui", "false",
			"-welcome", "false",
			"-cli", "false",
			"-printpass", "false"
		};
		String[]	newargs	= new String[defargs.length+args.length];
		System.arraycopy(defargs, 0, newargs, 0, defargs.length);
		System.arraycopy(args, 0, newargs, defargs.length, args.length);
		
		// Start the platform with the arguments.
		IFuture<IExternalAccess>	platfut	= Starter.createPlatform(newargs);
		
		// Wait until the platform has started and retrieve the platform access.
//		ThreadSuspendable	sus	= new ThreadSuspendable();
		IExternalAccess	platform	= platfut.get();
		System.out.println("Started platform: "+platform.getId());
		
		// Get the CMS service from the platform
		IComponentManagementService	cms	= platform.searchService(
			new ServiceQuery<>(IComponentManagementService.class)).get();
		
		// Start the chat component
		IComponentIdentifier	cid	= cms.createComponent(null, ChatD2Agent.class.getName()+".class", null, null).get();
		System.out.println("Started chat component: "+cid);
		
		// Fetch the chat service
		IChatService	chat	= platform.searchService( new ServiceQuery<>(IChatService.class).setProvider(cid)).get();
		chat.message("Main", "Chat started.");

	}
}
