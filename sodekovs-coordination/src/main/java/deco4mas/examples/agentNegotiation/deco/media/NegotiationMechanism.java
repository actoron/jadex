package deco4mas.examples.agentNegotiation.deco.media;

import jadex.service.clock.IClockService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import deco.lang.dynamics.AgentElementType;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.examples.agentNegotiation.common.dataObjects.NegotiationInitiator;
import deco4mas.examples.agentNegotiation.common.dataObjects.NegotiationParticipant;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceContract;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationInitatorInformation;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationParticipantInformation;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationContractInformation;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Implements a negotiation coordination mechanism.
 */
public class NegotiationMechanism extends ICoordinationMechanism
{
	/* Logger for the medium*/
	private Logger mediumLogger = AgentLogger.getTimeEvent("NegSpaceMedium");

	/* name for the medium */
	public static String NAME = "by_neg";

	/* id for negtiations */
	private Integer id = 0;

	/* Map of all current negotiations mapped by id (synchronized) */
	private Map<Integer, NegotiationInitiator> negotiations = Collections.synchronizedMap(new HashMap<Integer, NegotiationInitiator>());

	/* Set of all current participants (synchronized) */
	private Set<NegotiationParticipant> participants = Collections.synchronizedSet(new HashSet<NegotiationParticipant>());

	/* simulation clock, init at start */
	private IClockService clock;

	/**
	 * Implements a negotiation coordination mechanism.
	 * 
	 * @param the
	 *            space for the mechanism
	 */
	public NegotiationMechanism(CoordinationSpace space)
	{
		super(space);
	}

	/**
	 * Called for every {@link CoordinationInformation} by
	 * {@link CoordinationSpace} implements the method in the interface
	 * {@link ICoordinationMechanism}
	 * 
	 * @param obj
	 *            The fired envirement Event (should be a
	 *            {@link CoordinationInformation})
	 */
	public void perceiveCoordinationEvent(Object obj)
	{
		if (obj instanceof CoordinationInformation)
		{
			CoordinationInformation ci = (CoordinationInformation) obj;
			NegotiationInformation info = (NegotiationInformation) ((Map) ci.getValueByName("parameterDataMapping")).get("information");
			if (info.getMediumType().equals(NAME))
			{
				if (info instanceof DirectNegotiationInitatorInformation)
					newInitiatiorRequest(info);
				else if (info instanceof DirectNegotiationParticipantInformation)
					participantRegister(info);
				else if (info instanceof NegotiationContractInformation)
					contractReply(info);
			}
		}
	}

	/**
	 * Init a negotiation for given {@link DirectNegotiationInitatorInformation}
	 * (synchronized)
	 * 
	 * @param info
	 *            received {@link NegotiationInformation}
	 */
	private synchronized void newInitiatiorRequest(NegotiationInformation info)
	{
		DirectNegotiationInitatorInformation initiatorInfo = (DirectNegotiationInitatorInformation) info;
		System.out.println("#perceiveCoordinationEvent " + initiatorInfo);
		mediumLogger.info("New request: " + initiatorInfo);

		// create NegotiationInitiatorObject
		NegotiationInitiator negotiation = null;
		synchronized (id)
		{
			negotiation = new NegotiationInitiator(id, initiatorInfo);
			negotiation.setState(NegotiationInitiator.EXPLORATORY_PHASE, clock.getTime() + negotiation.getDeadline());
			negotiations.put(id, negotiation);
			id++;
		}

		performNegotiation(negotiation);
	}

	/**
	 * (De)register a participant for given
	 * {@link DirectNegotiationParticipantInformation}
	 * 
	 * @param info
	 *            received {@link NegotiationInformation}
	 */
	private synchronized void participantRegister(NegotiationInformation info)
	{
		DirectNegotiationParticipantInformation participantInfo = (DirectNegotiationParticipantInformation) info;
		System.out.println("#perceiveCoordinationEvent " + participantInfo);
		mediumLogger.info("participant: " + participantInfo);

		boolean found = false;
		synchronized (participants)
		{
			for (NegotiationParticipant participant : participants)
			{
				if (participant.getParticipant().getLocalName().equals(participantInfo.getParticipant().getLocalName()))
				{
					participant.setBlackout(participantInfo.getBlackout());
					found = true;
				}
			}
			if (!found)
			{
				participants.add(new NegotiationParticipant(participantInfo));
			}
		}
	}

	/**
	 * called when a {@link ServiceContract} reply is receive
	 * 
	 * @param info
	 *            received {@link NegotiationInformation}
	 */
	private void contractReply(NegotiationInformation info)
	{
		NegotiationContractInformation contractInfo = (NegotiationContractInformation) info;
		System.out.println("#perceiveCoordinationEvent " + contractInfo);
		mediumLogger.info("New Answer: " + contractInfo);

		NegotiationInitiator negotiation = negotiations.get(contractInfo.getId());
		if (negotiation.getState().equals(NegotiationInitiator.FINAL_PHASE))
		{
			Boolean[] answers = contractInfo.getAnswers();
			// initiator
			if (answers[0] != null)
			{
				if (answers[0])
				{
					negotiation.acceptReward(contractInfo.getContract().getInitiator());
				} else
				{
					if (negotiation.setState(NegotiationInitiator.INTERMEDIATE_PHASE, clock.getTime() + negotiation.getDeadline()))
					{
						sendReward(new NegotiationContractInformation(negotiation.getId(), NAME, new ServiceContract(negotiation
							.getServiceType(), negotiation.getSelected().getBid(), negotiation.getInitiator(), negotiation.getSelected()
							.getOwner()), NegotiationContractInformation.CANCELED_REWARD));
						performNegotiation(negotiation);
					}
				}
			}

			// participant
			if (answers[1] != null)
			{
				if (answers[1])
				{
					negotiation.acceptReward(contractInfo.getContract().getParticipant());
				} else
				{
					if (negotiation.setState(NegotiationInitiator.INTERMEDIATE_PHASE, clock.getTime() + negotiation.getDeadline()))
					{
						sendReward(new NegotiationContractInformation(negotiation.getId(), NAME, new ServiceContract(negotiation
							.getServiceType(), negotiation.getSelected().getBid(), negotiation.getInitiator(), negotiation.getSelected()
							.getOwner()), NegotiationContractInformation.CANCELED_REWARD));
						performNegotiation(negotiation);
					}
				}
			}

			// check if we can proceed
			if (negotiation.areRewardsAccepted())
			{
				if (negotiation.setState(NegotiationInitiator.CLOSED, Long.MAX_VALUE))
				{
					sendReward(new NegotiationContractInformation(negotiation.getId(), NAME, new ServiceContract(negotiation.getServiceType(),
						negotiation.getSelected().getBid(), negotiation.getInitiator(), negotiation.getSelected().getOwner()),
						NegotiationContractInformation.FINAL_REWARD));
					negotiations.remove(negotiation);
				}
			}
		}
	}

	/**
	 * Called by MediumTimeProcess for deadline check
	 */
	public void nextTick()
	{
		for (NegotiationInitiator negotiation : negotiations.values())
		{
			if (negotiation.getPhaseEnd() < clock.getTime()
				&& (negotiation.getState().equals(NegotiationInitiator.EXPLORATORY_PHASE) || negotiation.getState().equals(
					NegotiationInitiator.INTERMEDIATE_PHASE)))
			{
				System.out.println("#tick " + negotiation + " at deadline");
				mediumLogger.info(negotiation + ") at deadline");

				// go next round
				performNegotiation(negotiation);
			}

			if (negotiation.getPhaseEnd() < clock.getTime() && negotiation.getState().equals(NegotiationInitiator.FINAL_PHASE))
			{
				System.out.println("#tick " + negotiation + " at deadline");
				mediumLogger.info(negotiation + ") at deadline");

				if (negotiation.areRewardsAccepted())
				{
					if (negotiation.setState(NegotiationInitiator.CLOSED, Long.MAX_VALUE))
					{
						negotiations.remove(negotiation);
						sendReward(new NegotiationContractInformation(negotiation.getId(), NAME, new ServiceContract(negotiation
							.getServiceType(), negotiation.getSelected().getBid(), negotiation.getInitiator(), negotiation.getSelected()
							.getOwner()), NegotiationContractInformation.FINAL_REWARD));
					}
				} else
				{
					if (negotiation.setState(NegotiationInitiator.INTERMEDIATE_PHASE, clock.getTime() + negotiation.getDeadline()))
					{
						performNegotiation(negotiation);
					}
				}
			}
		}
	}

	/**
	 * Send a reward to sa / sma for the given {@link NegotiationContractInformation}
	 * @param info the given {@link NegotiationContractInformation}
	 */
	private void sendReward(NegotiationContractInformation info)
	{
		// get Space
		CoordinationSpace env = (CoordinationSpace) space;

		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();
		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		parameterDataMappings.put("information", info);

		coordInfo.setName(info + "@" + clock.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, Constants.BDI);
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "negotiationContract");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, AgentElementType.INTERNAL_EVENT.toString());
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, NAME);

		// publish
		mediumLogger.info("sendContract " + info);
		System.out.println("#publish " + info);
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Perform the negotiation for the given {@link NegotiationInformation}
	 * 
	 * @param the given {@link NegotiationInformation}
	 */
	private void performNegotiation(NegotiationInitiator negotiation)
	{
		System.out.println("#bidRound " + negotiation);
		mediumLogger.info("bid round " + negotiation);

		ServiceOffer offer = new ServiceOffer(negotiation.getId(), negotiation.getServiceType());
		for (NegotiationParticipant participant : participants)
		{
			if (!participant.isBlackout())
			{
				negotiation.addProposal(participant.deliverProposal(offer));
			}

		}

		// return false if a proposal is accepted
		if (negotiation.evaluateRound(clock.getTime()))
		{
			System.out.println("#noSaFound " + negotiation);
			mediumLogger.info("no sa found " + negotiation);

			// wait 1 deadline and then try again
			negotiation.setState(NegotiationInitiator.INTERMEDIATE_PHASE, clock.getTime() + negotiation.getDeadline());

		} else
		{
			System.out.println("#saFound " + negotiation);
			mediumLogger.info("sa found " + negotiation);

			// send rewards
			if (negotiation.setState(NegotiationInitiator.FINAL_PHASE, clock.getTime() + negotiation.getDeadline()))
			{
				sendReward(new NegotiationContractInformation(negotiation.getId(), NAME, new ServiceContract(negotiation.getServiceType(),
					negotiation.getSelected().getBid(), negotiation.getInitiator(), negotiation.getSelected().getOwner()),
					NegotiationContractInformation.TENTATIVE_REWARD));
			}
		}
	}

	/*
	 * ------------ Interface for ICoordinationMechanism ------------ /*
	 */

	public void start()
	{
		System.out.println("#StartMechanismNegotiation");
		clock = (IClockService) space.getContext().getServiceContainer().getService(IClockService.class);
		// clock.reset();
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

	public String getRealisationName()
	{
		return NAME;
	}

}
