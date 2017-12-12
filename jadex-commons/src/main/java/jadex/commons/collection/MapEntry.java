package jadex.commons.collection;

import java.util.Map;
import java.util.Objects;

/**
 *  Replacement for Map.Entry which cannot be instantiated :-(
 */
public class MapEntry<K,V> implements Map.Entry<K, V>
{
	protected K k;
	protected V v;

	/**
	 *  Create a new map entry.
	 *  @param k The key.
	 *  @param v The value.
	 */
    public MapEntry(K k, V v)
	{
		super();
		this.k = k;
		this.v = v;
	}

    /**
     *  Get the key.
     */
	public K getKey()
    {
    	return k;
    }

	/**
	 *  Get the value.
	 */
    public V getValue()
    {
    	return v;
    }

    /**
     *  Set the value.
     */
    public V setValue(V value)
    {
    	V old = v;
    	this.v = value;
    	return old;
    }

    /**
     *  Get the hashcode.
     */
    public final int hashCode() 
    {
        return (k!=null ? k.hashCode() : 0) ^ (v!=null ? v.hashCode() : 0);
    }

    /**
     *  Test if equal.
     */
    public final boolean equals(Object o) 
    {
        if(o == this)
            return true;
        if(o instanceof Map.Entry) 
        {
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            if(equals(k, e.getKey()) &&
                equals(v, e.getValue()))
                return true;
        }
        return false;
    }

    /**
     * Equals with null check.
     * @param var0
     * @param var1
     * @return
     */
    private static boolean equals(Object var0, Object var1) {
        return var0 == var1 || var0 != null && var0.equals(var1);
    }
}

