package jadex.commons;

/**
 * Encapsulates an Object to make it mutable for anonymous classes and lambdas.
 * 
 * @author &#64;wolfposd
 * 
 * @param <T>
 */
public class MutableObject<T>
{
	private T mutableObject;

	/**
	 * Creates a new MutableObject
	 * 
	 * @param mutable
	 */
	public MutableObject(T mutable)
	{
		mutableObject = mutable;
	}

	/**
	 * Creates a new MutableObject
	 * 
	 * @param mutable
	 */
	public static <T> MutableObject<T> create(T mutable)
	{
		return new MutableObject<T>(mutable);
	}

	public void set(T mutableObject)
	{
		this.mutableObject = mutableObject;
	}

	public T get()
	{
		return mutableObject;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (mutableObject != null)
			return mutableObject.equals(obj);
		else
			return false;
	}

	@Override
	public int hashCode()
	{
		if (mutableObject != null)
			return mutableObject.hashCode();
		else
			return 0;
	}

	@Override
	public String toString()
	{
		if (mutableObject != null)
			return "MutableObject<" + mutableObject.getClass().getSimpleName() + "> [value=" + mutableObject + "]";
		else

			return "MutableObject<NULL> [value=" + mutableObject + "]";
	}

}
