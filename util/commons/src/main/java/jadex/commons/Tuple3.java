package jadex.commons;

/**
 *  Generic version of tuple for two elements.
 */
@SuppressWarnings("serial")
public class Tuple3<T, E, F> extends Tuple
{
	/**
	 *  Convenience constructor for binary tuples.
	 *  @param entity1	The first object in the tuple.
	 *  @param entity2	The second object in the tuple.
	 */
	public Tuple3(T entity1, E entity2, F entity3)
	{
		super(new Object[]{entity1, entity2, entity3});
	}
	
	/**
	 *  Get the first entity.
	 *  @return The first entity.
	 */
	public T getFirstEntity()
	{
		return (T)getEntity(0);
	}
	
	/**
	 *  Get the second entity.
	 *  @return The second entity.
	 */
	public E getSecondEntity()
	{
		return (E)getEntity(1);
	}
	
	/**
	 *  Get the second entity.
	 *  @return The second entity.
	 */
	public F getThirdEntity()
	{
		return (F)getEntity(2);
	}
	
	//-------- Cloneable interface --------

	/**
	 *  Clone this tuple.
	 *  @return A shallow copy of this tuple.
	 */
	public Object	clone()
	{
		return new Tuple3<T, E, F>(getFirstEntity(), getSecondEntity(), getThirdEntity());
	}
}
