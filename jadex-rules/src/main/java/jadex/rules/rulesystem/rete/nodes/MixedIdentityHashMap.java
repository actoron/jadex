package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *  A mixed identity hash map allows to store java objects
 *  using identity and java values (numbers, strings, etc.)
 *  using equality. Rete tuples are always stored by equality. 
 */
public class MixedIdentityHashMap	implements Map
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The equality map. */
	protected Map	equality;
	
	/** The identity map. */
	protected Map	identity;
	
	//-------- constructors --------
	
	/**
	 *  Create a new mixed identity map.
	 */
	public MixedIdentityHashMap(IOAVState state)
	{
		this.state	= state;
		this.equality	= new LinkedHashMap();
		this.identity	= new IdentityHashMap();
	}
	
	//-------- Map interface --------

	public void clear()
	{
		equality.clear();
		identity.clear();
	}
	
	public boolean containsKey(Object key)
	{
		return equality.containsKey(key) || identity.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return equality.containsValue(value) || identity.containsValue(value);
	}
	
	public Object get(Object key)
	{
		return equality.containsKey(key) ? equality.get(key) : identity.get(key); 
	}
	
	public boolean isEmpty()
	{
		return equality.isEmpty() && identity.isEmpty();
	}
	
	public Object put(Object key, Object value)
	{
		OAVObjectType	type	= key!=null && !(key instanceof Tuple) ? state.getType(key) : null;
		return type instanceof OAVJavaType && !OAVJavaType.KIND_VALUE.equals(((OAVJavaType)type).getKind())
			? identity.put(key, value) : equality.put(key, value);
	}
	
	public void putAll(Map map)
	{
		for(Iterator it=map.keySet().iterator(); it.hasNext();)
		{
			Object	key	= it.next();
			put(key, map.get(key));
		}
	}
	
	public Object remove(Object key)
	{
		return equality.containsKey(key) ? equality.remove(key) : identity.remove(key); 
	}
	
	public int size()
	{
		return equality.size() + identity.size();
	}
	
	public Collection values()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Set entrySet()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public Set keySet()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public boolean equals(Object obj)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public int hashCode()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}		
}