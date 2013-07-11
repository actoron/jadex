package jadex.commons;


/**
 *  Simple method parameter guesser that uses a parameter guesser
 *  to resolve the single parameter guess requests.
 */
public class SimpleMethodParameterGuesser implements IMethodParameterGuesser
{
	/** The parameter guesser. */
	protected IParameterGuesser pguesser;

	/** The parameter types. */
	protected Class<?>[] ptypes;
	
	/**
	 *  Create a new guesser.
	 */
	public SimpleMethodParameterGuesser(Class<?>[] ptypes, IParameterGuesser pguesser)
	{
		this.ptypes = ptypes;
		this.pguesser = pguesser;
	}
	
	/**
	 *  Guess the parameters of a method call. 
	 */
	public Object[] guessParameters()
	{
		Object[] ret = null;
		
		if(ptypes==null)
		{
			ret = new Object[0];
		}
		else
		{
			ret = new Object[ptypes.length];
			for(int i=0; i<ptypes.length; i++)
			{
				try
				{
					ret[i] = pguesser.guessParameter(ptypes[i]);
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
}
