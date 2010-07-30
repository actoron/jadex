package deco4mas.examples.agentNegotiation.sa.masterServiceAgentCreator;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
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
public class InitialiseSaPlan extends Plan
{
	private static Integer id = new Integer(0);

	public void body()
	{
		try
		{
			ServiceType[] servicesArray = (ServiceType[]) getBeliefbase().getBeliefSet("serviceTypes").getFacts();
			Map<String, ServiceType> services = new HashMap<String, ServiceType>();
			for (ServiceType serviceType : servicesArray)
			{
				services.put(serviceType.getName(), serviceType);
			}

			ServiceAgentType[] agentTypesArray = (ServiceAgentType[]) getBeliefbase().getBeliefSet("serviceAgentTypes").getFacts();
			Map<String, ServiceAgentType> agentTypes = new HashMap<String, ServiceAgentType>();
			for (ServiceAgentType serviceAgentType : agentTypesArray)
			{
				agentTypes.put(serviceAgentType.getTypeName(), serviceAgentType);
			}
			ServiceAgentConfiguration[] configs = (ServiceAgentConfiguration[]) getBeliefbase().getBeliefSet("serviceAgentConfigurations")
				.getFacts();

			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);

			for (ServiceAgentConfiguration serviceAgentConfig : configs)
			{
				ServiceType serviceType = services.get(serviceAgentConfig.getServiceType());
				ServiceAgentType agentType = agentTypes.get(serviceAgentConfig.getAgentType());
				String saName = "SA" + id + "(" + serviceType.getName() + "-" + agentType.getTypeName() + ")";

				Logger saLogger = AgentLogger.getTimeEvent(saName);
				AgentLogger.addSa(saName);

				saLogger.info(serviceType.getName() + " with charakter " + agentType.getTypeName() + " C(" + agentType.getCostCharacter()
					+ "), D(" + agentType.getDurationCharacter() + "), B(" + agentType.getBlackoutCharacter() + ")");
				Map args = new HashMap();
				args.put("providedService", serviceType);
				args.put("serviceAgentType", agentType);

				cms.createComponent(saName, "deco4mas/examples/AgentNegotiation/sa/ServiceAgent.agent.xml", new CreationInfo(null, args,
					interpreter.getParent().getComponentIdentifier()), null);
				id++;
				saLogger.info("Agent start");
			}
			killAgent();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
