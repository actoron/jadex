package jadex.commons.functional;

/**
 * Functional interface for a function T -> R
 * 
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface Function<T, R>
{
	public R apply(T t);
}
