package jadex.tools.comanalyzer;

import java.io.Serializable;
import java.util.Map;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.SCollection;

/**
 *  A filter checks if an object matches
 *  the given subfilters.
 */
public class ComposedFilter implements IFilter,	Serializable
{
	//-------- constants --------

	/** The AND operator. */
	public static final int AND	= 1;

	/** The OR operator. */
	public static final int OR	= 2;

	/** The NOT operator. */
	public static final int NOT	= 3;
	

	//-------- attributes ---------

	/** The filters */
	protected IFilter[] filters;

	/** The operator. */
	protected int operator;

	//-------- constructors --------

	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedFilter(IFilter[] filters, int operator)
	{
		this.filters	= filters;
		this.operator	= operator;
	}

	//-------- methods --------

	/**
	 *  Match an object against the filter.
	 *  @param object The object.
	 *  @return True, if the filter matches.
	 * @throws Exception
	 */
	public boolean filter(Object object)
	{
		boolean ret	= false;
		if(operator==AND)
		{
			ret = true;
			for(int i=0; i<filters.length && ret; i++)
			{
				ret	= ret && filters[i].filter(object);
			}
		}
		else if(operator==OR)
		{
			// When exception occurs: remember.
			RuntimeException	exception	= null;
			for(int i=0; i<filters.length && !ret; i++)
			{
				try
				{
					ret	= ret || filters[i].filter(object);
				}
				catch(RuntimeException e)
				{
					exception	= exception!=null ? exception : e;
				}
			}

			// Throw remembered exception (if any), when filter doesn't match.
			if(!ret && exception!=null)
				throw exception;
		}
		else if(operator==NOT)
		{
			ret	= !filters[0].filter(object);
		}
		return ret;
	}

	/**
	 *  Create a string representation of this filter.
	 *  @return A string representing this filter.
	 */
	public String	toString()
	{
		StringBuffer	sb	= new StringBuffer();
		sb.append(SReflect.getInnerClassName(this.getClass()));
		sb.append("(operator=");
		sb.append(operatorToString(operator));
		sb.append(", filters=");
		sb.append(SUtil.arrayToString(filters));
		sb.append(")");
		return sb.toString();
	}

	/**
	 *  Create a string representation of the operator.
	 *  @return A string representing the operator.
	 */
	public static String	operatorToString(int operator)
	{
		switch(operator)
		{
			case AND:
				return "AND";
			case OR:
				return "OR";
			case NOT:
				return "NOT";
			default:
				throw new RuntimeException("Unknown operator: "+operator);
 		}
	}

	/**
	 *  Get the encodable representation.
	 */
	public Map getEncodableRepresentation()
	{
		IndexMap	representation = SCollection.createIndexMap();;
		representation.add("isencodeablepresentation", "true"); // to distinguish this map from normal maps.
		representation.add("class", "ComposedFilter");
		representation.add("operator", operatorToString(operator));
		representation.add("filters", SUtil.arrayToString(filters));
		return representation.getAsMap();
	}
}
