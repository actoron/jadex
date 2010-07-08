package deco4mas.examples.agentNegotiation.sa.masterSa;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Init SAs
 */
public class InitialiseSaPlan extends Plan
{
	private static Integer id = new Integer(0);

	public void body()
	{
		ServiceType[] servicesArray = (ServiceType[]) getBeliefbase().getBeliefSet("services").getFacts();
		Map<String, ServiceType> services = new HashMap<String, ServiceType>();
		for (ServiceType serviceType : servicesArray)
		{
			services.put(serviceType.getName(), serviceType);
		}

		AgentType[] agentTypesArray = (AgentType[]) getBeliefbase().getBeliefSet("agentTypes").getFacts();
		Map<String, AgentType> agentTypes = new HashMap<String, AgentType>();
		for (AgentType serviceAgentType : agentTypesArray)
		{
			agentTypes.put(serviceAgentType.getTypeName(), serviceAgentType);
		}
		ServiceAgentConfig[] configs = (ServiceAgentConfig[]) getBeliefbase().getBeliefSet("serviceAgentConfigs").getFacts();

		IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
			IComponentManagementService.class);

		for (ServiceAgentConfig serviceAgentConfig : configs)
		{
			ServiceType serviceType = services.get(serviceAgentConfig.getServiceType());
			AgentType agentType = agentTypes.get(serviceAgentConfig.getAgentType());
			String saName = "SA" + id + "(" + serviceType.getName() + "-" + agentType.getTypeName() + ")";

			Logger saLogger = AgentLogger.getTimeEvent(saName);
			AgentLogger.addSa(saName);

			saLogger.info(serviceType.getName() + " with charakter " + agentType.getTypeName() + " C(" + agentType.getCostCharacter()
				+ "), D(" + agentType.getDurationCharacter() + "), B(" + agentType.getBlackoutCharacter() + ")");
			Map args = new HashMap();
			args.put("providedService", serviceType);
			args.put("agentType", agentType);

			cms.createComponent(saName, "deco4mas/examples/AgentNegotiation/sa/serviceAgent.agent.xml", new CreationInfo(null, args,
				interpreter.getParent().getComponentIdentifier()), null);
			id++;
			saLogger.info("Agent start");
		}
		killAgent();
	}
}
