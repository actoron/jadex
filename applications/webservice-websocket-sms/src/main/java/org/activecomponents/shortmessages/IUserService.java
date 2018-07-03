package org.activecomponents.shortmessages;

import java.util.Collection;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for user management.
 */
@Service
public interface IUserService
{
	/**
	 *  Register a new user.
	 *  @param user The new user.
	 */
	public IFuture<Void> register(User user);
		
	/**
	 *  Login a user.
	 *  @param user The user.
	 *  @return The token for future access.
	 */
	public IFuture<String> login(User user);
	
	/**
	 *  Logout a user.
	 *  @param usertoken The user token.
	 *  @return The token for future access.
	 */
	public IFuture<Boolean> logout(String usertoken);
	
	/**
	 *  Get a user per token.
	 *  @param usertoken The user token.
	 *  @return The user or null if none.
	 */
	public IFuture<User> getUser(String usertoken);
	
	/**
	 *  Get a user per email.
	 *  @param email The user email.
	 *  @return The user.
	 */
	public IFuture<User> getUserByEmail(String email);
	
	/**
	 *  Add a new follower.
	 *  @param user The user.
	 *  @param follower The follower.
	 *  @return True if changed.
	 */
	public IFuture<Boolean> addFollower(String usertoken, User follower);
	
	/**
	 *  Remove a follower.
	 *  @param user The user.
	 *  @param follower The follower.
	 *  @return True if changed.
	 */
	public IFuture<Boolean> removeFollower(String usertoken, User follower);
	
	/**
	 *  Get the current followers.
	 *  @param user The user.
	 *  @return The followers.
	 */
	public IFuture<Collection<User>> getFollowers(String usertoken);
	
	/**
	 *  Add a new followed user.
	 *  @param user The user.
	 *  @param followed The followed user.
	 */
	public IFuture<Boolean> addFollowed(String usertoken, User followed);
	
	/**
	 *  Remove a followed user.
	 *  @param followed The followed user.
	 */
	public IFuture<Boolean> removeFollowed(String usertoken, User followed);
	
	/**
	 *  Get the current people that I follow.
	 *  @param usertoken The user token.
	 *  @return The users this user follows.
	 */
	public IFuture<Collection<User>> getFolloweds(String usertoken);

	/**
	 *  Get all users.
	 *  @return All users.
	 */
	public IFuture<Collection<User>> getAllUsers(String usertoken);
}
