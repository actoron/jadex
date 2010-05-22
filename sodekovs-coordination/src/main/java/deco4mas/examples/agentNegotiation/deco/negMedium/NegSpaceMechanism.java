package deco4mas.examples.agentNegotiation.deco.negMedium;

import jadex.service.clock.IClockService;
import java.util.HashMap;
import java.util.Map;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.examples.agentNegotiation.deco.AssignReply;
import deco4mas.examples.agentNegotiation.deco.AssignRequest;
import deco4mas.examples.agentNegotiation.deco.ServiceOffer;
import deco4mas.examples.agentNegotiation.deco.ServiceProposal;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Implements a negotiation coordination mechanism.
 */
public class NegSpaceMechanism extends ICoordinationMechanism
{
	/** name for the medium */
	public static String NAME = "by_neg";

	/** successive id for agents created by medium */
	private static Integer id = 0;

	/** Map of all negotiations mapped to ids */
	private HashMap<Integer, AgentNegotiation> negotiations = new HashMap<Integer, AgentNegotiation>();

	/** simulation clock, init at start */
	private IClockService clock;

	public NegSpaceMechanism(CoordinationSpace space)
	{
		super(space);
	}

	/**
	 * called by every CoordinationInformation
	 */
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

	/**
	 * Called by MediumTimeProcess for deadline check
	 */
	public void nextTick()
	{
		for (AgentNegotiation negotiation : negotiations.values())
		{
			if (negotiation.getPhaseEnd() < clock.getTime() && !negotiation.getState().equals(AgentNegotiation.FINAL_PHASE))
			{
				if (negotiation.evaluateRound())
				{
					nextRoundAssignSa(negotiation);
				} else
				{
					negotiation.setState(AgentNegotiation.FINAL_PHASE, clock.getTime());
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
			if (request.getMedium().equals(NAME))
			{
				System.out.println("#perceiveCoordinationEvent " + (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task")
					+ " for " + request.getServiceType().getName());

				// create agentNegotiation
				AgentNegotiation negotiation = new AgentNegotiation(id, request.getOwner(), request.getServiceType(), request.getUtilityFunction(),request.getSelector(), (Long) request.get("deadline"));
				negotiation.setState(AgentNegotiation.EXPLORATORY_PHASE, clock.getTime() + negotiation.getDeadline());
				negotiations.put(id, negotiation);
				id++;

				publishOffer(negotiation);
			}
		}
	}

	/**
	 * Proposal for offer received
	 */
	private void proposalReceived(CoordinationInformation ci)
	{
		// get proposal
		ServiceProposal proposal = (ServiceProposal) ((Map) ci.getValueByName("parameterDataMapping")).get("proposal");

		System.out.println("#perceiveCoordinationEvent " + (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task") + " for "
			+ proposal.getServiceType().getName());

		// set in AgentNegotiation
		AgentNegotiation negotiation = negotiations.get(proposal.getId());
		negotiation.addProposal(proposal);
	}

	/**
	 * Next Round of a negotiation for given AgentNegotiation object
	 */
	private void nextRoundAssignSa(AgentNegotiation negotiation)
	{
		System.out.println("#NextRound assignSa for " + negotiation.getServiceType().getName());
		CoordinationSpace env = (CoordinationSpace) space;
		negotiation.setState(AgentNegotiation.INTERMEDIATE_PHASE, clock.getTime() + negotiation.getDeadline());

		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();
		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		ServiceOffer offer = new ServiceOffer(negotiation.getId(), negotiation.getPhaseEnd(), negotiation.getServiceType());
		parameterDataMappings.put("offer", offer);

		coordInfo.setName("OfferRetry-" + negotiation.getServiceType().getName() + "@" + clock.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "offer");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "BDI_GOAL");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_neg");

		// publish
		System.out.println("#publish RetryOffer for " + negotiation.getServiceType().getName());
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Publish a offer at SAs
	 */
	private void publishOffer(AgentNegotiation negotiation)
	{
		// get Space
		CoordinationSpace env = (CoordinationSpace) space;

		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		ServiceOffer offer = new ServiceOffer(negotiation.getId(), negotiation.getPhaseEnd(), negotiation.getServiceType());
		parameterDataMappings.put("offer", offer);

		coordInfo.setName("Offer-" + negotiation.getServiceType().getName() + "@" + clock.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "offer");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "BDI_GOAL");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_neg");

		// publish
		System.out.println("#publish Offer for " + negotiation.getServiceType().getName());
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Callback the initiator (sma) of the request
	 */
	private void callbackSMA(AgentNegotiation negotiation)
	{
		// get Space
		CoordinationSpace env = (CoordinationSpace) space;
		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		AssignReply reply = new AssignReply(negotiation.getOwner(), negotiation.getServiceType(), negotiation.getSelected());
		parameterDataMappings.put("reply", reply);

		coordInfo.setName("Reply-" + negotiation.getServiceType().getName() + "@" + clock.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "assignSaReply");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "INTERNAL_EVENT");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_neg");

		// publish
		System.out.println("#publish assignSaReply for " + negotiation.getServiceType().getName());
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Start Medium
	 */
	public void start()
	{
		System.out.println("#Start Mechanism Negotiation");
		clock = (IClockService) space.getContext().getServiceContainer().getService(IClockService.class);
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
