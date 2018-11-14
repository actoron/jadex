package jadex.commons.functional;

/**
 * Functional interface for a consumer T -> ()
 * 
 * @param <T> the type of the input to the function
 */
public interface Consumer<T>
{
	public void accept(T t);
}
