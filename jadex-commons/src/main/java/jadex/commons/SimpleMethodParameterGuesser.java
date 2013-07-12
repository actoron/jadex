package jadex.commons;

import java.util.Collection;


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
	public SimpleMethodParameterGuesser(Class<?>[] ptypes, Collection<?> vals)
	{
		this(ptypes, new SimpleParameterGuesser(vals));
	}
	
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
					ret[i] = pguesser.guessParameter(ptypes[i], true);
					if(ret[i]==null)
					{
						ret[i] = pguesser.guessParameter(ptypes[i], false);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Get the guesser.
	 *  @return The guesser.
	 */
	public IParameterGuesser getGuesser()
	{
		return pguesser;
	}
}
