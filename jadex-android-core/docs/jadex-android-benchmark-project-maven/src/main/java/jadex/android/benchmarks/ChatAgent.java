package jadex.android.benchmarks;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.ICommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.chat.IChatService;

@Agent
@Arguments(@Argument(name="toastcmd", clazz=ICommand.class))
@ProvidedServices(@ProvidedService(type=IChatService.class,
	implementation=@Implementation(expression="$component.getPojoAgent()")))
@RequiredServices(@RequiredService(name="chats", type=IChatService.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_GLOBAL, dynamic=true)))
@Service
public class ChatAgent	implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The command argument. */
	@AgentArgument
	protected ICommand	toastcmd;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void>	startup()
	{
		final Future<Void>	ret	= new Future<Void>();
		IIntermediateFuture<IChatService>	chats	= agent.getServiceContainer().getRequiredServices("chats");
		chats.addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(IChatService chat)
			{
				toastcmd.execute("User "+((IService)chat).getServiceIdentifier().getProviderId()+" is online.");
				chat.status(STATE_IDLE);
			}
			public void finished()
			{
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	shutdown()
	{
		final Future<Void>	ret	= new Future<Void>();
		IIntermediateFuture<IChatService>	chats	= agent.getServiceContainer().getRequiredServices("chats");
		chats.addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(IChatService chat)
			{
				chat.status(STATE_DEAD);
			}
			public void finished()
			{
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	//-------- IChatService interface --------
	
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

	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IFuture<Void>	status(String status)
	{
		if(STATE_IDLE.equals(status))
		{
			toastcmd.execute("User "+IComponentIdentifier.CALLER.get()+" is online.");
		}
		else if(STATE_TYPING.equals(status))
		{
			toastcmd.execute("User "+IComponentIdentifier.CALLER.get()+" is typing.");
		}
		if(STATE_DEAD.equals(status))
		{
			toastcmd.execute("User "+IComponentIdentifier.CALLER.get()+" is offline.");
		}
		return IFuture.DONE;
	}
}
