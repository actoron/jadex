package jadex.commons;

/**
 *  Interface for parameter guessers.
 *  Try to map between type and value.
 */
public interface IParameterGuesser
{
	/**
	 *  Guess a parameter.
	 *  @param type The type.
	 *  @return The mapped value. 
	 *  (Throws exception if no value could be found to support null value).
	 */
	public Object guessParameter(Class<?> type);
}
