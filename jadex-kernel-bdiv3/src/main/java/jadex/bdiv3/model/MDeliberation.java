package jadex.bdiv3.model;

import jadex.commons.MethodInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class MDeliberation
{
	/** The cardinality. */
	protected boolean cardinalityone;
	
	/** The inhibited goal types. */
	protected Set<MGoal> inhibitions;

	/** The methods for checking inhibitions. */
	protected Map<String, MethodInfo> inhmethods;
	
	
	/** The inhnames. */
	protected Set<String> inhnames;
	
	/**
	 *	Bean Constructor. 
	 */
	public MDeliberation()
	{
	}
	
	/**
	 *  Create a new deliberation.
	 */
	public MDeliberation(Set<String> inhnames, Map<String, MethodInfo> inhmethods, boolean cardinalityone)
	{
		this.cardinalityone = cardinalityone;
		this.inhnames = inhnames;
		this.inhmethods = inhmethods;
	}
	
	/**
	 * 
	 */
	public void init(MCapability capa)
	{
		if(inhibitions==null && inhnames!=null)
		{
			inhibitions = new HashSet<MGoal>();
			for(String gname: inhnames)
			{
				inhibitions.add(capa.getGoal(gname));
			}
		}
	}

	/**
	 *  Get the cardinalityone.
	 *  @return The cardinalityone.
	 */
	public boolean isCardinalityOne()
	{
		return cardinalityone;
	}
	
	/**
	 *  Set the cardinalityone.
	 *  @param cardinalityone The cardinalityone to set.
	 */
	public void setCardinalityOne(boolean cardinalityone)
	{
		this.cardinalityone = cardinalityone;
	}

	/**
	 *  Get the inhibited.
	 *  @return The inhibited.
	 */
	public Set<MGoal> getInhibitions()
	{
		return inhibitions;
	}

	/**
	 *  Set the inhibitions.
	 *  @param inhibitions The inhibited to set.
	 */
	public void setInhibitions(Set<MGoal> inhibitions)
	{
		this.inhibitions = inhibitions;
	}

	/**
	 *  Get the inhmethods.
	 *  @return The inhmethods.
	 */
	public Map<String, MethodInfo> getInhibitionMethods()
	{
		return inhmethods;
	}

	/**
	 *  Set the inhmethods.
	 *  @param inhmethods The inhmethods to set.
	 */
	public void setInhibitionMethods(Map<String, MethodInfo> inhmethods)
	{
		this.inhmethods = inhmethods;
	}
}
