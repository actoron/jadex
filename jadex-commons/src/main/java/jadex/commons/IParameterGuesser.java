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
	 *  @param exact Test with exact 
	 *  @return The mapped value. 
	 */
	public Object guessParameter(Class<?> type, boolean exact);
	
//	/**
//	 *  Get the parent guesser.
//	 *  @return The parent guesser.
//	 */
//	public IParameterGuesser getParent();
//	
//	/**
//	 *  Set the parent.
//	 *  @param parent The parent.
//	 */
//	public void setParent(IParameterGuesser parent);
}
