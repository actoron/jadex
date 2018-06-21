package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.listeners.IAuthenticationListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** 
 * Authentication, Access control and Accounting Service.
 */
public interface IAAAService
{
	/** Keyword representing all roles */
	public static final String ALL_ROLES = "all";
	
	/** Keyword representing any role */
	public static final String ANY_ROLE = "any";
	
	/** Keyword representing the administrator security role */
	public static final String SEC_ROLE_ADMIN = "Administrator";
	
	/** Keyword representing the empty security role */
	public static final String SEC_ROLE_NONE = "none";
	
	// Actions
	public static final Integer REQUEST_PD_SERVICE				 = Integer.valueOf(0);
	public static final Integer REQUEST_MONITORING_SERVICE		 = Integer.valueOf(1);
	public static final Integer START_PROCESS					 = Integer.valueOf(200);
	public static final Integer REQUEST_AVAILABLE_WORKITEMS		 = Integer.valueOf(201);
	public static final Integer REQUEST_AVAILABLE_ACTIVITIES	 = Integer.valueOf(202);
	public static final Integer COMMIT_WORKITEM					 = Integer.valueOf(203);
	public static final Integer ACQUIRE_WORKITEM				 = Integer.valueOf(204);
	public static final Integer RELEASE_WORKITEM		 		 = Integer.valueOf(205);
	
	public static final Integer ADD_WORKITEM_LISTENER			 = Integer.valueOf(300);
	public static final Integer REMOVE_WORKITEM_LISTENER		 = Integer.valueOf(301);
	public static final Integer ADD_ACTIVITY_LISTENER			 = Integer.valueOf(302);
	public static final Integer REMOVE_ACTIVITY_LISTENER		 = Integer.valueOf(303);
	
	public static final Integer ADMIN_REQUEST_ALL_ACTIVITIES	 = Integer.valueOf(400);
	public static final Integer ADMIN_TERMINATE_ACTIVITY		 = Integer.valueOf(401);
	
	public static final Integer ADMIN_ADD_ACTIVITIES_LISTENER	 = Integer.valueOf(500);
	public static final Integer ADMIN_REMOVE_ACTIVITIES_LISTENER = Integer.valueOf(501);
	public static final Integer ADMIN_ADD_LOG_LISTENER			 = Integer.valueOf(502);
	public static final Integer ADMIN_REMOVE_LOG_LISTENER		 = Integer.valueOf(503);
	public static final Integer ADMIN_ADD_PROCESS_LISTENER		 = Integer.valueOf(504);
	public static final Integer ADMIN_REMOVE_PROCESS_LISTENER	 = Integer.valueOf(505);
	
	public static final Integer PD_REQUEST_MODEL_NAMES			 = Integer.valueOf(600);
	public static final Integer PD_REQUEST_MODEL_PATHS			 = Integer.valueOf(601);
	public static final Integer PD_ADD_PROCESS_MODEL			 = Integer.valueOf(602);
	public static final Integer PD_REMOVE_PROCESS_MODEL			 = Integer.valueOf(603);
	public static final Integer PD_REQUEST_PROCESS_MODEL		 = Integer.valueOf(604);
	
	public static final Integer PD_ADD_REPOSITORY_LISTENER		 = Integer.valueOf(700);
	public static final Integer PD_REMOVE_REPOSITORY_LISTENER	 = Integer.valueOf(701);
	
	/** All capabilities */
	public static final Set<Integer> CAPABILITIES = new HashSet<Integer>(Arrays.asList(
											   Integer.valueOf[] {
											   REQUEST_PD_SERVICE,
											   REQUEST_MONITORING_SERVICE,
											   START_PROCESS,
											   REQUEST_AVAILABLE_WORKITEMS,
											   REQUEST_AVAILABLE_ACTIVITIES,
											   COMMIT_WORKITEM,
											   ACQUIRE_WORKITEM,
											   RELEASE_WORKITEM,
											   ADD_WORKITEM_LISTENER,
											   REMOVE_WORKITEM_LISTENER,
											   ADD_ACTIVITY_LISTENER,
											   REMOVE_ACTIVITY_LISTENER,
											   PD_ADD_PROCESS_MODEL,
											   PD_REMOVE_PROCESS_MODEL,
											   PD_REQUEST_PROCESS_MODEL,
											   PD_REQUEST_MODEL_NAMES,
											   PD_REQUEST_MODEL_PATHS,
											   PD_ADD_REPOSITORY_LISTENER,
											   PD_REMOVE_REPOSITORY_LISTENER,
											   ADMIN_REQUEST_ALL_ACTIVITIES,
											   ADMIN_TERMINATE_ACTIVITY,
											   ADMIN_ADD_ACTIVITIES_LISTENER,
											   ADMIN_REMOVE_ACTIVITIES_LISTENER,
											   ADMIN_ADD_LOG_LISTENER,
											   ADMIN_REMOVE_LOG_LISTENER,
											   ADMIN_ADD_PROCESS_LISTENER,
											   ADMIN_REMOVE_PROCESS_LISTENER }));
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return null when authenticated, throws exception when authentication is denied.
	 */
	public IFuture<Void> authenticate(IComponentIdentifier client, ClientInfo info);
	
	/**
	 * Tests if a client is allowed to authenticate.
	 * @param info The client info.
	 * @return true, if the client can be authenticated.
	 */
	public IFuture<Boolean> canAuthenticate(ClientInfo info);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 * @return When done.
	 */
	public IFuture<Void> deauthenticate(IComponentIdentifier client);
	
	/**
	 * Returns the authenticated clients for a specific user name
	 * @parameter userName the user name
	 * @return Set of connected clients
	 */
	public IFuture<Set<IComponentIdentifier>> getAuthenticatedClients(String userName);
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return Null when done, Exception when access is denied.
	 */
	public IFuture<Void> accessAction(IComponentIdentifier client, Integer action);
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	//public boolean accessEvent(IClient client, Object event);
	
	/** Returns the user name of a client.
	 *  @param client the client
	 *  @return user name
	 */
	public IFuture<String> getUserName(IComponentIdentifier client);
	
	/**
	 * Returns the roles of a particular user
	 * @param userName the user name
	 * @return Set of the roles of the client.
	 */
	public IFuture<Set<String>> getRoles(String userName);
	
	/**
	 * Returns the security roles of the user
	 * @param userName the user name
	 * @return Set of the security roles.
	 */
	public IFuture<Set<String>> getSecurityRoles(String userName);
	
	/**
	 * Returns the capabilities of a security role
	 * @param secRole the security role
	 * @return Set of the capabilities of the security role.
	 */
	public IFuture<Set<Integer>> getCapabilities(String secRole);
	
	/**
	 * Returns the capabilities a set of security roles
	 * @param secRoles the security roles
	 * @return the combined capabilities of the security roles
	 */
	public IFuture<Set<Integer>> getCapabilities(Set<String> secRoles);
	
	/**
	 * Adds an authentication listener which triggers on
	 * authentications and deauthentications.
	 * 
	 * @param listener the listener
	 * @return Null, when done.
	 */
	public IFuture<Void> addAuthenticationListener(IAuthenticationListener listener);
	
	/**
	 * Removes an authentication listener.
	 * 
	 * @param listener the listener
	 * @return Null, when done.
	 */
	public IFuture<Void> removeAuthenticationListener(IAuthenticationListener listener);
}
