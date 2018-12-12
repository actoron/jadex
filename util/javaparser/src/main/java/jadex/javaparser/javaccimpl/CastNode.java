package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;


/**
 *  Node representing a cast expression.
 *  A cast node has two children: a type node
 *  representing the static type of the cast,
 *  and an expression, representing the value to cast.
 */
// Todo: Allow casts between basic number types.
public class CastNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public CastNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- evaluation --------

	/**
	 *  Precompute the static type and check if cast is possible.
	 */
	public void precompile()
	{
		ExpressionNode	type	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	expression	= (ExpressionNode)jjtGetChild(1);

		// Precompute type of cast.
		if(type.isConstant())
		{
			try
			{
				setStaticType((Class)type.getValue(null));
			}
			catch(Exception e)
			{
			}
		}

		// This node is constant, when the subnodes are constant.
		if(type.isConstant() && expression.isConstant())
		{
			try
			{
				setConstantValue(getValue(null));
				setConstant(true);
			}
			catch(Exception e)
			{
			}
		}
	}

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher) //throws Exception
	{
		// For not constant nodes we have to typecheck each evaluation.
		if(!isConstant())
		{
			Object	value	= ((ExpressionNode)jjtGetChild(1)).getValue(fetcher);

			// Only non-null values have to be checked.
			if(value!=null)
			{
				// Get type, if not static.
				Class	type;
				if(getStaticType()!=null)
				{
					type	= getStaticType();
				}
				else
				{
					type	= (Class)((ExpressionNode)jjtGetChild(0)).getValue(null);
				}

				// Check if cast is possible.
				if(!SReflect.isSupertype(type, value.getClass())
					&& SReflect.isSupertype(Number.class, type)
					&& value instanceof Number)
				{
					// Perform number conversion with precision loss
					// should print warning???
					type	= SReflect.getWrappedType(type);
					if(type==Float.class)
					{
						value	= Float.valueOf(((Number)value).floatValue());
					}
					else if(type==Long.class)
					{
						value	= Long.valueOf(((Number)value).longValue());
					}
					else if(type==Integer.class)
					{
						value	= Integer.valueOf(((Number)value).intValue());
					}
					else if(type==Short.class)
					{
						value	= Short.valueOf(((Number)value).shortValue());
					}
					else if(type==Byte.class)
					{
						value	= Byte.valueOf(((Number)value).byteValue());
					}
					else if(type==Character.class)
					{
						value	= Character.valueOf((char)((Number)value).intValue());
					}
				}
				else if(!SReflect.isSupertype(type, value.getClass()))
				{
					throw new ClassCastException(value.toString()
						+" cannot be cast to "+type.getName());
				}
			}
			return value;
		}

		// For constant nodes typecheck has been performed by first evaluation.
		else
		{
			return getConstantValue();
		}
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		// Have to add braces ???
		return "(("+jjtGetChild(0).toPlainString()+")" + jjtGetChild(1).toPlainString()+")";
	}
}

