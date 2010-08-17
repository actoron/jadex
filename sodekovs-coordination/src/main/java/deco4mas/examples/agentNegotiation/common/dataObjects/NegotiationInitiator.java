package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationInitatorInformation;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationContractInformation;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ISelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ITrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.IUtilityFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.WeightFactorUtilityFunction;

/**
 * Encapsulate all relevant information for a agent negotiation
 */
public class NegotiationInitiator
{
	/* Agent Negotiation phase */
	public static String EXPLORATORY_PHASE = "exploratory phase";
	public static String INTERMEDIATE_PHASE = "intermediate phase";
	public static String FINAL_PHASE = "final phase";
	public static String CLOSED = "closed";

	/* current negotiation state */
	private String state = "";

	/* end of the current negotiation state (interesting for medium) */
	private Long phaseEnd = Long.MAX_VALUE;

	/* synchronization monitor */
	private Object monitor = new Object();

	/* the negotiation id in the medium (unique) */
	private int id;

	/* negotiation for initiator (sma) */
	private IComponentIdentifier inititator;

	/* negotiation for service type */
	private ServiceType serviceType;

	/* deadline for rewards (or maybe negotiation rounds) */
	private Long deadline;

	/* utilityfunction to evaluate the utility of a proposal */
	private IUtilityFunction utilityFunction;

	/* selector, who select one proposal as winner (default: best utility) */
	private ISelectionStrategy selector;

	/* current proposals for this negotiation mapped to ServiceAgents */
	private Map<IComponentIdentifier, ServiceProposal> proposals = new HashMap<IComponentIdentifier, ServiceProposal>();

	/* the selected winner of the negotiation */
	private ServiceProposal selected = null;

	/*
	 * indicates if accepted[0] (sma) and accepted[1] accept the potential
	 * contract
	 */
	private Boolean[] accepted = { null, null};

	/**
	 * Encapsulate all relevant information for a agent negotiation
	 * 
	 * @param id
	 *            ID to identify the negotiation in the medium
	 * @param inititator
	 *            ServiceManagementAgent, who requested the negotiation
	 * @param serviceType
	 *            {@link ServiceType} the negotiation should find a sa for
	 * @param utilityFunction
	 *            {@link IUtilityFunction} to evaluate the proposals
	 * @param selector
	 *            {@link ISelectionStrategy}, who select one proposal as winner
	 * @param deadline
	 *            deadline for rewards (or maybe negotiation rounds)
	 */
	public NegotiationInitiator(int id, DirectNegotiationInitatorInformation info)
	{
		this.inititator = info.getInitiator();
		this.id = id;
		this.serviceType = info.getServiceType();
		this.utilityFunction = info.getUtilityFunction();
		this.selector = info.getSelector();
		this.deadline = (Long) info.getInfo("deadline");

	}

	/**
	 * Evaluate all current {@link ServiceProposal} with the
	 * {@link IUtilityFunction}
	 * 
	 * @param thetime
	 *            current clock time (for trust evaluation of
	 *            {@link ITrustFunction})
	 * @return Indicates if a new round of negotiation must start, because
	 *         selector accepted no proposal
	 */
	public Boolean evaluateRound(Long thetime)
	{
		synchronized (monitor)
		{
			Set<ServiceProposal> proposalSet = new HashSet<ServiceProposal>();
			for (Map.Entry<IComponentIdentifier, ServiceProposal> proposal : proposals.entrySet())
			{
				proposalSet.add(proposal.getValue());
			}
			SortedMap<Double, ServiceProposal> orderedProposal = utilityFunction.benchmarkProposals(proposalSet, thetime);
			selected = selector.selectProposal(orderedProposal);

			// log!
//			((WeightFactorUtilityFunction) utilityFunction).log(proposals.get(selected.getOwner()), thetime);

			Boolean nextRound = false;
			if (selected == null)
				nextRound = true;
			return nextRound;
		}
	}

	/**
	 * Add this {@link ServiceProposal} to the negotiation
	 * 
	 * @param proposal
	 *            the {@link ServiceProposal} object to add
	 */
	public void addProposal(ServiceProposal proposal)
	{
		synchronized (monitor)
		{
			proposals.put(proposal.getOwner(), proposal);
		}
	}

	/**
	 * set the new state of the negotiation. If it is a
	 * AgentNegotiation.INTERMEDIATE_PHASE all proposals and selected are
	 * deleted
	 * 
	 * @param state
	 *            the AgentNegotiation.STATE
	 * @param endtime
	 *            time when the phase should end
	 */
	public boolean setState(String state, Long endtime)
	{
		synchronized (monitor)
		{
			if (!this.state.equals(state))
			{
				this.state = state;
				this.phaseEnd = endtime;
				if (state.equals(INTERMEDIATE_PHASE))
				{
					proposals = new HashMap<IComponentIdentifier, ServiceProposal>();
					selected = null;
				}
				return true;
			} else
			{
//				System.out.println("--- SAME STATE SET ---");
				return false;
			}
				

		}
	}

	/**
	 * The receiver accept his reward
	 * 
	 * @param receiver
	 *            {@link IComponentIdentifier} of the receiver
	 */
	public void acceptReward(IComponentIdentifier receiver)
	{
		synchronized (monitor)
		{
			if (receiver.getName().equals(inititator.getName()))
			{
				accepted[0] = true;
			} else if (receiver.getName().equals(selected.getOwner().getName()))
			{
				accepted[1] = true;
			}
		}

	}

	/**
	 * @return tests if rewards are accepted for sma and sa
	 */
	public boolean areRewardsAccepted()
	{
		synchronized (monitor)
		{
			Boolean result = false;
			if (accepted[0] != null && accepted[1] != null)
			{
				if (accepted[0] && accepted[1])
				{
					result = true;
				}
			}
			return result;
		}
	}

	/**
	 * @return deadline for rewards (or maybe negotiation rounds)
	 */
	public Long getDeadline()
	{
		return deadline;
	}

	/**
	 * @return ID of negotiation
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return time, when the state of the negotiation end
	 */
	public Long getPhaseEnd()
	{
		synchronized (monitor)
		{
			return phaseEnd;
		}
	}

	/**
	 * @return the {@link IComponentIdentifier} sma of this negotiation
	 */
	public IComponentIdentifier getInitiator()
	{
		return inititator;
	}

	/**
	 * @return {@link ServiceType} the negotiation search a Agent for
	 */
	public ServiceType getServiceType()
	{
		return serviceType;
	}

	/**
	 * @return current AgentNegotiation.STATE
	 */
	public String getState()
	{
		synchronized (monitor)
		{
			return state;
		}
	}

	/**
	 * @return the selected ServiceAgent
	 */
	public ServiceProposal getSelected()
	{
		synchronized (monitor)
		{
			return selected;
		}
	}

	// /**
	// * @return the monitor object for synchronization
	// */
	// public Object getMonitor()
	// {
	// return monitor;
	// }

	@Override
	public String toString()
	{
		return "NegotiationInitiatorObject(" + id + " , " + inititator + " , " + serviceType + " , " + state + " , " + phaseEnd + " , "
			+ proposals + " , " + selected + " , " + "Accept(" + accepted[0] + " , " + accepted[1] + "))";
	}
}
