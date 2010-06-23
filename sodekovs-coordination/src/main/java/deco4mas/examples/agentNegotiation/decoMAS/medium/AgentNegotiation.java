package deco4mas.examples.agentNegotiation.decoMAS.medium;

import jadex.bridge.IComponentIdentifier;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.ISelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.IUtilityFunction;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.WeightFactorUtilityFunction;

public class AgentNegotiation
{
	/** Agent Negotiation phase */
	public static String EXPLORATORY_PHASE = "exploratory phase";
	public static String INTERMEDIATE_PHASE = "intermediate phase";
	public static String FINAL_PHASE = "final phase";

	private int id;
	private IComponentIdentifier owner;
	private ServiceType serviceType;
	private String state;
	private Long deadline;
	private IUtilityFunction utilityFunction;
	private ISelectionStrategy selector;

	private Long phaseEnd = Long.MAX_VALUE;
	private Set<ServiceProposal> proposals = new HashSet<ServiceProposal>();
	private IComponentIdentifier selected = null;

	public AgentNegotiation(int id, IComponentIdentifier owner, ServiceType serviceType, IUtilityFunction utilityFunction,
		ISelectionStrategy selector, Long deadline)
	{
		this.owner = owner;
		this.id = id;
		this.serviceType = serviceType;
		this.utilityFunction = utilityFunction;
		this.selector = selector;
		this.deadline = deadline;
	}

	public Long getDeadline()
	{
		return deadline;
	}

	public void addProposal(ServiceProposal proposal)
	{
		proposals.add(proposal);
	}

	public Boolean evaluateRound(Long thetime)
	{
		SortedMap<Double, IComponentIdentifier> orderedProposal = utilityFunction.benchmarkProposals(proposals, thetime);
		selected = selector.selectProposal(orderedProposal);
		// Hack!
		for (ServiceProposal pro : proposals)
		{
			if (pro.getOwner().equals(selected))
			{
				((WeightFactorUtilityFunction) utilityFunction).logWinner(pro, thetime);
			}
		}
		Boolean nextRound = false;
		if (selected == null)
			nextRound = true;
		return nextRound;
	}

	public Long getPhaseEnd()
	{
		return phaseEnd;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state, Long endtime)
	{
		this.state = state;
		this.phaseEnd = endtime;
		if (state.equals(INTERMEDIATE_PHASE))
		{
			proposals = new HashSet<ServiceProposal>();
			selected = null;
		}
	}

	public int getId()
	{
		return id;
	}

	public IComponentIdentifier getSelected()
	{
		return selected;

	}
}
