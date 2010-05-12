package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class AgentNegotiation
{
	public static String EXPLORATORY_PHASE = "exploratory phase";
	public static String INTERMEDIATE_PHASE = "intermediate phase";
	public static String FINAL_PHASE = "final phase";
	
	private Long phaseEnd = Long.MAX_VALUE;
	private IComponentIdentifier owner;
	private String serviceType = "default";
	private String state = "default";
	private Evaluator evaluator;
	private int id;
	private Set<ServiceProposal> proposals = new HashSet<ServiceProposal>();
	private SortedMap<Double, IComponentIdentifier> orderedProposal;

	private IComponentIdentifier winner;

	public AgentNegotiation(int id, IComponentIdentifier owner, String serviceType, Evaluator evaluator)
	{
		this.owner = owner;
		this.id = id;
		this.serviceType = serviceType;
		this.evaluator = evaluator;
	}
	
	public void addProposal(ServiceProposal proposal)
	{
		proposals.add(proposal);
	}
	
	public Boolean evaluateBids()
	{
		orderedProposal = evaluator.evaluateProposals(proposals);
		return nextRound();
	}

	public Long getPhaseEnd()
	{
		return phaseEnd;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public String getServiceType()
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
			orderedProposal = new TreeMap<Double, IComponentIdentifier>();
		}
	}

	public int getId()
	{
		return id;
	}
	
	public IComponentIdentifier getWinner()
	{
		return orderedProposal.get(orderedProposal.firstKey());
		
	}

	public Boolean nextRound()
	{
		if (orderedProposal.size() > 0) return false;
		else return true;
		
	}
}
