package jadex.micro.tutorial;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class ChatServiceD3 extends ChatServiceD1 implements IExtendedChatService
{
	protected static List profiles;

	protected UserProfileD3 profile;
	
	/**
	 * 
	 */
	public ChatServiceD3()
	{
		this.profile = (UserProfileD3)profiles.get((int)(Math.random()*profiles.size()));
	}
	
	static
	{
		profiles = new ArrayList();
		profiles.add(new UserProfileD3("John Doh", 33, false, "I like football, dart and beer."));
		profiles.add(new UserProfileD3("Anna Belle", 21, true, "I like classic music."));
		profiles.add(new UserProfileD3("Prof. Smith", 58, false, "I like Phdcomics."));
	}
	
	/**
	 *  Get the user profile.
	 *  @return The user profile.
	 */
	public IFuture getUserProfile()
	{
		return new Future(profile);
	}
}
