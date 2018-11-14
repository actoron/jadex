package jadex.micro.tutorial;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat bot replies to selected messages. 
 */
@Description("This agent replies to selected messages.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(ChatServiceF4.class)))
@RequiredServices({
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true, scope=ServiceScope.PLATFORM)
})
@Arguments({
	@Argument(name="keyword", clazz=String.class, defaultvalue="\"nerd\"", description="The keyword to react to."),
	@Argument(name="reply", clazz=String.class, defaultvalue="\"Watch your language\"", description="The reply message.")
})
@GuiClass(BotGuiF4.class)
public class ChatBotF4Agent
{
	//-------- attributes --------
	
	/** The keyword to react to. */
	@AgentArgument
	protected String keyword;
	
	/** The reply message. */
	@AgentArgument
	protected String reply;

	//-------- methods --------
	
	/**
	 *  Get the keyword.
	 *  @return	The keyword.
	 */
	public String getKeyword()
	{
		return keyword;
	}
	
	/**
	 *  Set the keyword.
	 *  @param keyword	The keyword.
	 */
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	/**
	 *  Get the reply message.
	 *  @return	The reply message.
	 */
	public String getReply()
	{
		return reply;
	}

	/**
	 *  Set the reply message.
	 *  @param reply	The reply message.
	 */
	public void setReply(String reply)
	{
		this.reply = reply;
	}	
}