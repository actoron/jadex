package jadex.bdi.planlib.protocols;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *  Default selector implementation for evaluating a set of proposals.
 *  The proposals will be first tested on acceptability.
 *  In the second (optional) step they will be sorted
 *  according to the specified comparator/comparable.
 *  In the third step a (sub)set will be filtered out
 *  according to the specified number chooseable of resuls (choose).
 */
public class Selector implements ISelector
{
	//-------- constants --------

	/** The constant for specifiying that all proposals should be chosen. */
	public static final int ALL = -1;

	/** The constant for specifiying that one proposal should be chosen. */
	public static final int ONE = 1;

	//-------- variables --------

	/** The comparator for comparing proposals. */
	protected Comparator comp;

	/** The max number of proposals to be chosen. */
	protected int max_winners;

	//-------- constructors --------

	/**
	 *  Create a new selector.
	 */
	public Selector()
	{
		this(null, ONE);
	}

	/**
	 *  Create a new selector.
	 *  @param comp The optional comparator for sorting proposals.
	 */
	public Selector(Comparator comp)
	{
		this(comp, ONE);
	}

	/**
	 *  Create a new selector.
	 *  @param max_winners The number of proposals to be chosen at most.
	 */
	public Selector(int max_winners)
	{
		this(null, max_winners);
	}

	/**
	 *  Create a new selector.
	 *  @param comp The optional comparator for sorting proposals.
	 *  @param max_winners The number of proposals to be chosen at most.
	 */
	public Selector(Comparator comp, int max_winners)
	{
		this.comp = comp;
		this.max_winners = max_winners;
	}

	//-------- methods --------

	/**
	 *  Select proposals.
	 *
	 *  @param proposals The proposals.
	 *  @return The selected proposal(s) or none.
	 *  todo: include information about negotiation history?
	 */
	public Object[] select(Object[] proposals)
	{
		// Find acceptable proposals.
		Object[] acc_proposals = determineAcceptableProposals(proposals);

		// Sort acceptable proposals.
		sortProposals(acc_proposals);

		// Select a number of proposals.
		Object[] win_proposals = determineWinners(acc_proposals);
		return new Object[]{acc_proposals, win_proposals};
	}

	/**
	 *  Get all acceptable proposals.
	 *  @param proposals The proposals.
	 *  @return The acceptable proposals.
	 */
	protected Object[] determineAcceptableProposals(Object[] proposals)
	{
		List ret = new ArrayList();
		for(int i=0; i<proposals.length; i++)
		{
			if(isAcceptable(proposals[i]))
				ret.add(proposals[i]);
		}
		return ret.toArray();
	}

	/**
	 *  Sort the proposals.
	 *  @param proposals The proposals to sort.
	 */
	protected void sortProposals(Object[] proposals)
	{
		if(proposals.length>1)
		{
			if(comp!=null)
			{
				Arrays.sort(proposals, comp);
			}
			else if(proposals[0] instanceof Comparable)
			{
				Arrays.sort(proposals);
			}
		}
	}

	/**
	 *  Select proposals.
	 *  @param proposals The proposals.
	 *  @return The selected proposals.
	 */
	protected Object[] determineWinners(Object[] proposals)
	{
		List ret = new ArrayList();
		for(int i=0; i<proposals.length; i++)
		{
			if(max_winners==ALL || max_winners>i)
				ret.add(proposals[i]);
		}
		return ret.toArray();
	}
	
	/**
	 *  Test if a proposal is acceptable.
	 *  @param proposal The proposal.
	 *  @return True, if proposal is acceptable.
	 */
	public boolean isAcceptable(Object proposal)
	{
		return true;
	}
}
