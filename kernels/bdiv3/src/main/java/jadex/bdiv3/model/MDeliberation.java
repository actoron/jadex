package jadex.bdiv3.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;

/**
 *  Model element for inhibitions.
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
	
	//-------- additional xml attributes --------
	
	/** The methods for checking inhibitions. */
	protected Map<String, UnparsedExpression> inhexpressions;
	
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
	
//	/**
//	 *  Resolve the inhibitions from inhibition names.
//	 */
//	public void init(MCapability capa)
//	{
//		if(inhibitions==null && inhnames!=null)
//		{
//			inhibitions = new HashSet<MGoal>();
//			for(String gname: inhnames)
//			{
//				inhibitions.add(capa.getGoal(gname));
//			}
//		}
//	}

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
//	public Set<MGoal> getInhibitions()
	public Set<MGoal> getInhibitions(MCapability capa)
	{
		if(inhibitions==null)
		{
			if(inhnames!=null)
			{
				inhibitions = new HashSet<MGoal>();
				for(String gname: inhnames)
				{
					inhibitions.add(capa.getGoal(gname));
				}
			}
			// xml version
			else if(inhexpressions!=null)
			{
				inhibitions = new HashSet<MGoal>();
				for(UnparsedExpression gexp: inhexpressions.values())
				{
					inhibitions.add(capa.getGoal(gexp.getName()));
				}
			}
		}
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
	
	/**
	 *  Add an inhibition name.
	 *  @param inhname The inhibition name.
	 */
	public void addInhibitionName(String inhname)
	{
		if(inhnames==null)
			inhnames = new HashSet<String>();
		inhnames.add(inhname);
	}
	
	/**
	 *  Add an inhibition expression.
	 *  @param inhname The inhibition expression.
	 */
	public void addInhibitionExpression(UnparsedExpression inhexp)
	{
		if(inhexpressions==null)
			inhexpressions = new HashMap<String, UnparsedExpression>();
		inhexpressions.put(inhexp.getName(), inhexp);
	}

	/**
	 *  Get the inhibition expressions.
	 *  @return The inhexpressions
	 */
	public Map<String, UnparsedExpression> getInhibitionExpressions()
	{
		return inhexpressions;
	}
}
