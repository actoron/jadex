package deco4mas.examples.agentNegotiation.deco.negMedium;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.examples.agentNegotiation.deco.AgentNegotiation;
import deco4mas.examples.agentNegotiation.deco.AssignReply;
import deco4mas.examples.agentNegotiation.deco.AssignRequest;
import deco4mas.examples.agentNegotiation.deco.ServiceOffer;
import deco4mas.examples.agentNegotiation.deco.ServiceProposal;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

// TODO getClock().getTime() statt Data

/**
 * Implements a negotiation coordination mechanism.
 */
public class NegSpaceMechanism extends ICoordinationMechanism
{
	public static String NAME = "by_neg";
	private static Integer id = 0;
	private Long deadline = 3000l;
	private HashMap<Integer, AgentNegotiation> negotiations = new HashMap<Integer, AgentNegotiation>();

	public void perceiveCoordinationEvent(Object obj)
	{
		if (obj instanceof CoordinationInformation)
		{
			CoordinationInformation ci = (CoordinationInformation) obj;
			String task = (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task");
			if (task.equals("assignSaRequest"))
				assignSa(ci);
			if (task.equals("proposal"))
				proposalReceived(ci);
		}

	}

	public void nextTick()
	{
		for (AgentNegotiation negotiation : negotiations.values())
		{
			if (negotiation.getPhaseEnd() < new Date().getTime() && !negotiation.getState().equals(AgentNegotiation.FINAL_PHASE))
			{
				if (negotiation.evaluateBids())
				{
					synchronized (id)
					{
						System.out.println("#Retry assignSa for " + negotiation.getServiceType());
						CoordinationSpace env = (CoordinationSpace) space;
						negotiation.setState(AgentNegotiation.INTERMEDIATE_PHASE, new Date().getTime() + deadline);

						// set parameter
						CoordinationInfo coordInfo = new CoordinationInfo();
						HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
						ServiceOffer offer = new ServiceOffer(negotiation.getId(), negotiation.getPhaseEnd(), negotiation.getServiceType());
						parameterDataMappings.put("offer", offer);

						coordInfo.setName("OfferRetry-" + negotiation.getServiceType() + "@" + new Date().getTime());
						coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
						coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
						coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
						coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "offer");
						coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "BDI_GOAL");
						coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_cpn");

						// publish
						System.out.println("#publish RetryOffer for " + negotiation.getServiceType());
						env.publishCoordinationEvent(coordInfo);
					}
				} else
				{
					negotiation.setState(AgentNegotiation.FINAL_PHASE, new Date().getTime());
					callbackSMA(negotiation);
				}
			}
		}
	}

	/**
	 * Init a negotiation for given request
	 */
	private void assignSa(CoordinationInformation ci)
	{
		synchronized (id)
		{
			// get Reqeust
			AssignRequest request = (AssignRequest) ((Map) ci.getValueByName("parameterDataMapping")).get("request");
			System.out.println("#perceiveCoordinationEvent " + (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task")
				+ " for " + request.getServiceType());

			// create agentNegotiation
			AgentNegotiation agentNegotiation = new AgentNegotiation(id, request.getOwner(), request.getServiceType(), request
				.getEvaluator());
			agentNegotiation.setState(AgentNegotiation.EXPLORATORY_PHASE, new Date().getTime() + deadline);
			negotiations.put(id, agentNegotiation);
			id++;

			publishOffer(agentNegotiation);
		}
	}

	private void publishOffer(AgentNegotiation negotiation)
	{
		// get Space
		CoordinationSpace env = (CoordinationSpace) space;

		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		ServiceOffer offer = new ServiceOffer(negotiation.getId(), negotiation.getPhaseEnd(), negotiation.getServiceType());
		parameterDataMappings.put("offer", offer);

		coordInfo.setName("Offer-" + negotiation.getServiceType() + "@" + new Date().getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "offer");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "BDI_GOAL");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_cpn");

		// publish
		System.out.println("#publish Offer for " + negotiation.getServiceType());
		env.publishCoordinationEvent(coordInfo);
	}

	private void callbackSMA(AgentNegotiation negotiation)
	{
		// get Space
		CoordinationSpace env = (CoordinationSpace) space;
		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		AssignReply reply = new AssignReply(negotiation.getOwner(), negotiation.getServiceType(), negotiation.getWinner());
		parameterDataMappings.put("reply", reply);

		coordInfo.setName("Reply-" + negotiation.getServiceType() + "@" + new Date().getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "assignSaReply");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "INTERNAL_EVENT");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_cpn");

		// publish
		System.out.println("#publish assignSaReply for " + negotiation.getServiceType());
		env.publishCoordinationEvent(coordInfo);
	}

	private void proposalReceived(CoordinationInformation ci)
	{
		// get proposal
		ServiceProposal proposal = (ServiceProposal) ((Map) ci.getValueByName("parameterDataMapping")).get("proposal");

		System.out.println("#perceiveCoordinationEvent " + (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task") + " for "
			+ proposal.getServiceType());

		// set in AgentNegotiation
		AgentNegotiation negotiation = negotiations.get(proposal.getId());
		negotiation.addProposal(proposal);
	}

	public NegSpaceMechanism(CoordinationSpace space)
	{
		super(space);
	}

	public void start()
	{
		System.out.println("#Start Mechanism Negotiation");
	}

	public void stop()
	{
	}

	public void suspend()
	{
	}

	public void restart()
	{
	}

}
