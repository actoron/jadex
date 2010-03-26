package jadex.distributed.jmx;

import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;

public class Agents implements AgentsMBean {
	
	private IComponentManagementService platform;
	
	private int agentCount;
	
	public Agents(IComponentManagementService platform) {
		super(); // Macht der Gewohnheit
		this.platform = platform;
		// wird von dem ComponentManagementService_Client erzeugt und beim Plattform-MBeanServer registriert
	}

	
	@Override
	public int getAgentCount() {
		IResultListener listener  = new IResultListener() { // I know, readability with anonymous inner classes sucks ...
			@Override
			public void resultAvailable(Object source, Object result) {
				
			}
			@Override
			public void exceptionOccurred(Object source, Exception exception) {
				
			}
		};
		this.platform.getComponentDescriptions(listener);
		
		return this.agentCount;
	}
	
}
