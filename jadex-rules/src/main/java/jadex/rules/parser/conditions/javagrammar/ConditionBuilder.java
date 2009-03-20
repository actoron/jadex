package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  The condition builder takes a set of constraints
 *  (e.g. from the parser) and generates appropriate
 *  object conditions for it.
 */
public class ConditionBuilder
{
	/**
	 *  Build or adapt conditions for representing the given constraints.
	 *  @param constraints	The constraints to represent.
	 *  @param condition	Predefined condition that can be used if necessary.
	 *  @param tmodel	The type model.
	 *  @return The generated condition.
	 */
	public static ICondition	buildCondition(Constraint[] constraints, ICondition condition, OAVTypeModel tmodel)
	{
		// Unfold AND conditions.
		List	lcons	= new ArrayList();
		lcons.add(condition);
		for(int i=0; i<lcons.size(); i++)
		{
			if(lcons.get(i) instanceof AndCondition)
			{
				lcons.addAll(i+1, ((AndCondition)lcons.get(i)).getConditions());
				lcons.remove(i);
				i--;	// Decrement to check new condition at i instead of continuing with i+1.
			}
		}
		
		Map	bcons	= buildConditionMap(lcons);
		
		for(int i=0; i<constraints.length; i++)
		{
			// Get object condition and value source for left part.
			Object[]	left	= getObjectConditionAndValueSource(constraints[i].getLeftValue(), lcons, bcons, tmodel);
			ObjectCondition	ocon	= (ObjectCondition)left[0];
			Object	valuesource	= left[1];

			// Get literal or variable for right part.
			Object	right	= flattenToPrimary(constraints[i].getRightValue(), lcons, bcons, tmodel);
			
			if(right instanceof Variable)
			{
				ocon.addConstraint(new BoundConstraint(valuesource, (Variable)right, getOperator(constraints[i].getOperator())));
			}
			else // Right is constant value
			{
				ocon.addConstraint(new LiteralConstraint(valuesource, right, getOperator(constraints[i].getOperator())));
			}
		}

		return lcons.size()>1 ? new AndCondition(lcons) : (ICondition)lcons.get(0);
	}
	
	/**
	 *  Find or create an object condition for a value and
	 *  also return the appropriate value source.
	 *  @param value	The value to be obtained.
	 *  @param lcons	The existing conditions.
	 *  @param bcons	The conditions for existing variables.
	 *  @param tmodel	The type model.
	 *  @return	A tuple containing the object conditions and the remaining value source.
	 */
	protected static Object[] getObjectConditionAndValueSource(UnaryExpression value, List lcons, Map bcons, OAVTypeModel tmodel)
	{
		Object[]	ret;
		Primary	prim	= value.getPrimary();
		if(prim instanceof jadex.rules.parser.conditions.javagrammar.Variable)
		{
			Variable	var	= (Variable) bcons.get(((jadex.rules.parser.conditions.javagrammar.Variable)prim).getName());
			if(var==null)
			{
				throw new RuntimeException("Unbound variable: "+prim);
			}
			
			ObjectCondition	ocon	= (ObjectCondition) bcons.get(var);
			if(ocon==null)
			{
				throw new RuntimeException("No object condition for: "+var);
			}
			
			if(value.getSuffixes()==null)
			{
				ret	= new Object[]{ocon, null};
			}
			else
			{
				List	suffs	= new ArrayList();
				OAVObjectType	type	= var.getType();
				for(int i=0; i<value.getSuffixes().length; i++)
				{
					if(value.getSuffixes()[i] instanceof FieldAccess)
					{
						OAVAttributeType	attr	= type.getAttributeType(
							((FieldAccess)value.getSuffixes()[i]).getName());
						suffs.add(attr);
						type	= attr.getType();
					}
					else if(value.getSuffixes()[i] instanceof MethodAccess)
					{
						if(type instanceof OAVJavaType)
						{
							MethodAccess	ma	= (MethodAccess) value.getSuffixes()[i];
							Object[]	params	= new Object[ma.getParameterValues()!=null ? ma.getParameterValues().length : 0];
							Class[]	paramtypes	= new Class[params.length];
							for(int j=0; j<params.length; j++)
							{
								params[j]	= flattenToPrimary(ma.getParameterValues()[j], lcons, bcons, tmodel);
								if(params[j] instanceof Variable)
								{
									if(((Variable) params[j]).getType() instanceof OAVJavaType)
									{
										paramtypes[j]	= ((OAVJavaType)((Variable)params[j]).getType()).getClazz(); 
									}
									else
									{
										throw new RuntimeException("Cannot build method call: Only Java types supported for parameters: "+ma.getParameterValues()[j]);
									}
								}
								else if(params[j]!=null) // literal value
								{
									paramtypes[j]	= params[j].getClass();
								}
							}
							
							Class	clazz	= ((OAVJavaType)type).getClazz();
							Method[]	methods	= SReflect.getMethods(clazz, ma.getName());
							Class[][]	mparamtypes	= new Class[methods.length][];
							for(int j=0; j<methods.length; j++)
							{
								mparamtypes[j]	= methods[j].getParameterTypes(); 
							}
							int[]	matches	= SReflect.matchArgumentTypes(paramtypes, mparamtypes);
							if(matches.length==0)
							{
								throw new RuntimeException("No matching method found for: "+clazz.getName()+ma);
							}
							else if(matches.length>1)
							{
								System.out.println("Warning: Multiple matching methods found for: "+clazz.getName()+", "+ma);
							}
							suffs.add(new MethodCall((OAVJavaType)type, methods[matches[0]], params));
							type	= tmodel.getJavaType(methods[matches[0]].getReturnType());
						}
						else
						{
							throw new RuntimeException("Method invocation not supported on type: "+type);
						}
					}
					else
					{
						throw new RuntimeException("Unknown suffix element: "+value.getSuffixes()[i]);
					}
				}
				ret	= suffs.size()==1 ? new Object[]{ocon, suffs.get(0)} : new Object[]{ocon, suffs};
			}
		}
		else
		{
			throw new RuntimeException("Left hand side of constraint must start with variable: "+value);
		}
		
		return ret;
	}

	/**
	 *  Flatten a value to a primary value (literal or variable).
	 *  For a complex expression, additional conditions might be created
	 *  that bind the desired value in a new variable. 
	 *  @param value	The value to be obtained.
	 *  @param lcons	The existing conditions.
	 *  @param bcons	The conditions for existing variables.
	 *  @param tmodel	The type model.
	 *  @return	The primary value (i.e. variable or literal).
	 */
	protected static Object flattenToPrimary(UnaryExpression value, List lcons, Map bcons, OAVTypeModel tmodel)
	{
		Object	ret;
		Primary	prim	= value.getPrimary();
		if(value.getSuffixes()==null)
		{
			if(prim instanceof jadex.rules.parser.conditions.javagrammar.Variable)
			{
				ret	= bcons.get(((jadex.rules.parser.conditions.javagrammar.Variable)prim).getName());
				if(ret==null)
				{
					throw new RuntimeException("Unbound variable: "+prim);
				}
			}
			else if(prim instanceof Literal)
			{
				ret	= ((Literal)prim).getValue();
			}
			else
			{
				throw new RuntimeException("Unknown primary element: "+prim); 
			}
		}
		else
		{
			Object[]	ocvs	= getObjectConditionAndValueSource(value, lcons, bcons, tmodel);
			OAVObjectType	type	= getReturnType(ocvs[1], tmodel);

			String	varname;
			for(int i=1; bcons.containsKey(varname	= "$tmpvar_"+i); i++);
			Variable	tmpvar	= new Variable(varname, type);
			bcons.put(varname, tmpvar);
			((ObjectCondition)ocvs[0]).addConstraint(new BoundConstraint(ocvs[1], tmpvar));
			ret	= tmpvar;
		}
		
		return ret;
	}

	/**
	 *  Build a map of conditions by variable.
	 */
	protected static Map	buildConditionMap(List lcons)
	{
		// Find conditions, bound to specific variables.
		Map	bcons	= new HashMap();
		for(int i=0; i<lcons.size(); i++)
		{
			List	bcs	= null;
			if(lcons.get(i) instanceof ObjectCondition)
			{
				bcs	= ((ObjectCondition)lcons.get(i)).getBoundConstraints();
			}
			
			// Todo: support collect conditions like object conditions !?
//			else if(lcons.get(i) instanceof CollectCondition)
//			{
//				bcs	= ((CollectCondition)lcons.get(i)).getBoundConstraints();
//			}
			
			for(int j=0; bcs!=null && j<bcs.size(); j++)
			{
				BoundConstraint	bc	= (BoundConstraint)bcs.get(j);
				List	bvars	= bc.getBindVariables();
				for(int k=0; k<bvars.size(); k++)
				{
					bcons.put(((Variable)bvars.get(k)).getName(), bvars.get(k));
					if(bc.getValueSource()==null && bc.getOperator().equals(IOperator.EQUAL))
					{
						bcons.put(bvars.get(k), lcons.get(i));
					}
				}
			}
		}
		
		return bcons;
	}

	/**
	 *  Get the operator object for a string.
	 */
	protected static IOperator	getOperator(String operator)
	{
		IOperator	ret;
		if("==".equals(operator))
			ret	= IOperator.EQUAL;
		else if("!=".equals(operator))
			ret	= IOperator.NOTEQUAL;
		else if(">".equals(operator))
			ret	= IOperator.GREATER;
		else if(">=".equals(operator))
			ret	= IOperator.GREATEROREQUAL;
		else if("<".equals(operator))
			ret	= IOperator.LESS;
		else if("<=".equals(operator))
			ret	= IOperator.LESSOREQUAL;
		else
			throw new RuntimeException("Unknown operator '"+operator+"'.");
		
		return ret;
	}
	
	/**
	 *  Get the return type of a value source.
	 *  @param valuesource	The value source.
	 *  @param tmodel	The type model.
	 *  @return The object type.
	 */
	protected static OAVObjectType	getReturnType(Object valuesource, OAVTypeModel tmodel)
	{
		OAVObjectType	ret;
		
		// For chained access only the last one is relevant.
		if(valuesource instanceof Object[])
		{
			valuesource	= ((Object[])valuesource)[((Object[])valuesource).length-1];
		}
		else if(valuesource instanceof List)
		{
			valuesource	= ((List)valuesource).get(((List)valuesource).size()-1);
		}

		if(valuesource instanceof OAVAttributeType)
		{
			ret	= ((OAVAttributeType)valuesource).getType();
		}
		else if(valuesource instanceof MethodCall)
		{
			ret	= tmodel.getJavaType(((MethodCall)valuesource).getMethod().getReturnType());
		}
		else
		{
			throw new RuntimeException("Unknown value source type: "+valuesource);
		}
		
		return ret;
	}
}
