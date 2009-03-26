package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.IConstraint;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
	 *  @param generated	Conditions that have been generated during parsing (required for inversion).
	 *  @param invert True, if the generated condition part should be inverted.
	 *  @return The generated condition.
	 */
	public static ICondition	buildCondition(Constraint[] constraints, ICondition condition, OAVTypeModel tmodel, Set generated, boolean invert)
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
		
		Map	variables	= new HashMap();	// name -> variable
		Map	boundconstraints	= new HashMap();	// variable -> boundconstraint (only variable definitions, i.e. EQUAL constraints)
		Map	bcons	= new HashMap();	// variable -> object conditions (object condition with defining bound constraint).
		buildConditionMap(lcons, variables, boundconstraints, bcons);
		
		if(generated==null)
			generated	= new HashSet();
		
		for(int i=0; i<constraints.length; i++)
		{
			// Get object condition and value source for left part.
			Object[]	left	= getObjectConditionAndValueSource(constraints[i].getLeftValue(), lcons, generated, variables, boundconstraints, bcons, tmodel, invert);
			ObjectCondition	ocon	= (ObjectCondition)left[0];
			Object	valuesource	= left[1];

			// Get literal or variable for right part.
			Object	right	= flattenToPrimary(constraints[i].getRightValue(), lcons, generated, variables, boundconstraints, bcons, tmodel, invert);
			
			if(right instanceof Variable)
			{
				addConstraint(ocon, new BoundConstraint(valuesource, (Variable)right, getOperator(constraints[i].getOperator())), lcons, generated, bcons, invert);
			}
			else // Right is constant value
			{
				addConstraint(ocon, new LiteralConstraint(valuesource, right, getOperator(constraints[i].getOperator())), lcons, generated, bcons, invert);
			}
		}
		
		shuffle(lcons);

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
	protected static Object[] getObjectConditionAndValueSource(UnaryExpression value, List lcons, Set generated, Map variables, Map boundconstraints, Map bcons, OAVTypeModel tmodel, boolean invert)
	{
		Object[]	ret;
		Object	prim	= value.getPrimary();
		if(prim instanceof Variable)
		{
			Variable	var	= (Variable)prim;
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
				BoundConstraint	bc	= (BoundConstraint)boundconstraints.get(var);
				ret	= new Object[]{ocon, bc.getValueSource()};
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
								params[j]	= flattenToPrimary(ma.getParameterValues()[j], lcons, generated, variables, boundconstraints, bcons, tmodel, invert);
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

				BoundConstraint	bc	= (BoundConstraint)boundconstraints.get(var);
				if(bc!=null && bc.getValueSource()!=null)
				{
					suffs	= combineValueSources(bc.getValueSource(), suffs);
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
	protected static Object flattenToPrimary(UnaryExpression value, List lcons, Set generated, Map variables, Map boundconstraints, Map bcons, OAVTypeModel tmodel, boolean invert)
	{
		Object	ret;
		Object	prim	= value.getPrimary();
		if(value.getSuffixes()==null)
		{
			if(prim instanceof Variable)
			{
				ret	= prim;
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
			Object[]	ocvs	= getObjectConditionAndValueSource(value, lcons, generated, variables, boundconstraints, bcons, tmodel, invert);
			OAVObjectType	type	= getReturnType(ocvs[1], tmodel);

			String	varname;
			for(int i=1; variables.containsKey(varname	= "$tmpvar_"+i); i++);
			Variable	tmpvar	= new Variable(varname, type);
			variables.put(varname, tmpvar);
			BoundConstraint	bc	= new BoundConstraint(ocvs[1], tmpvar);
			boundconstraints.put(tmpvar, bc);
			addConstraint(((ObjectCondition)ocvs[0]), bc, lcons, generated, bcons, invert);
			ret	= tmpvar;
		}
		
		return ret;
	}
	
	/**
	 *  Shuffle conditions and constraints, such that all variables are bound
	 *  before used.
	 *  @param lcons	The list of conditions (shuffled in place).
	 */
	protected static void	shuffle(List lcons)
	{
		Set	boundvars	= new HashSet();	// Variables, which are bound and therefore can be used.
		boolean	progress	= true;
		int	finished	= 0;
		
		while(progress)
		{
			progress	= false;
			for(int i=finished; i<lcons.size(); i++)
			{
				ICondition	con	= (ICondition) lcons.get(i);
	
				// Find variables, which are bound (i.e. operator EQUAL) in this condition.
				Set	localbound	= new HashSet();
				List	bcs	= null;
				if(lcons.get(i) instanceof ObjectCondition)
				{
					bcs	= ((ObjectCondition)lcons.get(i)).getBoundConstraints();
				}
				else if(lcons.get(i) instanceof CollectCondition)
				{
					bcs	= ((CollectCondition)lcons.get(i)).getBoundConstraints();
				}
				for(int j=0; bcs!=null && j<bcs.size(); j++)
				{
					BoundConstraint	bc	= (BoundConstraint)bcs.get(j);
					if(bc.getOperator().equals(IOperator.EQUAL))
					{
						List	bvars	= bc.getBindVariables();
						for(int k=0; k<bvars.size(); k++)
						{
							localbound.add(bvars.get(k));
						}
					}
				}
				
				// Check if all variables are bound.
				List	vars	= con.getVariables();
				boolean	check	= true;
				for(int j=0; check && j<vars.size(); j++)
				{
					// Variable must be bound before or in this condition.
					check	= boundvars.contains(vars.get(j)) || localbound.contains(vars.get(j));
				}
	
				if(check)
				{
					// Todo: Shuffle constraints if necessary.
					
					// Variables of condition may now be used.
					boundvars.addAll(localbound);
					progress	= true;
					finished++;
				}
				else
				{
					// Shuffle condition to the end
					lcons.remove(i);
					lcons.add(con);
					i--;
				}
			}
		}

		if(finished<lcons.size())
		{
			throw new RuntimeException("Remaining unbound variables in conditions (cycle?): "+lcons+", "+finished);
		}
	}

	/**
	 *  Build maps of conditions by variable and variables by name.
	 */
	public static void	buildConditionMap(List lcons, Map variables, Map boundconstraints, Map bcons)
	{
		// Find conditions, bound to specific variables.
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
					variables.put(((Variable)bvars.get(k)).getName(), bvars.get(k));
					// Todo: by multiple ocurrences use the simplest bc.getValueSource() 
					if(bc.getOperator().equals(IOperator.EQUAL))
					{
						boundconstraints.put(bvars.get(k), bc);
						bcons.put(bvars.get(k), lcons.get(i));
					}
				}
			}
		}
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

	/**
	 *  Combine (i.e. chain) value sources.
	 *  @param valuesource	The initial value source.
	 *  @param suffs	The suffixes to the initial value source.
	 *  @return The combined value source.
	 */
	protected static List	combineValueSources(Object valuesource, List suffs)
	{
		List	ret	= new ArrayList();
		
		// For chained access only the last one is relevant.
		if(valuesource instanceof Object[])
		{
			ret.addAll(Arrays.asList((Object[])valuesource));
		}
		else if(valuesource instanceof List)
		{
			ret.addAll((List)valuesource);
		}
		else if(valuesource!=null)
		{
			ret.add(valuesource);
		}
		
		ret.addAll(suffs);
		
		return ret;
	}

	/**
	 *  Add a constraint to an object condition.
	 *  Creates a copy of the condition, if required for inverting.
	 *  @param ocon	The object condition.
	 *  @param constraint The constraint to add.
	 *  @param generated	The set of generated conditions
	 *  @param bcons	The conditions map.
	 */
	protected static void addConstraint(ObjectCondition ocon, IConstraint constraint, List lcons, Set generated, Map bcons, boolean invert)
	{
		if(invert && !generated.contains(ocon))
		{
			// Copy condition.
			ObjectCondition	tmp	= new ObjectCondition(ocon.getObjectType());
			for(int i=0; i<ocon.getConstraints().size(); i++)
				tmp.addConstraint((IConstraint)ocon.getConstraints().get(i));
			lcons.add(tmp);
			generated.add(tmp);
			
			// Replace in mapping.
			for(Iterator it=bcons.keySet().iterator(); it.hasNext(); )
			{
				Object	var	= it.next();
				if(bcons.get(var)==ocon)
					bcons.put(var, tmp);
			}
			
			ocon	= tmp;
		}
		
		ocon.addConstraint(constraint);
	}
}
