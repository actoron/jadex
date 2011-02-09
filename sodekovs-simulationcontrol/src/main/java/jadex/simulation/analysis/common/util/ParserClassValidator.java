package jadex.simulation.analysis.common.util;

import jadex.commons.IValidator;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;


/**
 *  A validator that tries to parse the text of a textfield in context of a spezified Class
 *  Uses extra analyse import
 */
public class ParserClassValidator implements IValidator
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
	
	/** class to check for*/
	protected Class clazz;
	
	//-------- constructors --------

	/**
	 *  Create a parser class validator.
	 */
	public ParserClassValidator(ClassLoader classloader, Class clazz)
	{
		this.clazz = clazz;
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
						if (clazz.equals(String.class))
						{
							lastvalid	= true;
						} else
						{
							String[] imports = {"jadex.simulation.analysis.common.dataObject.*", "jadex.simulation.analysis.common.dataObject.parameter.*"};
							IParsedExpression pexp = parser.parseExpression(text, imports, null, classloader);
							Object parsedObj = pexp.getValue(null);
							if (parsedObj.getClass().equals(clazz))
							{
								lastvalid	= true;
							} else
							{
								lastvalid	= false;
							}
						}
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
