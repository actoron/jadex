package jadex.micro.examples.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import javax.swing.SwingUtilities;

/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IExternalAccess agent;
	
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
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				chatpanel = ChatPanel.createGui(agent);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Hear a new message.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String text)
	{
		chatpanel.addMessage(""+IComponentIdentifier.CALLER.get(), text);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ChatService, "+agent.getComponentIdentifier();
	}
}
