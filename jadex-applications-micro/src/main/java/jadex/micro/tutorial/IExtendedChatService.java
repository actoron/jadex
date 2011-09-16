package jadex.micro.tutorial;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IExtendedChatService extends IChatService
{
	/**
	 *  Get the user profile.
	 *  @return The user profile.
	 */
	public IFuture<UserProfileD3> getUserProfile();
}
