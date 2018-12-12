package jadex.bdi.planlib.protocols;


/**
 *  A helper class that handles a receiver side goals
 *  of the iterated contract net protocol by delegating
 *  to some implementation of the corresponding interface.
 */
public class ICNPHandler implements IProposalEvaluator, IQueryNextroundInfo
{
	//-------- attributes --------
	
	/** The proposal evaluator. */
	protected IProposalEvaluator	proposal_evaluator;
	
	/** The query nextround info. */
	protected IQueryNextroundInfo	query_nextround_info;
	
	//-------- constructors --------
	
	/**
	 *  Create an ICNPHandler using the given implementations.
	 *  @param proposal_evaluator	The proposal evaluator.
	 *  @param query_nextround_info	The query nextround info. 
	 */
	public ICNPHandler(IProposalEvaluator proposal_evaluator, IQueryNextroundInfo query_nextround_info)
	{
		this.proposal_evaluator	= proposal_evaluator;
		this.query_nextround_info	= query_nextround_info;
	}
	
	//-------- IProposalEvaluator interface --------

	/**
	 *  Evaluate the given proposals and determine winning proposals.
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The received proposals.
	 *  @return The acceptable proposals, sorted by preference (best proposal first).
	 */
	public ParticipantProposal[] evaluateProposals(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal[] proposals)
	{
		return proposal_evaluator.evaluateProposals(cfp, cfp_info, history, proposals);
	}

	//-------- IQueryNextroundInfo interface --------

	/**
	 *  Collect information for next negotiation round.
	 *  @param info	The nextround info object, which might be altered.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The received proposals.
	 *  @return True, if another negotiation round should be performed.
	 */
	public boolean queryNextroundInfo(NextroundInfo info, NegotiationRecord[] history, ParticipantProposal[] proposals)
	{
		return query_nextround_info.queryNextroundInfo(info, history, proposals);
	}
}
