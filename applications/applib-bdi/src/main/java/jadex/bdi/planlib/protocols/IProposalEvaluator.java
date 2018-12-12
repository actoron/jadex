package jadex.bdi.planlib.protocols;


/**
 *  Interface used by default plans of the protocols capability
 *  for evaluating proposals, eg in iterated contract-net.
 */
public interface IProposalEvaluator
{
	//-------- methods --------
	
	/**
	 *  Evaluate the given proposals and determine winning proposals.
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The received proposals.
	 *  @return The acceptable proposals, sorted by preference (best proposal first).
	 */
	public ParticipantProposal[]	evaluateProposals(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal[] proposals);
}
