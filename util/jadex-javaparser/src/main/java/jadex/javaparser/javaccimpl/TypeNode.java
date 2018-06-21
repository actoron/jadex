package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 *  Node representing a type.
 *  The value will be the class object of the type.
 */
public class TypeNode	extends ExpressionNode
{
	protected ClassLoader classloader;
	
	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public TypeNode(ParserImpl p, int id)
	{
		super(p, id);
		setStaticType(Class.class);
		classloader = p.getClassLoader();
	}

	//-------- evaluation --------

	/**
	 *  Append to the token text.
	 *  @param text	The text to append.
	 */
	public void	appendText(String text)
	{
		super.appendText(text);
		if(text.equals("[]"))
		{
			// Hack ??? Update constant value to array type.
			precompile();
		}
	}

	/**
	 *  Precompute the type.
	 */
	public void precompile()
	{
		String	name	= getText();

		// Get class object.
		Class	clazz	= SReflect.findClass0(getText(), imports, classloader);

		if(clazz==null)
		{
			// Shouldn't happen...?
			throw new ParseException("Class not found: "+name);
		}

		setConstantValue(clazz);
		setConstant(true);
	}

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher)
	{
		if(getConstantValue()==null)
			precompile();
		return getConstantValue();
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		return getText();			
	}


	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && SUtil.equals(getValue(null), ((TypeNode)o).getValue(null));
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		return super.hashCode()*31 + (getValue(null)!=null ? getValue(null).hashCode() : 1);
	}
}

