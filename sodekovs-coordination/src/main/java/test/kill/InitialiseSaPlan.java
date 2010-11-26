package test.kill;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.service.SServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentConfiguration;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentType;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Init SAs
 */
public class InitialiseSaPlan extends Plan {


	public void body() {
		System.out.println("New Master Agent...");
//		waitFor(1);
		
			killAgent();
	}

}
