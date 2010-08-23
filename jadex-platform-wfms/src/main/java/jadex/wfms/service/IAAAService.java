package jadex.wfms.service;

import jadex.commons.service.IService;
import jadex.wfms.client.IClient;

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
	public static final Integer REQUEST_PD_SERVICE				 = new Integer(0);
	public static final Integer REQUEST_MONITORING_SERVICE		 = new Integer(1);
	public static final Integer START_PROCESS					 = new Integer(200);
	public static final Integer REQUEST_AVAILABLE_WORKITEMS		 = new Integer(201);
	public static final Integer REQUEST_AVAILABLE_ACTIVITIES	 = new Integer(202);
	public static final Integer COMMIT_WORKITEM					 = new Integer(203);
	public static final Integer ACQUIRE_WORKITEM				 = new Integer(204);
	public static final Integer RELEASE_WORKITEM		 		 = new Integer(205);
	
	public static final Integer ADD_WORKITEM_LISTENER			 = new Integer(300);
	public static final Integer REMOVE_WORKITEM_LISTENER		 = new Integer(301);
	public static final Integer ADD_ACTIVITY_LISTENER			 = new Integer(302);
	public static final Integer REMOVE_ACTIVITY_LISTENER		 = new Integer(303);
	
	public static final Integer ADMIN_REQUEST_ALL_ACTIVITIES	 = new Integer(400);
	public static final Integer ADMIN_TERMINATE_ACTIVITY		 = new Integer(401);
	
	public static final Integer ADMIN_ADD_ACTIVITIES_LISTENER	 = new Integer(500);
	public static final Integer ADMIN_REMOVE_ACTIVITIES_LISTENER = new Integer(501);
	public static final Integer ADMIN_ADD_LOG_LISTENER			 = new Integer(502);
	public static final Integer ADMIN_REMOVE_LOG_LISTENER		 = new Integer(503);
	public static final Integer ADMIN_ADD_PROCESS_LISTENER		 = new Integer(504);
	public static final Integer ADMIN_REMOVE_PROCESS_LISTENER	 = new Integer(505);
	
	public static final Integer PD_REQUEST_MODEL_NAMES			 = new Integer(600);
	public static final Integer PD_REQUEST_MODEL_PATHS			 = new Integer(601);
	public static final Integer PD_ADD_PROCESS_MODEL			 = new Integer(602);
	public static final Integer PD_REMOVE_PROCESS_MODEL			 = new Integer(603);
	public static final Integer PD_REQUEST_PROCESS_MODEL		 = new Integer(604);
	
	public static final Integer PD_ADD_REPOSITORY_LISTENER		 = new Integer(700);
	public static final Integer PD_REMOVE_REPOSITORY_LISTENER	 = new Integer(701);
	
	/** All capabilities */
	public static final Set CAPABILITIES = new HashSet(Arrays.asList(
											   new Object[] {
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
	 * @return true, if the client has been successfully authenticated.
	 */
	public boolean authenticate(IClient client);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public void deauthenticate(IClient client);
	
	/**
	 * Returns the authenticated clients for a specific user name
	 * @parameter userName the user name
	 * @return Set of connected clients
	 */
	public Set getAuthenticatedClients(String userName);
	
	/**
	 * Checks if a client can access an action
	 * @param client the client requesting the action
	 * @param action the action the client is requesting
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessAction(IClient client, Integer action);
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	//public boolean accessEvent(IClient client, Object event);
	
	/**
	 * Returns the roles of a particular user
	 * @param userName the user name
	 * @return the roles of the client
	 */
	public Set getRoles(String userName);
	
	/**
	 * Returns the security roles of the user
	 * @param userName the user name
	 * @return the security roles
	 */
	public Set getSecurityRole(String userName);
	
	/**
	 * Returns the capabilities of a security role
	 * @param secRole the security role
	 * @return the capabilities of the security role
	 */
	public Set getCapabilities(String secRole);
	
	/**
	 * Returns the capabilities a set of security roles
	 * @param secRoles the security roles
	 * @return the combined capabilities of the security roles
	 */
	public Set getCapabilities(Set secRoles);
	
	/**
	 * Adds an authentication listener which triggers on
	 * authentications and deauthentications.
	 * 
	 * @param listener the listener
	 */
	public void addAuthenticationListener(IAuthenticationListener listener);
	
	/**
	 * Removes an authentication listener.
	 * 
	 * @param listener the listener
	 */
	public void removeAuthenticationListener(IAuthenticationListener listener);
}
