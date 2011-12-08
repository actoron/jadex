package jadex.commons;

/**
 *  Generic version of tuple for two elements.
 */
public class Tuple2<T, E> extends Tuple
{
	/**
	 *  Convenience constructor for binary tuples.
	 *  @param entity1	The first object in the tuple.
	 *  @param entity2	The second object in the tuple.
	 */
	public Tuple2(T entity1, E entity2)
	{
		super(new Object[]{entity1, entity2});
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
	
	//-------- Cloneable interface --------

	/**
	 *  Clone this tuple.
	 *  @return A shallow copy of this tuple.
	 */
	public Object	clone()
	{
		return new Tuple2<T, E>(getFirstEntity(), getSecondEntity());
	}
}
