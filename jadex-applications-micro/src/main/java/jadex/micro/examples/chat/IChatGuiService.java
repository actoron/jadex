package jadex.micro.examples.chat;

import jadex.commons.future.IFuture;

public interface IChatGuiService
{
	/**
	 *  Set the user name.
	 */
	public IFuture<Void>	setNickName(String nick);
	
	/**
	 *  Get the user name.
	 */
	public IFuture<String>	getNickName();
	
	/**
	 *  
	 */
}
