package jadex.rules.rulesystem.rete.nodes;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.commons.collection.IdentityHashSet;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

/**
 *  A mixed identity hash set allows to store java objects
 *  using identity and java values (numbers, strings, etc.)
 *  using equality. Rete tuples are always stored by equality. 
 */
public class MixedIdentityHashSet	implements Set
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The equality map. */
	protected Set	equality;
	
	/** The identity map. */
	protected Set	identity;
	
	//-------- constructors --------
	
	/**
	 *  Create a new mixed identity map.
	 */
	public MixedIdentityHashSet(IOAVState state)
	{
		this.state	= state;
	}
	
	//-------- Set interface --------

	public void clear()
	{
		if(equality!=null) equality.clear();
		if(identity!=null) identity.clear();
	}
	
	public boolean contains(Object value)
	{
		return equality!=null && equality.contains(value) || identity!=null && identity.contains(value);
	}
	
	public boolean isEmpty()
	{
		return (equality==null || equality.isEmpty()) && (identity==null ||	identity.isEmpty());
	}
	
	public boolean add(Object value)
	{
		OAVObjectType	type	= value!=null && !(value instanceof Tuple) ? state.getType(value) : null;
		boolean	ret;
		if(type instanceof OAVJavaType && !OAVJavaType.KIND_VALUE.equals(((OAVJavaType)type).getKind()))
		{
			if(identity==null)
				identity	= new IdentityHashSet();
			ret	= identity.add(value);
		}
		else
		{
			if(equality==null)
				equality	= new LinkedHashSet();
			ret	= equality.add(value);
		}
		return ret;
	}
	
	public boolean	addAll(Collection coll)
	{
		boolean	ret	= false;
		for(Iterator it=coll.iterator(); it.hasNext();)
		{
			ret	= add(it.next()) || ret;
		}
		return ret;
	}
	
	public boolean remove(Object value)
	{
		return equality!=null && equality.contains(value) ? equality.remove(value) : identity!=null ? identity.remove(value) : false; 
	}
	
	public int size()
	{
		return (equality!=null ? equality.size() : 0) + (identity!=null ? identity.size() : 0);
	}
	
	public boolean containsAll(Collection coll)
	{
		boolean	ret	= true;
		for(Iterator it=coll.iterator(); ret && it.hasNext();)
		{
			ret	= contains(it.next());
		}
		return ret;
	}
	
	public Iterator iterator()
	{
		return new Iterator()
		{
			Iterator it1 = equality!=null ? equality.iterator() : null;
			Iterator it2 = identity!=null ? identity.iterator() : null;
			
			public boolean hasNext()
			{
				return it1!=null && it1.hasNext() || it2!=null && it2.hasNext();
			}
			
			public Object next()
			{
				Object ret = null;
				if(it1!=null && it1.hasNext())
				{
					ret = it1.next();
				}
				else if(it2!=null && it2.hasNext())
				{
					ret = it2.next();
				}
				else
				{
					throw new RuntimeException("No next element.");
				}
				return ret;
			}
			
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public boolean removeAll(Collection coll)
	{
		boolean	ret	= false;
		for(Iterator it=coll.iterator(); it.hasNext();)
		{
			ret	= remove(it.next()) || ret;
		}
		return ret;
	}
	
	public boolean retainAll(Collection coll)
	{
		boolean	ret	= false;
		Object[]	values	= toArray();
		for(int i=0; i<values.length; i++)
		{
			if(!coll.contains(values[i]))
			{
				ret	= true;
				remove(values[i]);
			}
		}
		return ret;
	}
	
	public Object[] toArray()
	{
		return equality!=null && identity!=null ? (Object[]) SUtil.joinArrays(equality.toArray(), identity.toArray())
			: equality!=null ? equality.toArray() : identity!=null ? identity.toArray() : new Object[0];
	}
	
	public Object[] toArray(Object[] ret)
	{
		if(equality!=null && identity!=null)
		{
			if(ret.length<size())
			{
				ret	= (Object[])Array.newInstance(ret.getClass().getComponentType(), size());
			}
			Object[]	evals	= equality.toArray();
			Object[]	ivals	= identity.toArray();
			System.arraycopy(evals, 0, ret, 0, evals.length);
			System.arraycopy(ivals, 0, ret, evals.length, ivals.length);
		}
		else
		{
			ret	= equality!=null ? equality.toArray(ret) : identity!=null ? identity.toArray(ret) : ret;
		}
		return ret;
	}
	
	public boolean equals(Object obj)
	{
		return obj instanceof MixedIdentityHashSet
			&& SUtil.equals(equality, ((MixedIdentityHashSet)obj).equality)
			&& SUtil.equals(identity, ((MixedIdentityHashSet)obj).identity);
	}
	
	public int hashCode()
	{
		return (equality!=null ? equality.hashCode() : 0) + (identity!=null ? identity.hashCode() : 0);
	}
    
    /**
     *  Create a string representation.
     */
    public String	toString()
    {
    	StringBuffer	ret	= new StringBuffer("{");
    	for(Iterator it=iterator(); it.hasNext();)
    	{
    		ret.append(it.next());
    		if(it.hasNext())
    			ret.append(",");
    	}
    	ret.append("}");
    	return ret.toString();
    }
}