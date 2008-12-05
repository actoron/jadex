package jadex.rules.rulesystem.rete.nodes;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
		this.equality	= new LinkedHashSet();
		this.identity	= Collections.newSetFromMap(new IdentityHashMap());
	}
	
	//-------- Set interface --------

	public void clear()
	{
		equality.clear();
		identity.clear();
	}
	
	public boolean contains(Object value)
	{
		return equality.contains(value) || identity.contains(value);
	}
	
	public boolean isEmpty()
	{
		return equality.isEmpty() && identity.isEmpty();
	}
	
	public boolean add(Object value)
	{
		OAVObjectType	type	= value!=null && !(value instanceof Tuple) ? state.getType(value) : null;
		return type instanceof OAVJavaType && !OAVJavaType.KIND_VALUE.equals(((OAVJavaType)type).getKind())
			? identity.add(value) : equality.add(value);
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
		return equality.contains(value) ? equality.remove(value) : identity.remove(value); 
	}
	
	public int size()
	{
		return equality.size() + identity.size();
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
			Iterator it1 = equality.iterator();
			Iterator it2 = identity.iterator();
			
			public boolean hasNext()
			{
				return it1.hasNext() || it2.hasNext();
			}
			
			public Object next()
			{
				Object ret = null;
				if(it1.hasNext())
				{
					ret = it1.next();
				}
				else if(it2.hasNext())
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
		return (Object[]) SUtil.joinArrays(equality.toArray(), identity.toArray());
	}
	
	public Object[] toArray(Object[] ret)
	{
		if(ret.length>=size())
		{
			Object[]	evals	= equality.toArray();
			Object[]	ivals	= identity.toArray();
			System.arraycopy(evals, 0, ret, 0, evals.length);
			System.arraycopy(ivals, 0, ret, evals.length, ivals.length);
		}
		else
		{
			ret	= toArray();
		}
		return ret;
	}
	
	public boolean equals(Object obj)
	{
		assert !(obj instanceof MixedIdentityHashSet) && obj instanceof Collection;
		return obj instanceof MixedIdentityHashSet
			&& equality.equals(((MixedIdentityHashSet)obj).equality)
			&& identity.equals(((MixedIdentityHashSet)obj).identity);
	}
	
	public int hashCode()
	{
		return equality.hashCode() + identity.hashCode();
	}		
}