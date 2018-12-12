package jadex.bdi.planlib.protocols;


import java.text.SimpleDateFormat;
import java.util.Date;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  The negotiation record has the purpose to store all information
 *  about one negotiation round e.g. in an iterated contract net
 *  or auction protocol.
 */
public class NegotiationRecord
{
	//-------- variables --------

	/** The cfp sent to the participants. */
	protected Object cfp;
	
	/** The cfp_info kept locally. */
	protected Object cfp_info;
	
	/** The participant proposals. */
	protected ParticipantProposal[] proposals;
	
	/** The start time. */
	protected long starttime;
	
	/** The end time. */
	protected long endtime;


	//-------- constructors --------

	/**
	 *  Create a new negotiation record.
	 *  @param cfp The call for proposal object.
	 *  @param cfp_info The cfp info (local meta information).
	 *  @param participants	The participants agent identifiers.
	 */
	public NegotiationRecord(Object cfp, Object cfp_info, IComponentIdentifier[] participants, long starttime)
	{
		this.cfp = cfp;
		this.cfp_info = cfp_info;
		this.starttime	= starttime;
		this.proposals = new ParticipantProposal[participants.length];
		for(int i=0; i<proposals.length; i++)
			proposals[i]	= new ParticipantProposal(participants[i]);
	}

	//-------- accessor methods --------

	/**
	 *  Get the call for proposal (sent to participants).
	 *  @return The call for proposal.
	 */
	public Object getCFP()
	{
		return cfp;
	}
	
	/**
	 *  Set the call for proposal (sent to participants).
	 *  @param cfp The call for proposal.
	 */
	public void setCFP(Object cfp)
	{
		this.cfp	= cfp;
	}
	
	/**
	 *  Get the cfp info (i.e. local meta information).
	 *  @return The call for proposal info.
	 */
	public Object getCFPInfo()
	{
		return cfp_info;
	}
	
	/**
	 *  Set the cfp info (i.e. local meta information).
	 *  @param cfp_info The cfp_info to set.
	 */
	public void setCFPInfo(Object cfp_info)
	{
		this.cfp_info = cfp_info;
	}

	/**
	 *  Get the start time.
	 *  @return The starttime.
	 */
	public long getStarttime()
	{
		return starttime;
	}

	/**
	 *  Set the start time.
	 *  @param starttime The start time to set.
	 */
	public void setStarttime(long starttime)
	{
		this.starttime = starttime;
	}
	
	/**
	 *  Get the end time.
	 *  @return The endtime.
	 */
	public long getEndtime()
	{
		return endtime;
	}

	/**
	 *  Set the end time.
	 *  @param endtime The end time to set.
	 */
	public void setEndtime(long endtime)
	{
		this.endtime = endtime;
	}

	/**
	 *  Get the participant proposals.
	 */
	public ParticipantProposal[]	getProposals()
	{
		return proposals; 
	}
	
	//-------- extra methods --------
	
	/**
	 *  Get all participants.
	 *  @return The participants.
	 */
	public IComponentIdentifier[] getParticipants()
	{
		IComponentIdentifier[]	participants	= new IComponentIdentifier[proposals.length];
		for(int i=0; i<participants.length; i++)
			participants[i]	= proposals[i].getParticipant();

		return participants;
	}

	/**
	 *  Get the proposal for the participant.
	 *  @param participant The participants agent identifier.
	 *  @return The participant proposal.
	 */
	// Todo: allow an agent to participate more than once?
	public ParticipantProposal getProposal(IComponentIdentifier participant)
	{
		ParticipantProposal	ret	= null;
		for(int i=0; i<proposals.length; i++)
		{
			if(proposals[i].getParticipant().equals(participant))
			{
				if(ret==null)
					ret	= proposals[i];
				else
					throw new RuntimeException("An agent is not allowed to participate more than once: "+participant);
			}
		}
		return ret;
	}

	/** 
	 * Get the string representation.
	 * @return The string representation.
	 */
	public String toString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.'-'HH:mm:ss ': '");
		StringBuffer ret = new StringBuffer();
		ret.append("NegotiationRecord(");
		ret.append("starttime: "+sdf.format(new Date(starttime))+", ");		
		ret.append("endtime: "+sdf.format(new Date(endtime))+", ");
		ret.append("cfp: "+cfp+", ");
		ret.append("proposals: "+SUtil.arrayToString(proposals)+", ");
		ret.append(")");
		return ret.toString();
	}	

	/**
	 *  Add a proposal (and the message).
	 *  @param msg The proposal message.
	 * /
	public void addProposal(IMessageEvent msg)
	{
		proposals.put(msg.getContent(), new ProposalInfo(msg));
	}

	/**
	 *  Set the acceptable proposals.
	 *  @param proposals The acceptable proposals.
	 * /
	public void setAcceptableProposals(Object[] proposals)
	{
		for(int i=0; i<proposals.length; i++)
			acceptables.add(proposals[i]);
	}

	/**
	 *  Set the winner proposals.
	 *  @param proposals The winner proposals.
	 * /
	public void setWinnerProposals(Object[] proposals)
	{
		for(int i=0; i<proposals.length; i++)
			winners.add(proposals[i]);
	}

	/**
	 *  Add an executed task.
	 *  @param task The executed task.
	 * /
	public void addExecutedTask(Object task)
	{
		tasks.add(task);
	}

	/**
	 *  Get all proposals.
	 *  @return All proposals.
	 * /
	public Object[] getProposals()
	{
		return proposals.keySet().toArray();
	}

	/**
	 *  Get the acceptable proposals.
	 *  @return The acceptable proposals.
	 * /
	public Object[] getAcceptableProposals()
	{
		return acceptables.toArray();
	}

	/**
	 *  Get the winner proposals.
	 *  @return The winner proposals.
	 * /
	public Object[] getWinnerProposals()
	{
		return winners.toArray();
	}

	/**
	 *  Get the executed tasks.
	 *  @return The executed tasks.
	 * /
	public Object[] getTasks()
	{
		return tasks.toArray();
	}
	
	/**
	 *  Get the winner participants.
	 *  @return The winner participants.
	 * /
	public AgentIdentifier[] getParticipants(Object[] props)
	{
		List ret = new ArrayList();
		for(int i=0; i<props.length; i++)
		{
			ProposalInfo pi = (ProposalInfo)proposals.get(props[i]);
			ret.add(pi.getAgentIdentifier());
		}
		return (AgentIdentifier[])ret.toArray(new AgentIdentifier[ret.size()]);
	}

	/**
	 *  Get the non-acceptable proposals.
	 *  @return The non-acceptable proposals.
	 * /
	public Object[] getNonAcceptableProposals()
	{
		HashSet ret = new HashSet(proposals.keySet());
		ret.removeAll(acceptables);
		return ret.toArray();
	}

	/**
	 *  Get the non-winner proposals.
	 *  @return The non-winner proposals.
	 * /
	public Object[] getNonWinnerProposals()
	{
		HashSet ret = new HashSet(proposals.keySet());
		ret.removeAll(winners);
		return ret.toArray();
	}

	/**
	 *  Get the sender of a proposal.
	 *  @param proposal The proposal.
	 *  @return The sender of the proposal.
	 * /
	public AgentIdentifier getAgentIdentifier(Object proposal)
	{
		ProposalInfo pi = (ProposalInfo)proposals.get(proposal);
		return pi.getAgentIdentifier();
	}

	/**
	 *  Get the message event for a proposal.
	 *  @param proposal The proposal.
	 *  @return The message event for a proposal.
	 * /
	public IMessageEvent getMessageEvent(Object proposal)
	{
		ProposalInfo pi = (ProposalInfo)proposals.get(proposal);
		return pi.getMessageEvent();
	}

	/**
	 *  Get the number of proposals.
	 *  @return The number of proposals.
	 * /
	public int size()
	{
		return proposals.size();
	}*/
}