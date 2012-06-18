package jadex.base.gui;

import jadex.commons.IValidator;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;


/**
 *  A validator that tries to parse the text of a textfield.
 */
public class ParserValidator implements IValidator
{
	//-------- attributes --------
	
	/** Flag indicating the last valid state. */
	protected boolean	lastvalid;

	/** The text corresponding to the last valid state. */
	protected String	lasttext;
	
	/** The parser. */
	protected IExpressionParser parser;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- constructors --------

	/**
	 *  Create a parser validator.
	 */
	public ParserValidator(ClassLoader classloader)
	{
		this.lastvalid	= true;
		this.lasttext	= null;
		this.parser = new JavaCCExpressionParser();
		this.classloader = classloader;
	}
	
	//-------- IValidator interface --------

	/**
	 *  Return true when the given object is valid.
	 */
	public boolean isValid(Object object)
	{
		if(object instanceof String)
		{
			String	text	= (String) object;
			if(lasttext==null || !lasttext.equals(text))
			{
				lasttext	= text;
				if(text.length()!=0)
				{
					try
					{
						IParsedExpression pexp = parser.parseExpression(text, null, null, classloader);
						pexp.getValue(null);
						lastvalid	= true;
					}
					catch(Exception e)
					{
						lastvalid	= false;
					}
				}
				else
				{
					lastvalid	= true;
				}
			}
		}
		else
		{
			lastvalid	= false;
			lasttext	= null;
		}

		return lastvalid;
	}
}