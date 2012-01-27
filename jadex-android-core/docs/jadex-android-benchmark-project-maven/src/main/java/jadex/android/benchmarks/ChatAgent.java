package jadex.android.benchmarks;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.examples.chat.IChatService;

@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class,
	implementation=@Implementation(expression="$component.getPojoAgent()")))
@Service
public class ChatAgent	implements IChatService
{
	/**
	 *  Hear a new message.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	public void hear(String name, String text)
	{
		// Print to console, which gets mirrored in android view.
		System.out.println(name+": "+text);
	}
}
