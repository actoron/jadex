package jadex.commons;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  An expression tokenizer is able to parse a string with respect
 *  to different nesting-levels (eg. brackets, quotes).
 */
public class ExpressionTokenizer
{
	//-------- constants --------

	/** The escape characters allow to include separators in the tokens.
		E.g. with escape character '\' it is possible to parse "(\))". */
	public static final String	ESCAPE_CHARACTERS	= "\\";

	//-------- attributes --------
	
	/** The source string. */
	protected String source;
	
	/** The top level separators. */
	protected String separators;
	
	/** The nesting level delimiters. */
	protected String open, close;

	/** Return the separators. */
	protected boolean	retsep;

	//-------- internal variables --------

	/** All separators. */
	protected String allseps;

	/** The current tokenization position. */
	protected int pos;

	//-------- constructors ---------
	
	/**
	 *  Create a new tokenizer.
	 *  @param string The string.
	 *  @param separators The separator chars.
	 *  @param metas	The nesting level delimiters.
	 */
	public ExpressionTokenizer(String string, String separators, String[] metas)
	{
		this(string, separators, metas, false);
	}

	/**
	 *  Create a new tokenizer.
	 *  @param string The string.
	 *  @param separators The separator chars.
	 *  @param metas	The nesting level delimiters.
	 *  @param retsep	Return the separator chars.
	 */
	public ExpressionTokenizer(String string, String separators, String[] metas, boolean retsep)
	{
		//System.out.println("exto: "+separators+" | "+string);
		this.source	= string;
		this.separators	= separators;
		this.retsep	= retsep;
		this.open	= ""; 
		this.close	= ""; 
		for(int i=0; i<metas.length; i++)
		{
			this.open	+= metas[i].charAt(0);
			this.close	+= metas[i].charAt(1);
		}
		this.allseps	= separators + open + close;

		this.pos	= 0;
	}

	//-------- tokenization methods --------

	/**
	 *  Get the next token from the string.
	 *  @return The next token.
	 */
	public String	nextToken()
	{
		// Skip leading separators.
		if(separatorAt(pos))
		{
			if(retsep)
			{
				// Return separator as string.
				return String.valueOf(source.charAt(pos++));
			}
			else
			{
				// Skip until no more separator.
				while(separatorAt(++pos));
			}
		}

		StringBuffer	token	= new StringBuffer();
		Vector closings	= new Vector();
		StringTokenizer stok	=
			new StringTokenizer(source.substring(pos), allseps, true);
		String	tmp;

		// Break, when at top-level, and next char is top-level separator.
		while(stok.hasMoreTokens()
			&& (closings.size()>0 || !separatorAt(pos)) )
		{
			tmp	= stok.nextToken();

			//System.out.println("tok: "+tmp);
			//System.out.println("buf: "+token);

			// Test for nesting separators.
			if(nestingSeparatorAt(pos))
			{
				char sepel	= tmp.charAt(0);
				int	index	= closings.lastIndexOf(Character.valueOf(sepel));

				// When matching close separator, remove from list.
				if(index!=-1)
				{
					// Remove any spurious closings, too???
					// e.g. "((" -> ((, but "(("blah"))" -> ((, blah, )) ???
					for(int i2=closings.size()-1; i2>=index; i2--)
					{
						closings.remove(i2);
					}
					//System.out.println("remove: "+closings);
				}
				else
				{
					// When open separator, add matching close to list.
					int	idx	= open.indexOf(sepel);
					if(idx!=-1)
					{
						closings.addElement(Character.valueOf(close.charAt(idx)));
						//System.out.println("added: "+closings);
					}
				}	

			}

			// Append substring and move forward.
			token.append(tmp);
			pos	+= tmp.length();
		}

		return token.toString();
	}

	/**
	 *  Get the remaining tokens as single string.
	 *  When separators are to be returned,
	 *  the remaining string will start with the next separator.
	 *  Otherwise, the separators at the current position are skipped.
	 *  @return The remaining tokens token.
	 */
	public String	remainingTokens()
	{
		// Separators not to be returned?
		if(!retsep)
		{
			// Skip until no more separator.
			while(separatorAt(pos))
			{
				pos++;
			}
		}

		return source.substring(pos);
	}

	/**
	 *  Test if there are more tokens available.
	 */
	public boolean hasMoreTokens()
	{
		// Skip top level tokens.
		int	nextpos	= pos;
		while(nextpos<source.length() && !retsep && separatorAt(nextpos))
		{
			nextpos++;
		}

		return nextpos < source.length();	
	}
	
	/**
	 *  Count the number of tokens.
	 *  @return The number of tokens.
	 */
	public int	countTokens()
	{
		int oldpos	= pos;
		int toks	= 0;
		while(hasMoreTokens())
		{
			nextToken();
			toks++;
		}
		pos	= oldpos;
		return toks;
	}

	/**
	 *  Set the parse position manually.
	 *  Be careful when using this method!.
	 *  @param pos	The new position.
	 */
	// Hack !!! Should provide kindof back() method?
	public void	setPosition(int pos)
	{
		this.pos	= pos;
	}

	//-------- helper methods --------

	/**
	 *  Check if there is a separator character at the specified position.
	 *  @param pos	The position to check.
	 *  @return true, if the position contains a separator.
	 */
	protected boolean	separatorAt(int pos)
	{
		// Check for separator and no escape character before.
		return separators.indexOf(source.charAt(pos))!=-1
			&& (pos==0 || ESCAPE_CHARACTERS.indexOf(source.charAt(pos-1))==-1);
	}

	/**
	 *  Check if there is a nesting level separator character
	 *  at the specified position.
	 *  @param pos	The position to check.
	 *  @return true, if the position contains a separator.
	 */
	protected boolean	nestingSeparatorAt(int pos)
	{
		// Check for nesting separator and no escape character before.
		return (open.indexOf(source.charAt(pos))!=-1
			|| close.indexOf(source.charAt(pos))!=-1)
			&& (pos==0 || ESCAPE_CHARACTERS.indexOf(source.charAt(pos-1))==-1);
	}

	//-------- main for testing --------

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		ExpressionTokenizer etok	= new ExpressionTokenizer
			("get(a, get(x)[a, b]), c, ,\"\\\"\"", ", ", new String[]{"()","[]", "\"\""});

		while(etok.hasMoreTokens())
		{
			System.out.println("found: "+etok.nextToken());
		}

		System.out.println("\n");
		etok	= new ExpressionTokenizer
			("get(a, get(x)[a, b]), c, ,\"\\\"\"", ", ", new String[]{"()","[]", "\"\""}, true);

		while(etok.hasMoreTokens())
		{
			System.out.println("found: "+etok.nextToken());
		}

		String test	= "steppable_dispatcher_step_done :steps \"0\" :state \"applicable candidates\" :applicables (sequence "
			+"\"Tuple[MPlan( name = decrement, waitqueuefilter = null, filter = MExpression(expression = new GoalEventFilter(\\\"keep\\\")), condition = null, initial = false ), RBDIAgent(name=ca)]\")";
		System.out.println("\n"+test);
		etok	= new ExpressionTokenizer(test, ", ", new String[]{"()", "\"\""});
		while(etok.hasMoreTokens())
		{
			System.out.println("found: "+etok.nextToken());
		}
	}
}

