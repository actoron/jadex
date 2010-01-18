package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.client.IClient;

import java.util.Set;

/** 
 * Authentication, Access control and Accounting Service.
 */
public interface IAAAService extends IService
{
	/** Keyword representing all roles */
	public static final String ALL_ROLES = "all";
	
	// Actions
	public static final int REQUEST_PD_SERVICE				 = 0;
	public static final int REQUEST_MONITORING_SERVICE		 = 1;
	public static final int ADD_PROCESS_MODEL				 = 50;
	public static final int REQUEST_PROCESS_MODEL			 = 51;
	public static final int START_BPMN_PROCESS				 = 100;
	public static final int START_GPMN_PROCESS				 = 101;
	public static final int REQUEST_MODEL_NAMES				 = 102;
	public static final int REQUEST_AVAILABLE_WORKITEMS		 = 103;
	public static final int REQUEST_AVAILABLE_ACTIVITIES	 = 104;
	public static final int COMMIT_WORKITEM					 = 105;
	public static final int ACQUIRE_WORKITEM				 = 106;
	public static final int RELEASE_WORKITEM		 		 = 107;
	public static final int ADMIN_REQUEST_ALL_ACTIVITIES	 = 401;
	public static final int ADMIN_ADD_ACTIVITIES_LISTENER	 = 500;
	public static final int ADMIN_REMOVE_ACTIVITIES_LISTENER = 501;
	public static final int ADMIN_ADD_LOG_LISTENER			 = 502;
	public static final int ADMIN_REMOVE_LOG_LISTENER		 = 503;
	public static final int ADMIN_ADD_PROCESS_LISTENER		 = 504;
	public static final int ADMIN_REMOVE_PROCESS_LISTENER	 = 505;
	
	/** All capabilities */
	public static final int[] CAPABILITIES = { REQUEST_PD_SERVICE,
											   REQUEST_MONITORING_SERVICE,
											   ADD_PROCESS_MODEL,
											   REQUEST_PROCESS_MODEL,
											   START_BPMN_PROCESS,
											   START_GPMN_PROCESS,
											   REQUEST_MODEL_NAMES,
											   REQUEST_AVAILABLE_WORKITEMS,
											   REQUEST_AVAILABLE_ACTIVITIES,
											   COMMIT_WORKITEM,
											   ACQUIRE_WORKITEM,
											   RELEASE_WORKITEM,
											   ADMIN_REQUEST_ALL_ACTIVITIES,
											   ADMIN_ADD_ACTIVITIES_LISTENER,
											   ADMIN_REMOVE_ACTIVITIES_LISTENER,
											   ADMIN_ADD_LOG_LISTENER,
											   ADMIN_REMOVE_LOG_LISTENER,
											   ADMIN_ADD_PROCESS_LISTENER,
											   ADMIN_REMOVE_PROCESS_LISTENER };
	
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
	public boolean accessAction(IClient client, int action);
	
	/**
	 * Checks if a client can access an event
	 * @param client the client scheduled to receive the event
	 * @param event the event
	 * @return true, if the client is authorized to perform the action, false otherwise
	 */
	public boolean accessEvent(IClient client, Object event);
	
	/**
	 * Returns the roles of a particular client
	 * @param client the client
	 * @return the roles of the client
	 */
	public Set getRoles(IClient client);
	
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
