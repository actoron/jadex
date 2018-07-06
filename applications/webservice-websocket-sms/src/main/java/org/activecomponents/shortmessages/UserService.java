package org.activecomponents.shortmessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Class for user management.
 */
@Service
public class UserService implements IUserService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The users. */
	protected Map<User, User> users;
	
	/** The followers (that follow the user). */
	protected Map<User, Set<User>> followers;
	
	/** The followed people (that the user follows). */
	protected Map<User, Set<User>> followeds;
	
	/** The active sessions - logged in users. */
	protected Map<String, User> sessions;
	
	/**
	 *  Create a new user service.
	 */
	public UserService()
	{
		this.users = new HashMap<User, User>();
		this.followers = new HashMap<User, Set<User>>();
		this.followeds = new HashMap<User, Set<User>>();
		this.sessions = new HashMap<String, User>();
		
		User lars = new User("Lars", "lars@example.com", "1234");
		User hans = new User("Hans", "hans@example.com", "1234");
		User franz = new User("Franz", "franz@example.com", "1234");
		User dirk = new User("Dirk", "dirk@example.com", "1234");

		registerUser(lars);
		registerUser(hans);
		registerUser(franz);
		registerUser(dirk);

		addFollower(lars, hans);
		addFollower(lars, franz);
		
		addFollowed(lars, dirk);
		addFollowed(lars, hans);
		
//		System.out.println("f: "+followers);
//		System.out.println("fb: "+followeds);
	}
	
	/**
	 *  Register a new user.
	 *  @param user The new user.
	 */
	public IFuture<Void> register(final User user)
	{
		Future<Void> ret = new Future<Void>();
		try
		{
			registerUser(user);
			
			Collection<User> recs = new HashSet<User>(users.values());
			recs.remove(user);
			ShortMessageAgent.notifyClients(component, recs, new IResultCommand<IFuture<Void>, IClientService>()
			{
				public IFuture<Void> execute(IClientService service)
				{
					return service.userAdded(processForClient(user));
				}
			}).addResultListener(new DelegationResultListener<Void>(ret));
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
		
//		if(!users.containsKey(user))
//		{
//			users.put(user, user);
//			return IFuture.DONE;
//		}
//		else
//		{
//			return new Future<Void>(new RuntimeException("User already contained: "+user));
//		}
	}
	
	/**
	 *  Register a new user.
	 *  @param user The new user.
	 */
	protected void registerUser(final User user)
	{
		if(!users.containsKey(user))
		{
			if(user.getName()==null || user.getName().length()==0
				|| user.getEmail()==null || user.getEmail().length()==0
				|| user.getPassword()==null ||user.getPassword().length()==0)
			{
				throw new IllegalArgumentException("User data must not be null: "+user);
			}
			else
			{
				users.put(user, user);
			}
		}
		else
		{
			throw new RuntimeException("User already contained: "+user)
			{
				@Override
				public void printStackTrace()
				{
					super.printStackTrace();
				}
			};
		}
	}
		
	/**
	 *  Login a user.
	 *  @param user The user.
	 */
	public IFuture<String> login(final User user)
	{
		final Future<String> ret = new Future<String>();
		
		User saved = users.get(user);
		
		if(saved!=null && saved.getPassword().equals(user.getPassword()))
		{
			final String token = SUtil.createUniqueId(saved.getName());
			sessions.put(token, saved);
			
			notifyOnlineState(saved, true).addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
			{
				public void customResultAvailable(Void result) throws Exception
				{
					ret.setResult(token);
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("Login failed"));
		}
		
		return ret;
	}
	
	/**
	 *  Logout a user.
	 *  @param usertoken The user token.
	 *  @return The token for future access.
	 */
	public IFuture<Boolean> logout(String usertoken)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		User user = sessions.remove(usertoken);
		
		if(user!=null)
		{
			notifyOnlineState(user, false).addResultListener(new ExceptionDelegationResultListener<Void, Boolean>(ret)
			{
				public void customResultAvailable(Void result) throws Exception
				{
					ret.setResult(true);
				}
			});
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 *  Get a user per token.
	 *  @param usertoken The user token.
	 *  @return The user or null if none.
	 */
	public IFuture<User> getUser(String usertoken)
	{
		return sessions.containsKey(usertoken)? 
			new Future<User>(processForClient(sessions.get(usertoken))): 
			new Future<User>(new RuntimeException("No user session: "+usertoken));
	}
	
	/**
	 *  Get a user per email.
	 *  @param email The user email.
	 *  @return The user.
	 */
	public IFuture<User> getUserByEmail(String email)
	{
		User user = internalGetUserByEmail(email);
		return user!=null? new Future<User>(processForClient(user)): new Future<User>(new IllegalArgumentException("Unknown user: "+email));
	}
	
	/**
	 *  Get a user per email
	 *  @param email The email.
	 *  @return The user or null.
	 */
	protected User internalGetUserByEmail(String email)
	{
		return users.get(new User(null, email, null));
	}
	
	/**
	 *  Get all users.
	 *  @return All users.
	 */
	public IFuture<Collection<User>> getAllUsers(String usertoken)
	{
		if(!sessions.containsKey(usertoken))
			return new Future<Collection<User>>(new RuntimeException("Not logged in."));
		return new Future<Collection<User>>(processForClient(users.values()));
	}
	
	/**
	 *  Add a new follower.
	 *  @param user The user.
	 *  @param follower The follower.
	 *  @return True if changed.
	 */
	public IFuture<Boolean> addFollower(String usertoken, User follower)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		final User ffollower = internalGetUserByEmail(follower.getEmail());
		if(ffollower==null)
		{
			ret.setException(new IllegalArgumentException("Unknown user: "+follower));
		}
		else
		{
			getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Boolean>(ret)
			{
				public void customResultAvailable(User user) throws Exception
				{
					final boolean res = addFollower(user, ffollower);
					ret.setResult(res);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Add a follower.
	 *  @param user
	 *  @param follower
	 *  @return
	 */
	protected boolean addFollower(User user, User follower)
	{
		boolean res = false;
		Set<User> fs = followers.get(user);
		if(fs==null)
		{
			fs = new HashSet<User>();
			followers.put(user, fs);
		}
		res = fs.add(follower);
		
		Set<User> fds = followeds.get(follower);
		if(fds==null)
		{
			fds = new HashSet<User>();
			followeds.put(follower, fds);
		}
		fds.add(user);
	
		return res;
	}
	
	/**
	 *  Remove a follower.
	 *  @param user The user.
	 *  @param follower The follower.
	 *  @return True if changed.
	 */
	public IFuture<Boolean> removeFollower(String usertoken, User follower)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		final User ffollower = internalGetUserByEmail(follower.getEmail());
		if(ffollower==null)
		{
			ret.setException(new IllegalArgumentException("Unknown user: "+follower));
		}
		else
		{
			getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Boolean>(ret)
			{
				public void customResultAvailable(User user) throws Exception
				{
					boolean res = false;
					
					Set<User> fs = followers.get(user);
					if(fs!=null)
						res = fs.remove(ffollower);
					
					Set<User> fds = followeds.get(ffollower);
					if(fds!=null)
						fds.remove(user);
					
					ret.setResult(res);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a follower.
	 *  @param user
	 *  @param follower
	 *  @return
	 */
	protected boolean removeFollower(User user, final User follower)
	{
		boolean res = false;
		
		Set<User> fs = followers.get(user);
		if(fs!=null)
			res = fs.remove(follower);
		
		Set<User> fds = followeds.get(follower);
		if(fds!=null)
			fds.remove(user);
		
		return res;
	}
	
	/**
	 *  Get the current followers.
	 *  @param user The user.
	 *  @return The followers.
	 */
	public IFuture<Collection<User>> getFollowers(String usertoken)
	{
		final Future<Collection<User>> ret = new Future<Collection<User>>();
		
		getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Collection<User>>(ret)
		{
			public void customResultAvailable(User user) throws Exception
			{
				ret.setResult(processForClient((Collection)followers.get(user)));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a new followed user.
	 *  @param user The user.
	 *  @param followed The followed user.
	 */
	public IFuture<Boolean> addFollowed(String usertoken, User followed)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		final User ffollowed = internalGetUserByEmail(followed.getEmail());
		if(ffollowed==null)
		{
			ret.setException(new IllegalArgumentException("Unknown user: "+followed));
		}
		else
		{
			getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Boolean>(ret)
			{
				public void customResultAvailable(User user) throws Exception
				{
					final boolean res = addFollower(ffollowed, user);
					
					if(res)
					{
						ShortMessageAgent.notifyClient(component, ffollowed, new IResultCommand<IFuture<Void>, IClientService>()
						{
							public IFuture<Void> execute(IClientService service)
							{
								return service.followersChanged(processForClient(followers.get(ffollowed)));
							}
						}).addResultListener(new ExceptionDelegationResultListener<Void, Boolean>(ret)
						{
							public void customResultAvailable(Void result) throws Exception
							{
								ret.setResult(res);
							}
							
						});
					}
					else
					{
						ret.setResult(Boolean.FALSE);
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Add a followed by person.
	 *  @param user
	 *  @param followed
	 *  @return
	 */
	protected boolean addFollowed(User user, User followed)
	{
		return addFollower(followed, user);
	}
	
	/**
	 *  Remove a followed user.
	 *  @param followed The followed user.
	 */
	public IFuture<Boolean> removeFollowed(String usertoken, User followed)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		final User ffollowed = internalGetUserByEmail(followed.getEmail());
		if(ffollowed==null)
		{
			ret.setException(new IllegalArgumentException("Unknown user: "+followed));
		}
		else
		{
			getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Boolean>(ret)
			{
				public void customResultAvailable(User user) throws Exception
				{
					final boolean res = removeFollower(ffollowed, user);
					
					if(res)
					{
						ShortMessageAgent.notifyClient(component, ffollowed, new IResultCommand<IFuture<Void>, IClientService>()
						{
							public IFuture<Void> execute(IClientService service)
							{
								return service.followersChanged(processForClient(followers.get(ffollowed)));
							}
						}).addResultListener(new ExceptionDelegationResultListener<Void, Boolean>(ret)
						{
							public void customResultAvailable(Void result) throws Exception
							{
								ret.setResult(res);
							}
							
						});
					}
					else
					{
						ret.setResult(Boolean.FALSE);
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get the current followers.
	 *  @param followed The followed user.
	 *  @return The user that follow this user.
	 */
	public IFuture<Collection<User>> getFolloweds(String usertoken)
	{
		final Future<Collection<User>> ret = new Future<Collection<User>>();
		
		getUser(usertoken).addResultListener(new ExceptionDelegationResultListener<User, Collection<User>>(ret)
		{
			public void customResultAvailable(User user) throws Exception
			{
//				System.out.println("followeds of: "+user+" "+followeds.get(user));
				ret.setResult(processForClient(followeds.get(user)));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test if a user is online.
	 *  @param user The user.
	 *  @return True, if is currently online.
	 */
	protected boolean isOnline(User user)
	{
		// todo: index, this is a linear lookup
		return sessions.containsValue(user);
	}
	
	/**
	 *  Notify all connected users of a specific online state.
	 */
	protected IFuture<Void> notifyOnlineState(final User user, final boolean online)
	{
		final Future<Void> ret = new Future<Void>();
		
		Collection<User> tonotify = new HashSet<User>();
		
		// Notify all followers / followeds that user is online
		for(Map.Entry<User, Set<User>> fol: followers.entrySet())
		{
			if(fol.getValue()!=null && fol.getValue().contains(user))
			{
				tonotify.add(fol.getKey());
			}
		}
		for(Map.Entry<User, Set<User>> fol: followeds.entrySet())
		{
			if(fol.getValue()!=null && fol.getValue().contains(user))
			{
				tonotify.add(fol.getKey());
			}
		}
		
		if(tonotify.size()>0)
		{
			ShortMessageAgent.notifyClients(component, tonotify, new IResultCommand<IFuture<Void>, IClientService>()
			{
				public IFuture<Void> execute(IClientService service)
				{
					return service.onlineStateChanged(processForClient(user));
				}
			}).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Process the user objects before sending to the client.
	 *  - Erases password
	 *  - Sets online state.
	 *  @param coll The collection.
	 *  @return The processed collection.
	 */
	protected Collection<User> processForClient(Collection<User> coll)
	{
		Collection<User> ret = new HashSet<User>();
	
		for(User user: coll)
		{
			ret.add(processForClient(user));
		}
		
		return ret;
	}
	
	/**
	 *  Process the user objects before sending to the client.
	 *  - Erases password
	 *  - Sets online state.
	 *  @param coll The collection.
	 *  @return The processed collection.
	 */
	protected User processForClient(User user)
	{
		User ret = new User(user.getName(), user.getEmail(), null);
		ret.setOnline(isOnline(user));
		return ret;
	}
}
