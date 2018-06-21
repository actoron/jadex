package jadex.javaparser.javaccimpl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.collection.SortedList;


/**
 *  Node for OQL like select statements.
 */
public class SelectNode	extends ExpressionNode
{
	//-------- constants --------

	/** The selection mode for returning a set of elements (default). */
	public static final	int	ALL	= 1;

	/** The selection mode for returning the first matching element. */
	public static final	int	ANY	= 2;

	/** The selection mode for returning a single matching element. */
	public static final	int	IOTA	= 3;

	/** The ascending order direction. */
	public static final	int	ASC	= 1;

	/** The descending order direction. */
	public static final	int	DESC	= 2;

	//-------- attributes --------

	/** The selection mode. */
	protected int	mode;

	/** The variable names. */
	protected String[]	vars;

	/** The flag indicating presence of a where clause. */
	protected boolean	where;

	/** The flag indicating presence of an order by clause. */
	protected boolean	orderby;

	/** The order direction. */
	protected int	order;

	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public SelectNode(ParserImpl p, int id)
	{
		super(p, id);
		this.mode	= ALL;
		this.order	= ASC;
	}

	//-------- attribute accessors --------

	/**
	 *  Set the token text.
	 *  @param text	The token text.
	 */
	public void	setText(String text)
	{
		super.setText(text);
		this.mode	= fromString(text);
	}

	/**
	 *  Set the variable names.
	 */
	public void	setVariables(String[] vars)
	{
		this.vars	= vars;
	}

	/**
	 *  Set the where clause flag.
	 */
	public void	setWhere(boolean where)
	{
		this.where	= where;
	}

	/**
	 *  Set the where order by flag.
	 */
	public void	setOrderBy(boolean orderby)
	{
		this.orderby	= orderby;
	}

	/**
	 *  Set the ordering direction.
	 */
	public void	setOrder(String order)
	{
		this.order	= orderFromString(order);
	}

	//-------- evaluation --------

	/**
	 *  Precompile the node.
	 */
	public void	precompile()
	{
		// Todo: ???
	}

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(final IValueFetcher fetcher) //throws Exception
	{
		// Create result set container.
		AbstractList	results;
		SortedList	orders;
		if(orderby)
		{
			orders	= new SortedList(order==ASC);
			results	= new LinkedList();
		}
		else
		{
			results	= new ArrayList();
			orders	= null;
		}

		// Evaluate all subnodes.
		int	subnode	= 0;

		// Ignore return type declaration. (hack!!!)
		if(jjtGetChild(subnode) instanceof TypeNode)
			subnode++;
		
		// The element (select clause).
		ExpressionNode	element	= (ExpressionNode)jjtGetChild(subnode++);

		// Check if select clause depends on at least one variable,
		// otherwise print warning.
		String	select	= element.toPlainString();
		boolean	contained	= false;
		for(int i=0; !contained && i<vars.length; i++)
			contained	= select.indexOf(vars[i])!=-1;
		if(!contained)
		{
			System.out.println("Warning: Selection expression does not refer to any variables: "+toPlainString());
		}

		// The collections (from clause).
		Object[]	collections	= new Object[vars.length];
		Iterator[]	iterators	= new Iterator[vars.length];
		
//		Map	params2	= SCollection.createNestedMap(params);	// Local parameters.
		
		final Map params2 = SCollection.createHashMap();
		for(int i=0; i<vars.length; i++)
		{
			// Ignore return type declaration. (hack!!!)
			if(jjtGetChild(subnode) instanceof TypeNode)
				subnode++;

			// Evaluate collection expression.
			collections[i]	= ((ExpressionNode)jjtGetChild(subnode++))
				.getValue(fetcher);
			// Create iterator over collection.
			iterators[i]	= SReflect.getIterator(collections[i]);
			
			if(iterators[i].hasNext())
			{
				// Initial local parameters.
				params2.put(vars[i], iterators[i].next());
			}
			else
			{
				// When one collection is empty, empty result is returned.
				// Hack !!!
				if(mode==ALL)
				{
					return results;
				}
				else
				{
					return null;
				}
			}
		}
		IValueFetcher f2 = new IValueFetcher()
		{
			public Object fetchValue(String name)
			{
				Object ret = params2.get(name);
				if(ret==null && !params2.containsKey(name))	
					ret = fetcher.fetchValue(name);
				return ret;
			}
			
//			public Object fetchValue(String name, Object object)
//			{
//				return fetcher.fetchValue(name, object);
//			}
		};

		// The condition (where clause, if any).
		ExpressionNode	condition	= where	? (ExpressionNode)jjtGetChild(subnode++) : null;

		// The order expression (if any).
		ExpressionNode	orderexp	= orderby	? (ExpressionNode)jjtGetChild(subnode++) : null;

		while(true)
		{
			// Evaluate where clause, if any.
			if(!where ||
				((Boolean)condition.getValue(f2)).booleanValue())
			{
				// Check unique match for iota.
				if(mode==IOTA && results.size()>0)
				{
					throw new RuntimeException("No unique element for iota selection: "+this);
				}

				// Evaluate result element.
				Object	result	= element.getValue(f2);
				if(orderby)
				{
					// Use insertion order index for adding elements.
					// For ASC add insert from end of list,
					// for DESC add insert from start of list.
					int	index	= orders.insertElement(
						order==ASC ? orders.size() : 0,
						orderexp.getValue(f2));
					results.add(index, result);
				}
				else
				{
					results.add(result);
				}

				// For any selects, return with first matching element.
				if(mode==ANY && !orderby)
				{
					break;
				}
			}

			// Calculate next join tuple (stored as local parameters).
			int i=0;
			for(; i<iterators.length && !iterators[i].hasNext(); i++)
			{
				// Overflow: Re-init iterator.
				iterators[i]	= SReflect.getIterator(collections[i]);
				params2.put(vars[i], iterators[i].next());
			}
			if(i<iterators.length)
			{
				params2.put(vars[i], iterators[i].next());
			}
			else
			{
				// Overflow in last iterator: done.
				// Hack: Unnecessarily re-inits all iterators before break ?
				break;
			}
		}

		if(mode==ALL)
		{
			return results;
		}
		else
		{
			if(results.size()>0)
				return results.iterator().next();
			else
				return null;
		}
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		int	subnode	= 0;
		String	ret	= "SELECT " + toString(mode) + " " + subnodeToString(subnode++)
			+ " FROM ";
		for(int i=0; i<vars.length; i++)
		{
			if(i!=0)
				ret	+= ", ";
			ret	+= vars[i] + " IN " + subnodeToString(subnode++);
		}
		if(where)
			ret	+= " WHERE " + subnodeToString(subnode++);
		if(orderby)
		{
			ret	+= " ORDER BY " + subnodeToString(subnode++);
			if(order==DESC) ret+=" DESC";
		}

		return ret;			
	}

	/**
	 *  Get unbound parameter nodes.
	 *  @return The unbound parameter nodes.
	 */
	public ParameterNode[]	getUnboundParameterNodes()
	{
		// Exclude nodes from select variables.
		ParameterNode[]	param	= super.getUnboundParameterNodes();
		ArrayList	ret	= new ArrayList();
		for(int i=0; i<param.length; i++)
		{
			boolean	found	= false;
			for(int j=0; !found && j<vars.length; j++)
			{
				found	= param[i].getText().equals(vars[j]);
			}
			if(!found)
			{
				ret.add(param[i]);
			}
		}
		return (ParameterNode[])ret.toArray(new ParameterNode[ret.size()]);
	}

	//-------- static part --------

	/**
	 *  Convert a selection mode to a string representation.
	 *  @param mode	The mode
	 *  @return A string representation of the mode.
	 */
	public static String	toString(int mode)
	{
		switch(mode)
		{
			case ALL:
				return "ALL";
			case ANY:
				return "ANY";
			case IOTA:
				return "IOTA";
			default:
				return ""+mode;
		}
	}

	/**
	 *  Convert an ordering direction to a string representation.
	 *  @param order	The ordering direction
	 *  @return A string representation of the ordering direction.
	 */
	public static String	orderToString(int order)
	{
		switch(order)
		{
			case ASC:
				return "ASC";
			case DESC:
				return "DESC";
			default:
				return ""+order;
		}
	}

	/**
	 *  Convert a selection mode from a string representation.
	 *  @param mode	The mode as string.
	 *  @return The int value of the mode.
	 */
	public static int	fromString(String mode)
	{
		if("ALL".equalsIgnoreCase(mode))
		{
			return ALL;
		}
		else if("ANY".equalsIgnoreCase(mode))
		{
			return ANY;
		}
		else if("IOTA".equalsIgnoreCase(mode))
		{
			return IOTA;
		}
		else
		{
			throw new ParseException("Unknown selection mode: "+mode);
		}
	}

	/**
	 *  Convert an ordering direction from a string representation.
	 *  @param order	The ordering direction as string.
	 *  @return The int value of the ordering direction.
	 */
	public static int	orderFromString(String order)
	{
		if("ASC".equalsIgnoreCase(order))
		{
			return ASC;
		}
		else if("DESC".equalsIgnoreCase(order))
		{
			return DESC;
		}
		else
		{
			throw new ParseException("Unknown ordering direction: "+order);
		}
	}


	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && mode==((SelectNode)o).mode
			&& order==((SelectNode)o).order
			&& orderby==((SelectNode)o).orderby
			&& where==((SelectNode)o).where
			&& (vars==null && ((SelectNode)o).vars==null
				|| vars!=null && ((SelectNode)o).vars!=null && Arrays.equals(vars, ((SelectNode)o).vars));
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		int	ret	= super.hashCode();
		ret	= ret*31 + mode;
		ret	= ret*31 + order;
		ret	= ret*31 + (orderby ? 1 : 2);
		ret	= ret*31 + (where ? 1 : 2);
		ret	= ret*31 + (vars!=null ? 1+Arrays.hashCode(vars) : 1);
		return ret;
	}
}

