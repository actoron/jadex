package jadex.bdi.planlib.protocols;


import jadex.bridge.IComponentIdentifier;

/**
 *  An object containing information about
 *  a proposal made in a negotiation, i.e.,
 *  the component identifier of the participant
 *  and the content object of the proposal
 *  message.
 *  When the proposal is evaluated the
 *  result of the evaluation can be saved.
 *  In the final round of an (interated) contract-net
 *  interaction, the evaluation can store the
 *  result of the task execution.
 */
public class ParticipantProposal
{
	//-------- attributes --------
	
	/** The participants component identifier. */
	protected IComponentIdentifier	participant;
	
	/** The proposal object. */
	protected Object proposal;
	
	/** An evaluation of the proposal. */
	protected Object evaluation;
	
	//-------- constructors --------
	
	/**
	 *  Create a participant proposal.
	 *  Empty bean constructor. 
	 */
	public ParticipantProposal()
	{
	}

	/**
	 *  Create a participant proposal for a given participant.
	 */
	public ParticipantProposal(IComponentIdentifier participant)
	{
		this.participant	= participant;
	}
	
	//-------- methods --------
	
	/**
	 *	Get the participants component identifier.
	 */
	public IComponentIdentifier	getParticipant()
	{
		return participant;
	}
	
	
	/**
	 *	Set the participants component identifier.
	 */
	public void	setParticipant(IComponentIdentifier participant)
	{
		this.participant	= participant;
	}
	
	/**
	 *	Get the proposal object.
	 */
	public Object	getProposal()
	{
		return proposal;
	}
	
	/**
	 *	Set the proposal object.
	 */
	public void	setProposal(Object proposal)
	{
		this.proposal	= proposal;
	}
	
	/**
	 *	Get the evaluation.
	 */
	public Object	getEvaluation()
	{
		return evaluation;
	}
	
	/**
	 *	Set the evaluation.
	 */
	public void	setEvaluation(Object evaluation)
	{
		this.evaluation	= evaluation;
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation of the participant proposal.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append("ParticipantProposal(participant=");
		sbuf.append(participant);
		sbuf.append(", proposal=");
		sbuf.append(proposal);
		sbuf.append(", evaluation=");
		sbuf.append(evaluation);
		sbuf.append(")");
		return sbuf.toString();
	}
}
