package jadex.rules.rulesystem.rete;

import java.util.LinkedList;
import java.util.List;

import jadex.commons.SReflect;
import jadex.rules.state.IOAVState;

/**
 *  A tuple stores a variable binding and optionally has
 *  a pointer to another predecessor tuple.
 */
public class Tuple
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The object. */
	protected Object object;
	
	/** The tuple pointer. */
	protected Tuple last;
	
	/** The size. */
	protected int size;
	
	/** The hashcode (cached for speed). */
	// Hack!!! Assumes that hashcodes of assigned objects are fix.
	protected final int hashcode;
	
	//-------- constructors --------
	
	/**
	 *  Create a new tuple.
	 */
	public Tuple(IOAVState state, Tuple last, Object object)
	{
		this.state = state;
		this.object = object;
		this.last = last;
		this.size = last==null? 1: last.size()+1;
		int hash = 31 + ((last == null) ? 0 : last.hashCode());
		hash = 31 * hash + ((object == null) ? 0 : object.hashCode());
		this.hashcode	= hash;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the object.
	 *  @return The object.
	 */
	public Object getObject()
	{
		return object;
	}

	/**
	 *  Get the last tuple.
	 *  @return The last tuple.
	 */
	public Tuple getLastTuple()
	{
		return last;
	}
	
	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public int size()
	{
		return size;
	}

	/**
	 *  Get the value at the index.
	 *  @param index The index. 
	 */
	public Object getObject(int index)
	{
		Tuple target = this;
		
		for(int i=size-1; i>index; i--)
			target = target.getLastTuple();
	
		return target.getObject();
	}
	
	/**
	 *  Get the values.
	 */
	public List getObjects()
	{
		List ret = new LinkedList();
		Tuple current = this;
		
		while(current!=null)
		{
			ret.add(0, current.getObject());
			current = current.getLastTuple();		
		}
		return ret;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return hashcode;
	}

	/**
	 *  Test for equality.
	 *  True, if both objects are equal.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;
		
		boolean ret = obj!=null && hashcode==obj.hashCode() && obj instanceof Tuple;
		if(ret)
		{
			Tuple	tuple1	= this;
			Tuple	tuple2 = (Tuple)obj;
			
//			ret	= Srules.equals(this.last, tuple2.last)
//				&& Srules.equals(this.object, tuple2.object);
			ret	= tuple1.size==tuple2.size;
			while(ret && tuple1!=null)
			{
				ret = state.equals(tuple1.object, tuple2.object);
				tuple1	= tuple1.last;
				tuple2	= tuple2.last;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass())+"(values="+getObjects()+")";
	}
}
