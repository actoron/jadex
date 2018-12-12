package jadex.commons;

import java.io.Serializable;
import java.util.Arrays;

/**
 *  A filter checks if an object matches
 *  the given subfilters.
 */
public class ComposedFilter<T> implements IFilter<T>,	Serializable
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
	protected IFilter<T>[] filters;

	/** The operator. */
	protected int operator;

	//-------- constructors --------
	
	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedFilter()
	{
		this(new IFilter[0], AND);
	}

	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedFilter(IFilter<T>... filters)
	{
		this(filters, AND);
	}
	
	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedFilter(IFilter<T>[] filters, int operator)
	{
		this.filters	= filters!=null? filters.clone(): null;
		this.operator	= operator;
	}

	//-------- methods --------

	/**
	 *  Match an object against the filter.
	 *  @param object The object.
	 *  @return True, if the filter matches.
	 * @throws Exception
	 */
	public boolean filter(T object)
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
	 *  Get the filters.
	 *  @return the filters.
	 */
	public IFilter<T>[] getFilters()
	{
		return filters;
	}

	/**
	 *  Set the filters.
	 *  @param filters The filters to set.
	 */
	public void setFilters(IFilter<T>[] filters)
	{
		this.filters = filters.clone();
	}
	
	/**
	 *  Add a filter.
	 *  @param filter The filter.
	 */
	public void addFilter(IFilter<T> filter)
	{
		IFilter[] copy = new IFilter[filters==null? 1: filters.length+1];
		if(filters!=null)
			System.arraycopy(filter, 0, copy, 0, filters.length);
		copy[copy.length-1] = filter;
		this.filters = copy;
	}

	/**
	 *  Get the operator.
	 *  @return the operator.
	 */
	public int getOperator()
	{
		return operator;
	}

	/**
	 *  Set the operator.
	 *  @param operator The operator to set.
	 */
	public void setOperator(int operator)
	{
		this.operator = operator;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(filters);
		result = prime * result + operator;
		return result;
	}

	/**
	 *  Test if an object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof ComposedFilter)
		{
			ComposedFilter other = (ComposedFilter)obj;
			ret = Arrays.equals(filters, other.filters) && operator == other.operator;
		}
		return ret;
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
	
}
