package jadex.commons.functional;

/**
 * Functional interface for a function T,U -> R
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 */
public interface BiFunction<T,U,R>
{
	public R apply(T t, U u);
}
