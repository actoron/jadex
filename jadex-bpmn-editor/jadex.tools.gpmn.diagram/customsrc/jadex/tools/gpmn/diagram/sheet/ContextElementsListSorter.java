package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.ContextElement;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class ContextElementsListSorter extends ViewerSorter
{
	/** Sort by name */
	public final static int SORT_BY_NAME = 1;

	/** Sort by type */
	public final static int SORT_BY_TYPE = 2;

	/** Sort by value */
	public final static int SORT_BY_VALUE = 3;

	/** Sort by set */
	public final static int SORT_BY_SET = 4;

	/** Sort by dynamic */
	public final static int SORT_BY_DYNAMIC = 5;

	// ----- attributes -----

	/** Sort criteria for this instance */
	private int criteria;

	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 * 
	 * @param criteria
	 *            the sort criterion to use
	 */
	public ContextElementsListSorter(int criteria)
	{
		super();
		this.criteria = criteria;
	}

	/**
	 * @see ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, Object,
	 *      Object)
	 */
	public int compare(Viewer viewer, Object o1, Object o2)
	{

		if (o1 instanceof Object[] && o2 instanceof Object[])
		{
			if (((Object[]) o1)[0] instanceof ContextElement
					&& ((Object[]) o2)[0] instanceof ContextElement)
			{
				ContextElement elm1 = (ContextElement) ((Object[]) o1)[0];
				ContextElement elm2 = (ContextElement) ((Object[]) o2)[0];

				switch (criteria)
				{
				case SORT_BY_NAME:
					return compareName(elm1, elm2);
				case SORT_BY_TYPE:
					return compareType(elm1, elm2);
				case SORT_BY_VALUE:
					return compareValue(elm1, elm2);
				case SORT_BY_SET:
					return compareBoolean(elm1.isSet(), elm2.isSet());
				case SORT_BY_DYNAMIC:
					return compareBoolean((Boolean)((Object[]) o1)[1], (Boolean)((Object[]) o1)[1]);
				default:
					return 0;
				}
			}
		}
		return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given
	 * ContextElements based on the name.
	 * 
	 * @param elm1
	 * @param elm2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareName(ContextElement elm1, ContextElement elm2)
	{
		return super.getComparator().compare(elm1.getName(), elm2.getName());
	}

	/**
	 * Returns a number reflecting the collation order of the given
	 * ContextElements based on the type.
	 * 
	 * @param elm1
	 * @param elm2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int compareType(ContextElement elm1, ContextElement elm2)
	{
		return super.getComparator().compare(elm1.getType(), elm2.getType());
	}

	/**
	 * Returns a number reflecting the collation order of the given
	 * ContextElements based on the value.
	 * 
	 * @param elm1
	 * @param elm2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int compareValue(ContextElement elm1, ContextElement elm2)
	{
		return super.getComparator().compare(elm1.getInitialValue(),elm1.getInitialValue());
	}
	
	/**
	 * Returns a number reflecting the order of the given boolean
	 * 
	 * @param elm1
	 * @param elm2
	 * @return a negative number if the first element is true and the second
	 *         element is false; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is false and the second element is true
	 */
	private int compareBoolean(Boolean elm1, Boolean elm2)
	{
		//return super.getComparator().compare(elm1.isSet(), elm2.isSet());
		
		if (elm1.booleanValue() && elm2.booleanValue())
		{
			return 0;
		}
		else
		{
			if (elm1.booleanValue()) 
			{ 
				return -1; 
			}
			else
			{ 
				return 1; 
			}
		}
	}

	/**
	 * Returns the sort criteria of this this sorter.
	 * 
	 * @return the sort criterion
	 */
	public int getCriteria()
	{
		return criteria;
	}
}
