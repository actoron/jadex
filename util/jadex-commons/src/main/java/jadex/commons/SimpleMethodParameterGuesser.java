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

//	/** The parameter types. */
//	protected Class<?>[] ptypes;
	
	/**
	 *  Create a new guesser.
	 */
	public SimpleMethodParameterGuesser(Collection<?> vals)//Class<?>[] ptypes, Collection<?> vals)
	{
		this(new SimpleParameterGuesser(vals));
	}
	
	/**
	 *  Create a new guesser.
	 */
	public SimpleMethodParameterGuesser(IParameterGuesser pguesser)
	{
		this.pguesser = pguesser;
	}
	
	/**
	 *  Guess the parameters of a method call. 
	 */
	public Object[] guessParameters(Class<?>[] ptypes, IParameterGuesser parent)
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
					if(parent!=null)
					{
						ret[i] = parent.guessParameter(ptypes[i], true);
						if(ret[i]==null)
						{
							ret[i] = parent.guessParameter(ptypes[i], false);
						}
					}
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
