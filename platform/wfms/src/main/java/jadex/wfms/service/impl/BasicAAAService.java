package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.standard.SCapReqs;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.listeners.IAuthenticationListener;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** 
 *	Basic Authentication, Access control and Accounting Service.
 */
@Service
public class BasicAAAService implements IAAAService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	protected Map<IComponentIdentifier, ClientInfo> clientInfos;
	
	protected Map<String, UserAAAEntry> users;
	
	protected Map<String, Set<Integer>> secRoleCaps;
	
	protected Set authenticationListeners;
	
	public static IAAAService getTestService()
	{
		Map secRoles = new HashMap();
		Set userNoStartCaps = new HashSet();
		userNoStartCaps.addAll(SCapReqs.ACTIVITY_HANDLING);
		userNoStartCaps.addAll(SCapReqs.WORKITEM_LIST);
		Set userCaps = new HashSet(userNoStartCaps);
		userCaps.addAll(SCapReqs.PROCESS_LIST);
		secRoles.put("User", userCaps);
		secRoles.put("User_No_Start", userNoStartCaps);
		UserAAAEntry userNoStart = new UserAAAEntry("TestUserNoStart", new String[] {IAAAService.ALL_ROLES}, new String[] {"User_No_Start"});
		UserAAAEntry user = new UserAAAEntry("TestUser", new String[] {IAAAService.ALL_ROLES}, new String[] {"User"});
		UserAAAEntry admin = new UserAAAEntry("TestAdmin", new String[] {IAAAService.ALL_ROLES}, new String[] {"Administrator"});
		UserAAAEntry bankTeller = new UserAAAEntry("BankTellerUser", new String[] {"Bank Teller"}, new String[] {"User"});
		return new BasicAAAService(new UserAAAEntry[] { admin, user, userNoStart, bankTeller }, secRoles);
	}
	
	public BasicAAAService(UserAAAEntry[] users, Map secrolecaps)
	{
		this.authenticationListeners = new HashSet();
		this.users = new HashMap<String, UserAAAEntry>();
		for (int i = 0; i < users.length; ++i)
			this.users.put(users[i].getUserName(), users[i]);
		
		this.secRoleCaps = secrolecaps!=null? secrolecaps: new HashMap<String, Set<Integer>>();
		secRoleCaps.put(IAAAService.SEC_ROLE_NONE, new HashSet<Integer>());
		secRoleCaps.put(IAAAService.SEC_ROLE_ADMIN, new HashSet<Integer>(IAAAService.CAPABILITIES));
		
		clientInfos = new HashMap<IComponentIdentifier, ClientInfo>();
	}
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture<Void> authenticate(final IComponentIdentifier client, final ClientInfo info)
	{
		final Future ret = new Future<Void>();
		if (client != null)
		{
			ia.getServiceContainer().getRequiredServices("aaa_services").addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<Collection<Object>, Boolean>(ret)
			{
				public void customResultAvailable(Collection<Object> result)
				{
					final List<IAAAService> aaaservices = new ArrayList<IAAAService>();
					for (Object obj : result)
					{
						IAAAService service = (IAAAService) obj;
						aaaservices.add(service);
					}
					
					IResultListener<Boolean> rl = ia.createResultListener(new DelegationResultListener<Boolean>(ret)
					{
						public void customResultAvailable(Boolean result)
						{
							if (Boolean.TRUE.equals(result))
							{
								ret.setResult(null);
								System.out.println("Authenticated: " + info.getUserName());
								clientInfos.put(client, info);
								fireAuthenticationEvent(client, info);
							}
							else
							{
								if (!aaaservices.isEmpty())
								{
									IAAAService aaaservice = aaaservices.remove(0);
									aaaservice.canAuthenticate(info).addResultListener(this);
								}
								else
								{
									ret.setException(new SecurityException("User " + info.getUserName() + " authentication failed."));
								}
							}
						}
					});
					IAAAService aaaservice = aaaservices.remove(0);
					aaaservice.canAuthenticate(info).addResultListener(rl);
				}
			}));
		}
		else
		{
			ret.setException(new NullPointerException("Authentication failed: Client must not be null!"));
		}
		
		return ret;
	}
	
	/**
	 * Tests if a client is allowed to authenticate.
	 * @param info The client info.
	 * @return true, if the client can be authenticated.
	 */
	public IFuture<Boolean> canAuthenticate(ClientInfo info)
	{
		if (users.containsKey(info.getUserName()))
		{
			return new Future(Boolean.TRUE);
		}
		return new Future<Boolean>(Boolean.FALSE);
	}
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 * @return True, when done.
	 */
	public IFuture<Void> deauthenticate(IComponentIdentifier client)
	{
		ClientInfo info = clientInfos.remove(client);
		if (info != null)
		{
			fireDeauthenticationEvent(client, info);
		}
		return IFuture.DONE;
	}
	
	/**
	 * Returns the authenticated clients for a specific user name
	 * @parameter userName the user name
	 * @return Set of connected clients
	 */
	public IFuture<Set<IComponentIdentifier>> getAuthenticatedClients(String userName)
	{
		HashSet<IComponentIdentifier> ret = new HashSet<IComponentIdentifier>();
		for (Iterator<Map.Entry<IComponentIdentifier, ClientInfo>> it = clientInfos.entrySet().iterator();
			 it.hasNext(); )
		{
			Map.Entry<IComponentIdentifier, ClientInfo> entry = it.next();
			if (entry.getValue().getUserName().equals(userName))
				ret.add(entry.getKey());
		}
		return new Future<Set<IComponentIdentifier>>(ret);
	}
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public IFuture<Void> accessAction(IComponentIdentifier client, final Integer action)
	{
		final Future<Void> ret = new Future<Void>();
		ClientInfo info = clientInfos.get(client);
		if (info != null)
		{
			getCapabilities(((UserAAAEntry) users.get(info.getUserName())).getSecRoles()).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					Set secroles = (Set) result;
					if (secroles.contains(action))
					{
						ret.setResult(null);
					}
				}
			}));
		}
		else
		{
			ret.setException(new AccessControlException("Not allowed: "+client + " " + action));
		}
		
		return ret;
	}
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	//public boolean accessEvent(IComponentIdentifier client, Object event)
	//{
		//return true;
		//TODO: FIXME
		/*if (!authenticatedClients.contains(client))
			return false;
		if (event instanceof WorkitemQueueChangeEvent)
		{
			WorkitemQueueChangeEvent evt = (WorkitemQueueChangeEvent) event;
			Set roles = getRoles(client);
			if ((roles.contains(evt.getWorkitem().getRole())) || (roles.contains(IAAAService.ALL_ROLES)))
				return true;
		}
		return false;*/
	//}
	
	/**
	 * Adds a new user to the service.
	 * @param user the new user entry
	 */
	public void addUser(UserAAAEntry user)
	{
		users.put(user.getUserName(), user);
	}
	
	/**
	 * Removes a user from the service.
	 * @param userName user name of the user
	 */
	public void removeUser(String userName)
	{
		users.remove(userName);
	}
	
	/** Returns the user name of a client.
	 *  @param client the client
	 *  @return user name
	 */
	public IFuture<String> getUserName(IComponentIdentifier client)
	{
		Future<String> ret = new Future<String>();
		ClientInfo info = clientInfos.get(client);
		if (info != null)
		{
			ret.setResult(info.getUserName());
		}
		else
		{
			ret.setException(new RuntimeException("No user found for " + String.valueOf(client)));
		}
		return ret;
	}
	
	/**
	 * Returns the roles of a particular user
	 * @param username the user name
	 * @return the roles of the client
	 */
	public IFuture<Set<String>> getRoles(String username)
	{
		Set<String> roles = ((UserAAAEntry) users.get(username)).getRoles();
		return new Future<Set<String>>(new HashSet<String>(roles));
	}
	
	/**
	 * Returns the security roles of the user
	 * @param userName the user name
	 * @return the security roles
	 */
	public IFuture<Set<String>> getSecurityRoles(String userName)
	{
		return new Future(new HashSet(((UserAAAEntry) users.get(userName)).getSecRoles()));
	}
	
	/**
	 * Returns the capabilities of a security role
	 * @param secRole the security role
	 * @return the capabilities of the security role
	 */
	public IFuture<Set<Integer>> getCapabilities(String secRole)
	{
		return new Future<Set<Integer>>(new HashSet<Integer>(secRoleCaps.get(secRole)));
	}
	
	/**
	 * Returns the capabilities a set of security roles
	 * @param secRoles the security roles
	 * @return the combined capabilities of the security roles
	 */
	public IFuture<Set<Integer>> getCapabilities(Set<String> secRoles)
	{
		Set<Integer> caps = new HashSet<Integer>();
		for (Iterator it = secRoles.iterator(); it.hasNext(); )
			caps.addAll(secRoleCaps.get(it.next()));
		
		return new Future<Set<Integer>>(caps);
	}
	
	/**
	 * Adds an authentication listener which triggers on
	 * authentications and deauthentications.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> addAuthenticationListener(IAuthenticationListener listener)
	{
		authenticationListeners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 * Removes an authentication listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> removeAuthenticationListener(IAuthenticationListener listener)
	{
		authenticationListeners.remove(listener);
		return IFuture.DONE;
	}
	
	private void fireAuthenticationEvent(IComponentIdentifier client, ClientInfo info)
	{
		IAuthenticationListener[] listeners = (IAuthenticationListener[]) authenticationListeners.toArray(new IAuthenticationListener[authenticationListeners.size()]);
		for (int i = 0; i < listeners.length; ++i)
		{
			final IAuthenticationListener ls = listeners[i];
			ls.authenticated(client, info).addResultListener(ia.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					authenticationListeners.remove(ls);
				}
			}));
		}
	}
	
	private void fireDeauthenticationEvent(IComponentIdentifier client, ClientInfo info)
	{
		IAuthenticationListener[] listeners = (IAuthenticationListener[]) authenticationListeners.toArray(new IAuthenticationListener[authenticationListeners.size()]);
		for (int i = 0; i < listeners.length; ++i)
		{
			final IAuthenticationListener ls = listeners[i];
			ls.deauthenticated(client, info).addResultListener(ia.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					authenticationListeners.remove(ls);
				}
			}));
		}
	}
	
	protected static void kickClient(IServiceContainer provider, final IComponentIdentifier client)
	{
		provider.getRequiredService("aaa_service").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.deauthenticate(client);
			}
		});
	}
}
