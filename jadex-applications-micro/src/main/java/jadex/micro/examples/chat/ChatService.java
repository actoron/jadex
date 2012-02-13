package jadex.micro.examples.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Binding;


/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The chat gui. */
	protected ChatPanel chatpanel;
	
	//-------- methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	exta	= agent.getExternalAccess();
		agent.getServiceContainer().searchService(IClockService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(final IClockService clock)
			{
				ChatPanel.createGui(exta, clock)
					.addResultListener(new ExceptionDelegationResultListener<ChatPanel, Void>(ret)
				{
					public void customResultAvailable(ChatPanel result)
					{
						chatpanel = result;
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		chatpanel.dispose();
		
		final Future<Void>	ret	= new Future<Void>();
		// Todo: required services don't work in service shutdown!?
		IIntermediateFuture<IChatService>	chatfut	= SServiceProvider.getServices(agent.getServiceContainer(), IChatService.class, Binding.SCOPE_GLOBAL);
//		IIntermediateFuture<IChatService>	chatfut	= agent.getServiceContainer().getRequiredServices("chatservices");
		chatfut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(IChatService chat)
			{
				// Hack!!! change local id from rms to chat agent.
				IComponentIdentifier id	= IComponentIdentifier.LOCAL.get();
				IComponentIdentifier.LOCAL.set(agent.getComponentIdentifier());
				chat.status(STATE_DEAD);
				IComponentIdentifier.LOCAL.set(id);
			}
			public void finished()
			{
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Post a message
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String text)
	{
		chatpanel.addMessage(IComponentIdentifier.CALLER.get(), text);
		return IFuture.DONE;
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IFuture<Void>	status(String status)
	{
		chatpanel.setUserState(IComponentIdentifier.CALLER.get(), status);
		return IFuture.DONE;		
	}
}
