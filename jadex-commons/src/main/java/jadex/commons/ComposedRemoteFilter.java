package jadex.commons;

import java.io.Serializable;
import java.util.Arrays;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  A filter checks if an object matches
 *  the given subfilters.
 */
public class  ComposedRemoteFilter<T> implements IAsyncFilter<T>, Serializable
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
	protected IAsyncFilter[] filters;

	/** The operator. */
	protected int operator;

	//-------- constructors --------

	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedRemoteFilter(IAsyncFilter<T>[] filters)
	{
		this(filters, AND);
	}
	
	/**
	 *  Create a composed filter.
	 *  @param filters The filters.
	 *  @param operator The operator.
	 */
	public ComposedRemoteFilter(IAsyncFilter<T>[] filters, int operator)
	{
		this.filters	= filters.clone();
		this.operator	= operator;
	}

	//-------- methods --------

	/**
	 *  Match an object against the filter.
	 *  @param object The object.
	 *  @return True, if the filter matches.
	 * @throws Exception
	 */
	public IFuture<Boolean> filter(Object object)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		if(operator==AND)
		{
			checkAndFilter(object, filters, 0)
				.addResultListener(new DelegationResultListener(ret));
		}
		else if(operator==OR)
		{
			checkOrFilter(object, filters, 0)
				.addResultListener(new DelegationResultListener(ret));
		}
		else if(operator==NOT)
		{
			filters[0].filter(object).addResultListener(new DelegationResultListener<Boolean>(ret)
			{
				public void customResultAvailable(Boolean result)
				{
					ret.setResult(!result.booleanValue());
				}
			});
		}
		return ret;
	}

	/**
	 *  Check and filter.
	 */
	protected IFuture<Boolean> checkAndFilter(final Object object, final IAsyncFilter[] filter, final int i)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		filters[i].filter(object).addResultListener(new DelegationResultListener<Boolean>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
				if(result.booleanValue())
				{
					if(i+1<filter.length)
					{
						checkAndFilter(object, filter, i+1)
							.addResultListener(new DelegationResultListener<Boolean>(ret));
					}
					else
					{
						ret.setResult(Boolean.TRUE);
					}
				}
				else
				{
					ret.setResult(Boolean.FALSE);
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Check or filter.
	 */
	protected IFuture<Boolean> checkOrFilter(final Object object, final IAsyncFilter[] filter, final int i)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		filters[i].filter(object).addResultListener(new DelegationResultListener<Boolean>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
				if(result.booleanValue())
				{
					ret.setResult(Boolean.TRUE);
				}
				else
				{
					if(i+1<filter.length)
					{
						checkOrFilter(object, filter, i+1)
							.addResultListener(new DelegationResultListener<Boolean>(ret));
					}
					else
					{
						ret.setResult(Boolean.FALSE);
					}
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Get the filters.
	 *  @return the filters.
	 */
	public IAsyncFilter[] getFilters()
	{
		return filters;
	}

	/**
	 *  Set the filters.
	 *  @param filters The filters to set.
	 */
	public void setFilters(IAsyncFilter<T>[] filters)
	{
		this.filters = filters.clone();
	}
	
	/**
	 *  Add a filter.
	 *  @param filter The filter.
	 */
	public void addFilter(IAsyncFilter<T> filter)
	{
		IAsyncFilter<T>[] copy = new IAsyncFilter[filters==null? 1: filters.length+1];
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
		if(obj instanceof ComposedRemoteFilter)
		{
			ComposedRemoteFilter<T> other = (ComposedRemoteFilter<T>)obj;
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
