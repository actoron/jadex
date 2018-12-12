package jadex.rules.rulesystem.rete.extractors;

import java.lang.reflect.InvocationTargetException;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;

/**
 *  Call a method on an object.
 */
public class JavaMethodExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The object extractor. */
	protected IValueExtractor objex;
	
	/** The method call descriptor. */
	protected MethodCall methodcall;

	/** The parameter extractors. */
	protected IValueExtractor[]	parameters;
	
	/** The relevant attributes. */
//	protected Set	relevants;
	
	//-------- constructors --------
	
	
	/**
	 *  Create a new method extractor
	 */
	public JavaMethodExtractor(IValueExtractor objex, MethodCall methodcall, IValueExtractor[] parameters)
	{
		this.objex	= objex;
		this.methodcall	= methodcall;
		this.parameters	= parameters;
	}

	//-------- IValueExtractor interface --------
	
	/**
	 *  Get the value of an attribute from an object or tuple.
	 *  @param left The left input tuple. 
	 *  @param right The right input object.
	 *  @param prefix The prefix input object (last value from previous extractor in a chain).
	 *  @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, Object prefix, IOAVState state)
	{
		// Extract parameters
		Object[]	paramvalues	= new Object[parameters.length];
		for(int i=0; i<parameters.length; i++)
			paramvalues[i]	= parameters[i].getValue(left, right, prefix, state);
		
		// Call method on object.
		Object	object	= objex.getValue(left, right, prefix, state);
		try
		{
			return methodcall.getMethod().invoke(object, paramvalues);
		}
		catch(Throwable e)
		{
			if(e instanceof InvocationTargetException)
			{
				e	= ((InvocationTargetException)e).getTargetException();
			}
			if(e instanceof Error)
			{
				throw (Error)e;
			}
			else if(e instanceof RuntimeException)
			{
				throw (RuntimeException)e;
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index (-1 for object).
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr)
	{
		boolean	ret	= tupleindex==-1 && getRelevantAttributes().contains(attr);
		for(int i=0; !ret && i<parameters.length; i++)
			ret	= parameters[i].isAffected(tupleindex, attr);
		return ret;
	}

	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
//		if(relevants==null)
//		{
//			relevants	= new HashSet();
//			OAVObjectType	type	= methodcall.getType();
//			while(type!=null)
//			{
//				relevants.addAll(type.getDeclaredAttributeTypes());
//				type	= type.getSupertype();
//			}
//			
//			for(int i=0; i<parameters.length; i++)
//				relevants.addAll(parameters[i].getRelevantAttributes());
//		}
//		return relevants;
		
		AttributeSet relevants	= new AttributeSet();
		OAVJavaType	type = methodcall.getType();
		relevants.addAllType(type);
		
		for(int i=0; i<parameters.length; i++)
			relevants.addAll(parameters[i].getRelevantAttributes());
		
		return relevants;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(objex);
		ret.append(".");
		ret.append(methodcall.getMethod().getName());
		ret.append("(");
		for(int i=0; parameters!=null && i<parameters.length; i++)
		{
			if(i>0)
				ret.append(", ");
			ret.append(parameters[i]);
		}
		ret.append(")");
		
		return ret.toString();
	}
}
