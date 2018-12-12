package jadex.bdi.planlib.protocols;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  A default implementation of the proposal evaluator interface.
 *  The implementation determines acceptable proposals by comparing
 *  proposals or evaluations to a given limit value.
 *  
 *  <p>
 *  The evaluation process implemented in the evaluateProposals() method
 *  is distributed across three methods, which can be separately overwritten
 *  if needed, while reusing functionality of the other methods.
 *  <ol>
 *  <li>The proposals are evaluated
 *  	by calling the the evaluateProposal() method for each of the proposals.
 *  	The evaluation result is written back into the proposal. The
 *      default implementation just checks, if the proposal object itself
 *      is suitable as an evaluation (i.e. if it is comparable).
 *   </li><li>For each of the proposals, the acceptability is determined.
 *      By default, the given string constants are interpreted or, if
 *      a limit value is given, the proposal evaluations are compared
 *      to the limit value.
 *  </li><li>Finally, the acceptable proposals are ordered by preference.
 *      As a default, the proposals are compared to each other and sorted
 *      according to the given ordering.
 *  </ol>
 */
public class ProposalEvaluator implements IProposalEvaluator
{
	//-------- constants --------
	
	/** Evaluation value indicating an inacceptable proposal that should be excluded. */
	public static final String	EVALUATION_INACCEPTABLE	= "evaluation-inacceptable";
	
	/** Evaluation value indicating an acceptable proposal that should be considered in further negotiation rounds. */
	public static final String	EVALUATION_ACCEPTABLE	= "evaluation-acceptable";
	
	//-------- attributes --------
	
	/** A comparator used for comparing evaluations. */
	protected Comparator	evaluation_comparator;
	
	/** Limit determining the acceptability of an evaluation. */
	protected Object	evaluation_limit;
	
	/** Flag indicating if evaluations are rated ascending (the higher the better)
	    or the other way round. */
	protected boolean	ascending;

	//-------- constructors --------
	
	/**
	 *  Create a default proposal evaluator.
	 *  This (empty) constructor cannot be used directly,
	 *  as it requires at least the isProposalAcceptable() method
	 *  to be overwritten.
	 */
	protected ProposalEvaluator() {}
	
	/**
	 *  Create a default proposal evaluator with a given limit value.
	 *  This constructor can be used without overwriting any methods,
	 *  if the proposal objects are comparable to each other and the limit
	 *  value. Otherwise, the evaluateProposal() method should be overwritten
	 *  to provide comparable evaluation values for the proposal objects.
	 *  @param evaluation_limit	The limit specifying which proposals are acceptable.
	 *  @param ascending Sort order, which specifies that all evaluations below or equal (true)
	 *    or above or equal (false) to the limit are acceptable.
	 */
	public ProposalEvaluator(Object evaluation_limit, boolean ascending)
	{
		this.evaluation_limit	= evaluation_limit;
		this.ascending	= ascending;
	}

	/**
	 *  Create a default proposal evaluator with a given limit value.
	 *  This constructor can be used without overwriting any methods,
	 *  if the proposal objects are comparable to each other and the limit
	 *  value using the given comparator. Otherwise, the evaluateProposal()
	 *  method should be overwritten to provide comparable evaluation values for the proposal objects.
	 *  @param evaluation_comparator	A comparator used to compare proposal evaluations.
	 *  @param evaluation_limit	The limit specifying which proposals are acceptable.
	 *  @param ascending Sort order, which specifies that all evaluations below or equal (true)
	 *    or above or equal (false) to the limit are acceptable.
	 */
	public ProposalEvaluator(Comparator evaluation_comparator, Object evaluation_limit, boolean ascending)
	{
		this.evaluation_comparator	= evaluation_comparator;
		this.evaluation_limit	= evaluation_limit;
		this.ascending	= ascending;
	}

	//-------- IProposalEvaluator interface --------
	
	/**
	 *  Evaluate the given proposals and determine winning proposals.
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The received proposals.
	 *  @return The winners among the proposals.
	 */
	public ParticipantProposal[] evaluateProposals(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal[] proposals)
	{
		// Determine evaluations for each of the proposals.
		for(int i=0; i<proposals.length; i++)
		{
			proposals[i].setEvaluation(evaluateProposal(cfp, cfp_info, history, proposals[i]));
		}
		
		// Determine acceptable proposals.
		List	acceptables	= new ArrayList();
		for(int i=0; i<proposals.length; i++)
		{
			if(isProposalAcceptable(cfp, cfp_info, history, proposals[i]))
			{
				acceptables.add(proposals[i]);
			}
		}
		
		// Order acceptable proposals by preference.
		ParticipantProposal[]	ordered	= orderAcceptables(cfp, cfp_info, history,
			(ParticipantProposal[])acceptables.toArray(new ParticipantProposal[acceptables.size()]));
		
		return ordered;
	}
	
	//-------- template methods --------
	
	/**
	 *  Evaluate the given proposal.
	 *  An implementation may use the defined constants for specifying
	 *  evaluation results, but custom evaluation values are also allowed.
	 *  This default implementation justs uses the proposal object
	 *  itself as its evaluation, as long as it is comparable in itself
	 *  or with a given comparator.
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposal A received proposal.
	 *  @return The proposal evaluation.
	 */
	protected Object	evaluateProposal(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal proposal)
	{
		Object	ret	= null;
		if(isValueComparable(proposal.getProposal()))
		{
			ret	= proposal.getProposal();
		}
		return ret;
	}

	/**
	 *  Check if a proposal is acceptable.
	 *  This default implementation checks for one of the evaluation constants and otherwise tries
	 *  to use the given evaluation limit (if any) and compares it to the proposals evaluation value.
	 *  Proposals without evaluation or which are not comparable are deemed inacceptable by default.
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposal A received proposal.
	 *  @return The proposal evaluation.
	 */
	protected boolean	isProposalAcceptable(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal proposal)
	{
		boolean	ret;
		
		// Acceptability aready set in proposal.
		if(EVALUATION_ACCEPTABLE.equals(proposal.getEvaluation())
			|| EVALUATION_INACCEPTABLE.equals(proposal.getEvaluation()))
		{
			ret	= !EVALUATION_INACCEPTABLE.equals(proposal.getEvaluation());
		}
		
		// Use limit to determine acceptability
		else if(proposal.getEvaluation()!=null && evaluation_limit!=null)
		{
			int	eval;
			if(evaluation_comparator!=null)
			{
				eval	= evaluation_comparator.compare(proposal.getEvaluation(), evaluation_limit);
			}
			else
			{
				eval	= ((Comparable)proposal.getEvaluation()).compareTo(evaluation_limit);
			}
			ret	= ascending ? eval<=0 : eval>=0;
		}
		else
		{
			ret	= false;
		}
		
		return ret;
	}
	
	/**
	 *  Order acceptable proposals by preference.
	 *  This default implementation tries to compare the proposal evaluations
	 *  directly or using the given comparator.
	 *  If some proposal evaluations are not comparable,
	 *  these are returned in the original order (after comparable proposals, if any).
	 *  @param cfp	The original call-for-proposal object.
	 *  @param cfp_info	Local meta information associated to the interaction.
	 *  @param history The history of negotiation rounds.
	 *  @param proposals The acceptable proposals.
	 *  @return The ordered acceptable proposals.
	 */
	protected ParticipantProposal[]	orderAcceptables(Object cfp, Object cfp_info, NegotiationRecord[] history, ParticipantProposal[] proposals)
	{
		List comparables	= new ArrayList();
		List uncomparables	= new ArrayList();
		for(int i=0; i<proposals.length; i++)
		{
			if(isValueComparable(proposals[i].getEvaluation()))
			{
				comparables.add(proposals[i]);
			}
			else
			{
				uncomparables.add(proposals[i]);
			}
		}
		
		Collections.sort(comparables, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				ParticipantProposal	prop1	= (ParticipantProposal)o1;
				ParticipantProposal	prop2	= (ParticipantProposal)o2;

				int	ret;
				if(evaluation_comparator!=null)
				{
					ret	= evaluation_comparator.compare(prop1.getEvaluation(), prop2.getEvaluation());
				}
				else
				{
					ret	= ((Comparable)prop1.getEvaluation()).compareTo(prop2.getEvaluation());
				}
				
				return ascending ? ret : -ret;
			}
		});

		comparables.addAll(uncomparables);
		return (ParticipantProposal[])comparables.toArray(new ParticipantProposal[comparables.size()]);
	}

	//-------- helper methods --------
	
	/**
	 *  Test if a value is comparable.
	 */
	private boolean isValueComparable(Object value)
	{
		boolean	ret;
		if(evaluation_comparator!=null)
		{
			try
			{
				evaluation_comparator.compare(value, value);
				ret	= true;
			}
			catch(Exception e)
			{
				ret	= false;
			}
		}
		else
		{
			ret	= value instanceof Comparable;
		}
		return ret;
	}
}
