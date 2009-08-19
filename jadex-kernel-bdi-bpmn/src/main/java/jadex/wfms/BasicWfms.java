package jadex.wfms;

import jadex.bridge.IPlatform;
import jadex.wfms.client.GuiClient;
import jadex.wfms.client.ProcessStarterClient;
import jadex.wfms.service.IAuthenticationService;
import jadex.wfms.service.IBpmnProcessService;
import jadex.wfms.service.IGpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IRoleService;
import jadex.wfms.service.IWfmsClientService;
import jadex.wfms.service.IWorkitemQueueService;
import jadex.wfms.service.impl.BasicModelRepositoryService;
import jadex.wfms.service.impl.BasicRoleService;
import jadex.wfms.service.impl.BpmnProcessService;
import jadex.wfms.service.impl.ClientConnector;
import jadex.wfms.service.impl.GpmnProcessService;
import jadex.wfms.service.impl.NullAuthenticationService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *  Basic Wfms implementation.
 */
public class BasicWfms implements IWfms
{
	private IPlatform platform;
	
	/** Wfms services */
	private Map services;
	
	/** Initializes the Wfms */
	public BasicWfms()
	{
		services = new HashMap();
	}
	
	/**
	 *  Add a service to the Wfms.
	 *  @param type type of service
	 *  @param service The service.
	 */
	public synchronized void addService(Class type, Object service)
	{
		services.put(type, service);
	}
	
	/**
	 *  Removes a service from the Wfms.
	 *  @param type type of service
	 */
	public synchronized void removeService(Class type)
	{
		services.remove(type);
	}
	
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding Wfms-service.
	 */
	public synchronized Object getService(Class type)
	{
		return services.get(type);
	}
}
