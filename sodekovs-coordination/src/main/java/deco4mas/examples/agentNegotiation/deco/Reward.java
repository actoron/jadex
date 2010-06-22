package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;
import java.util.Set;

// TODO Negotiation Details

public class Reward
{
	private Integer id;
	private Boolean answer;
	private Set<IComponentIdentifier> participants;
	// private Long deadline = 0l;
	private ServiceOffer offer;
	private Boolean finalReward;

	public Reward(Integer id, Set<IComponentIdentifier> participants, ServiceOffer offer, Boolean finalReward)
	{
		this.id = id;
		// this.deadline = deadline;
		this.participants = participants;
		this.finalReward = finalReward;
		this.offer = offer;
	}

	public Set<IComponentIdentifier> getParticipants()
	{
		return participants;
	}

	public void setParticipants(Set<IComponentIdentifier> participants)
	{
		this.participants = participants;
	}

	public Boolean getFinalReward()
	{
		return finalReward;
	}

	public void getFinalReward(Boolean finalReward)
	{
		this.finalReward = finalReward;
	}

	public void setOffer(ServiceOffer offer)
	{
		this.offer = offer;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Boolean getAnswer()
	{
		return answer;
	}

	public void setAnswer(Boolean answer)
	{
		this.answer = answer;
	}

	public ServiceOffer getServiceOffer()
	{
		return offer;
	}

	public Boolean testPartizipant(IComponentIdentifier sa)
	{
		return participants.contains(sa);
	}
}
