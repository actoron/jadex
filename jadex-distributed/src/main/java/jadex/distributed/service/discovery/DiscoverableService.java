package jadex.distributed.service.discovery;

import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import jadex.distributed.jmx.Agents;
import jadex.service.IService;
import jadex.service.IServiceContainer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class DiscoverableService implements IService {
	// 1. alle MBeans verfügbar machen
	// 2. DiscoveryResponder, um sich bemerkbar zu machen
	
	private IComponentManagementService _platform;
	private DiscoveryResponder _drespoonder;
	
	public DiscoverableService(IServiceContainer serviceContainer) {
		try {
			this._drespoonder = new DiscoveryResponder();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this._platform = (IComponentManagementService) serviceContainer.getService(IComponentManagementService.class); // loading order of services is of importance
	}

	/*** For IService: startService, shutdownService(IResultListener) ***/
	@Override
	public void shutdownService(IResultListener listener) {
		try {
			this._drespoonder.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startService() {
		// test: create an agent to test getAgentCount()
		//this._platform.createComponent("boring", "jadex/distributed/agents/Boring.agent.xml", null, null, null); // nice, works
		// TODO hat mal geklappt, jetzt eine NullPointerException? im MessageService
		
		// 1. register all MBeans to make them available for JMX clients
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer(); // creates MBeanServer and registeres MXBeans in the background
		
		// register AgentsMBean
		try {
			Agents agents = new Agents(this._platform);
			ObjectName agentsName = new ObjectName("daniel:name=agents");
			mbeanServer.registerMBean(agents, agentsName);
		} catch (MalformedObjectNameException e1) { // never happens
			e1.printStackTrace();
		} catch (NullPointerException e1) { // never happens
			e1.printStackTrace();
		} catch (InstanceAlreadyExistsException e) { // never happens
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
		
		
		
		// 2. start the DiscoveryResponder to be visible for other platforms, specifically for the master platform
		try {
			this._drespoonder.start();
			//System.out.println("DISCOVERABLESERVICE gestartet, DiscoveryResponder aktiv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
