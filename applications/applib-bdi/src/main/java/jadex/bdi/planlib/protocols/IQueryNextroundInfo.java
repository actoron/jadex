package jadex.bdi.planlib.protocols;


import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Interface used by default plans of the protocols capability
 *  for collecting information for a next negotiation round
 *  eg in iterated contract-net.
 */
public interface IQueryNextroundInfo
{
	//-------- methods --------
	
	/**
	 *  Collect information for next negotiation round.
	 *  @param info	The nextround info object, which might be altered.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The received proposals.
	 *  @return True, if another negotiation round should be performed.
	 */
	public boolean	queryNextroundInfo(NextroundInfo info, NegotiationRecord[] history, ParticipantProposal[] proposals);
	
	//-------- helper classes --------
	
	/**
	 *  An object encapsulating the nextround info.
	 */
	public static class NextroundInfo
	{
		//-------- attributes --------
		
		/** The cfp object. */
		protected Object	cfp;

		/** The cfp_info object. */
		protected Object	cfp_info;

		/** The cfp object. */
		protected List	participants;
		
		//-------- constructors --------
		
		/**
		 *  Create a nextround info object.
		 */
		public NextroundInfo(Object cfp, Object cfp_info, IComponentIdentifier[] participants)
		{
			this.cfp	= cfp;
			this.cfp_info	= cfp_info;
			this.participants	= SUtil.arrayToList(participants);
		}

		//-------- accessor methods --------
		
		/**
		 *  Get the cfp object.
		 *  @return Returns the cfp object.
		 */
		public Object getCfp()
		{
			return cfp;
		}

		/**
		 *  Set the cfp object.
		 *  @param cfp The cfp object to set.
		 */
		public void setCfp(Object cfp)
		{
			this.cfp = cfp;
		}

		/**
		 *  Get the cfp_info object.
		 *  @return Returns the cfp_info object.
		 */
		public Object getCfpInfo()
		{
			return cfp_info;
		}

		/**
		 *  Set the cfp_info oobject.
		 *  @param cfp_info The cfp_info object.
		 */
		public void setCfpInfo(Object cfp_info)
		{
			this.cfp_info = cfp_info;
		}

		/**
		 *  Get the participants.
		 *  @return Returns the participants.
		 */
		public IComponentIdentifier[]	getParticipants()
		{
			return (IComponentIdentifier[])participants.toArray(new IComponentIdentifier[participants.size()]);
		}

		/**
		 *  Add a participant.
		 *  @param participant The participants to add.
		 */
		public void addParticipant(IComponentIdentifier participant)
		{
			participants.add(participant);
		}

		/**
		 *  Remove a participant.
		 *  @param participant The participants to remove.
		 */
		public void removeParticipant(IComponentIdentifier participant)
		{
			participants.remove(participant);
		}
	}
}
