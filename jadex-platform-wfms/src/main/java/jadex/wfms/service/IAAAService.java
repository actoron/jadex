package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWfmsListener;

import java.util.Set;

/** 
 * Authentication, Access control and Accounting Service.
 */
public interface IAAAService extends IService
{
	/** Keyword representing all roles */
	public static final String ALL_ROLES = "all";
	
	// Actions
	public static final int REQUEST_PD_SERVICE	 	 	= 0;
	public static final int REQUEST_MONITORING_SERVICE	= 1;
	public static final int ADD_PROCESS_MODEL 	 		= 50;
	public static final int REQUEST_PROCESS_MODEL		= 51;
	public static final int START_BPMN_PROCESS 		 	= 100;
	public static final int START_GPMN_PROCESS 		 	= 101;
	public static final int REQUEST_MODEL_NAMES 		= 102;
	public static final int REQUEST_AVAILABLE_WORKITEMS = 103;
	public static final int COMMIT_WORKITEM			 	= 104;
	public static final int ACQUIRE_WORKITEM		 	= 105;
	public static final int RELEASE_WORKITEM		 	= 106;
	public static final int ADD_LOG_HANDLER				= 500;
	public static final int REMOVE_LOG_HANDLER			= 501;
	
	/**
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public boolean authenticate(IClient client);
	
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
}
