package jadex.micro.examples.chat;

import jadex.bridge.IInternalAccess;
import jadex.commons.service.BasicService;

/**
 *  Chat service implementation.
 */
public class ChatService extends BasicService implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The chat gui. */
	protected ChatPanel chatpanel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new helpline service.
	 */
	public ChatService(IInternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IChatService.class, null);
		this.agent = agent;
		this.chatpanel = ChatPanel.createGui(agent.getExternalAccess());
	}
	
	//-------- methods --------
	
	/**
	 *  Hear something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void hear(String name, String text)
	{
		chatpanel.addMessage(name, text);
		
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
