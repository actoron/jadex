package jadex.javaparser.javaccimpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;


/**
 *  Base class of expression node hierarchy.
 */
public abstract class ExpressionNode	extends SimpleNode	implements IParsedExpression
{
	//-------- attributes --------

	/** The token text (if any). */
	protected String	text;

	/** The expression text (if any). */
	protected String	expressiontext;

	/** The imports (if any). */
	protected String[]	imports;

	/** The type model. */
//	protected OAVTypeModel	tmodel;

	/** The static type (if any). */
	protected Class	static_type;

	/** Is the node value constant
	    (independent of evaluation context and parameters)? */
	protected boolean	constant;

	/** The constant value (if any). */
	protected Object	constant_value;

	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ExpressionNode(ParserImpl p, int id)
	{
		super(p, id);
		this.imports	= p.getImports();
//		this.tmodel	= p.getTypeModel();
		//this.static_type	= Object.class; // not yet
	}

	//-------- attribute accessors --------

	/**
	 *  Get the full expression text.
	 *  @param text	The expression text.
	 */
	public void	setExpressionText(String expressiontext)
	{
		this.expressiontext	= expressiontext;
	}
	/**
	 *  Get the full expression text.
	 */
	public String	getExpressionText()
	{
		return expressiontext;
	}

	/**
	 *  Set the token text.
	 *  @param text	The token text.
	 */
	public void	setText(String text)
	{
		this.text	= text;
	}

	/**
	 *  Append to the token text.
	 *  @param text	The text to append.
	 */
	public void	appendText(String text)
	{
		this.text	= this.text==null ? text : this.text+text;
	}

	/**
	 *  Set the static type.
	 *  @param static_type	The static type.
	 */
	public void	setStaticType(Class static_type)
	{
		this.static_type	=  static_type;
	}

	/**
	 *  Set the constant value.
	 *  @param constant_value	The constant value.
	 */
	public void	setConstantValue(Object constant_value)
	{
		this.constant_value	=  constant_value;
	}

	/**
	 *  Get the constant value.
	 *  The constant value of a node may be known,
	 *  when it is independent of the evaluation context,
	 *  and the child nodes are constant, too.
	 *  @return The constant value.
	 */
	public Object	getConstantValue()
	{
		return this.constant_value;
	}

	/**
	 *  Set if the node is constant.
	 *  @param constant	The constant.
	 */
	public void	setConstant(boolean constant)
	{
		this.constant	=  constant;
	}

	/**
	 *  Get if the node is constant.
	 *  The node is constant, when it is independent
	 *  of the evaluation context, and the child nodes
	 *  are constant, too.
	 *  @return The constant flag.
	 */
	public boolean	isConstant()
	{
		return this.constant;
	}

	/**
	 *  Create a string representation of this node for dumping in a tree.
	 *  @return A string representation of this node.
	 */
	public String toString(String prefix)
	{
		return prefix + ParserImplTreeConstants.jjtNodeName[id]+"("+text+")";
	}

	/**
	 *  Create a string representation of this node for dumping in a tree.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		return super.toString();
	}

	/**
	 *  Create a string representation of this node for dumping in a tree.
	 *  @return A string representation of this node.
	 */
	public String toString()
	{
		return "<" + toPlainString() + ">";
	}

	/**
	 *  Create a string for a subnode.
	 *  Automatically adds braces if necessary.
	 *  @param subnode	The index of the subnode.
	 *  @return The string for the subnode.
	 */
	protected String	subnodeToString(int subnode)
	{
		Node	node	= jjtGetChild(subnode);
		if(node.jjtGetNumChildren()==0)
			return node.toPlainString();
		else
			return "(" + node.toPlainString() + ")";
	}

	//-------- ITerm methods --------

	/**
	 *  Get the expression text.
	 *  @return The text.
	 */
	public String	getText()
	{
		return this.text;
	}

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public abstract Object	getValue(IValueFetcher fetcher); //throws Exception;

	/**
	 *  Get the static type.
	 *  If no information about the return type of an expression
	 *  is available (e.g. because it depends on the evaluation context),
	 *  the static type is null.
	 *  @return The static type.
	 */
	public Class<?> getStaticType()
	{
		return static_type;
	}
	
	
	/**
	 *  Get the parameters used in the expression.
	 */
	public Set<String>	getParameters()
	{
		Set<String>	ret	= new LinkedHashSet<String>();
		for(ParameterNode n: getUnboundParameterNodes())
		{
			ret.add(n.getText());
		}
		return ret;
	}

	//-------- expression methods --------

	/**
	 *  Set a static java type.
	 * /
	protected void setStaticType(Class clazz)
	{
		setStaticType(tmodel.getJavaType(clazz));
	}*/
	
	/**
	 *  Get unbound parameter nodes.
	 *  @return The unbound parameter nodes.
	 */
	public ParameterNode[]	getUnboundParameterNodes()
	{
		// Default: Return unbound parameters of subnodes.
		ParameterNode[]	ret	= new ParameterNode[0];
		for(int i=0; i<jjtGetNumChildren(); i++)
		{
			ret	= (ParameterNode[])SUtil.joinArrays(ret,
				((ExpressionNode)jjtGetChild(i)).getUnboundParameterNodes());
		}
		return ret;
	}

	/**
	 *  This method should be overridden to perform
	 *  all possible checks and precompute all values
	 *  (e.g. the static_type), which are independent
	 *  of the evaluation context and parameters.
	 */
	public void precompile()
	{
	}

	/**
	 *  Precompile this node and all subnodes.
	 */
	public void precompileTree()
	{
		// Precompile subtree first !
		for(int i=0; i<jjtGetNumChildren(); i++)
		{
			((ExpressionNode)jjtGetChild(i)).precompileTree();
		}

		// Now precompile this node.
		precompile();
	}

	/**
	 *  (Re)throw an exception that occured during parsing
	 *  and add a useful error message.
	 *  @param ex	The exception to be rethrown (if any).
	 */
	protected void	throwParseException(Throwable ex)	throws ParseException
	{
		Node	root	= this;
		while(root.jjtGetParent()!=null)
			root	= root.jjtGetParent();

		StringWriter	sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		throw new ParseException("Exception while parsing expression: "
				+root.toPlainString()+"\n"+sw);
	}

	/**
	 *  (Re)throw an exception that occured during evaluation
	 *  and add a useful error message.
	 *  @param ex	The exception to be rethrown (if any).
	 */
	protected void	throwEvaluationException(Throwable ex)	throws RuntimeException
	{
		Node	root	= this;
		while(root.jjtGetParent()!=null)
			root	= root.jjtGetParent();

		StringWriter	sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		throw new RuntimeException("Exception while evaluating expression: "
				+root.toPlainString()+"\n"+sw);
	}

	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		boolean	ret	= o!=null && o.getClass().equals(getClass())
			&& jjtGetNumChildren()==((ExpressionNode)o).jjtGetNumChildren();
		for(int i=0; ret && i<jjtGetNumChildren(); i++)
		{
			ret	= jjtGetChild(i).equals(((ExpressionNode)o).jjtGetChild(i));
		}
		return ret;
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		int ret	= 31 + getClass().hashCode();
		for(int i=0; i<jjtGetNumChildren(); i++)
		{
			ret	= ret*31 + jjtGetChild(i).hashCode();
		}
		return ret;
	}
}

