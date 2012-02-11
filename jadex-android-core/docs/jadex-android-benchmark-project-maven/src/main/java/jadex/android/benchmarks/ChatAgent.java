package jadex.android.benchmarks;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.chat.IChatService;

@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class,
	implementation=@Implementation(expression="$component.getPojoAgent()")))
@RequiredServices(@RequiredService(name="chats", type=IChatService.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
@Service
public class ChatAgent	implements IChatService
{
	/**
	 *  Hear a new message.
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String text)
	{
		// Print to console, which gets mirrored in android view.
		System.out.println(IComponentIdentifier.CALLER.get()+": "+text);
		return IFuture.DONE;
	}
}
