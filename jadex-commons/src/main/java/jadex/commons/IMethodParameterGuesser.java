package jadex.commons;

/**
 *  Interface for method parameter guessers.
 *  Tries to find a suitable value for each parameter.
 */
public interface IMethodParameterGuesser
{
	/**
	 *  Guess the parameters of a method call. 
	 */
	public Object[] guessParameters();
	
}
