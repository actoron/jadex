package jadex.commons;

/**
 *  Interface for method parameter guessers.
 *  Tries to find a suitable value for each parameter.
 */
public interface IMethodParameterGuesser
{
	/**
	 *  Guess the parameters of a method call. 
	 *  @return The parameters.
	 */
	public Object[] guessParameters(Class<?>[] ptypes, IParameterGuesser parent);
	
//	/**
//	 *  Get the parameter guesser.
//	 *  @return The parameter guesser.
//	 */
//	public IParameterGuesser getGuesser();
	
}
