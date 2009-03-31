package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *  The constraint builder takes an expression
 *  (e.g. from the parser) and generates appropriate
 *  constraints and/or conditions for it.
 */
public class ConstraintBuilder
{
	/**
	 *  Build or adapt conditions for representing the given constraints.
	 *  @param expression	The expression, which contains the constraints to represent.
	 *  @param condition	Predefined condition that should be extended to reflect the constraints.
	 *  @param tmodel	The type model.
	 *  @return The generated condition.
	 */
	public static ICondition	buildConstraints(Expression expression, ICondition condition, OAVTypeModel tmodel)
	{
		BuildContext	context	= new BuildContext(condition, tmodel);

		// Unfold AND expressions
		List	constraints	= new ArrayList();
		constraints.add(expression);
		for(int i=0; i<constraints.size(); i++)
		{
			Expression	exp	= (Expression)constraints.get(i);
			if(exp instanceof OperationExpression
				&& ((OperationExpression)exp).getOperator().equals(OperationExpression.OPERATOR_AND))
			{
				constraints.add(i+1, ((OperationExpression)exp).getLeftValue());
				constraints.add(i+2, ((OperationExpression)exp).getRightValue());
				constraints.remove(i);
				i--;	// Decrement to check new condition at i instead of continuing with i+1.
			}
		}
		
		for(int i=0; i<constraints.size(); i++)
		{
			buildConstraint((Expression)constraints.get(i), context);
		}

		List	lcons	= context.getConditions();
		shuffle(lcons);

		return lcons.size()>1 ? new AndCondition(lcons) : (ICondition)lcons.get(0);
	}
		
	/**
	 *  Build a constraint for a single expression.
	 */
	protected static void	buildConstraint(Expression exp, BuildContext context)
	{
		if(exp instanceof OperationExpression)
		{
			OperationExpression	opex	= (OperationExpression)exp;
			if(opex.getOperator() instanceof IOperator)
			{
				buildOperatorConstraint(opex.getLeftValue(), opex.getRightValue(), (IOperator)opex.getOperator(), context);
			}
//			else if(opex.getOperator() instanceof IFunction)
//			{
//				buildFunctionConstraint(opex.getLeftValue(), opex.getRightValue(), (IFunction)opex.getOperator(), context);
//			}
			else
			{
				throw new RuntimeException("Unexpected operator type: "+opex);
			}
		}
		else if(exp instanceof UnaryExpression)
		{
			UnaryExpression	unex	= (UnaryExpression)exp;
			if(unex.getOperator().equals(UnaryExpression.OPERATOR_NOT))
			{
				boolean	built	= false;

				// Try to build not constraint by inverting the operator.
				if(unex.getValue() instanceof OperationExpression)
				{
					OperationExpression	opex	= (OperationExpression)unex.getValue();
					if(opex.getOperator() instanceof IOperator)
					{
						IOperator	inverse	= OperationExpression.getInverseOperator0((IOperator)opex.getOperator());
						if(inverse!=null)
						{
							buildConstraint(new OperationExpression(
								opex.getLeftValue(), opex.getRightValue(), inverse) , context);
							built	= true;
						}
					}
				}

				if(!built)
				{
					buildOperatorConstraint(unex.getValue(), new LiteralExpression(Boolean.TRUE), IOperator.NOTEQUAL, context);
				}
			}
			else
			{
				throw new RuntimeException("Unexpected operator type: "+unex);
			}
		}
		// Conditional
		// Literal
		// Primary
		// Variable
		else
		{
			buildOperatorConstraint(exp, new LiteralExpression(Boolean.TRUE), IOperator.EQUAL, context);
		}
	}

	/**
	 *  Build an operator constraint.
	 */
	protected static void	buildOperatorConstraint(Expression left, Expression right, IOperator op, BuildContext context)
	{
		// Get object condition and value source for left part.
		Object[]	tmp	= getObjectConditionAndValueSource(left, context);
		ObjectCondition	ocon	= (ObjectCondition)tmp[0];
		Object	valuesource	= tmp[1];

		right	= flattenToPrimary(right, context);

		// Build literal constraint for constant values
		if(right instanceof LiteralExpression)
		{
			ocon.addConstraint(new LiteralConstraint(valuesource, ((LiteralExpression)right).getValue(), op));
		}

		// Build variable constraint for other expressions
		else //if(right instanceof VariableExpression)
		{
			ocon.addConstraint(new BoundConstraint(valuesource, ((VariableExpression)right).getVariable(), op));
		}
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
	protected static Object[] getObjectConditionAndValueSource(Expression value, BuildContext context)
	{
		Object[]	ret;
		
		if(value instanceof VariableExpression)
		{
			ret	= new Object[]{
				context.getObjectCondition(((VariableExpression)value).getVariable()),
				context.getBoundConstraint(((VariableExpression)value).getVariable()).getValueSource()};
		}
		else if(value instanceof PrimaryExpression)
		{
			Expression	prim	= ((PrimaryExpression)value).getPrefix();
			if(prim instanceof VariableExpression)
			{
				Variable	var	= ((VariableExpression)prim).getVariable();
				
				ObjectCondition	ocon	= (ObjectCondition)context.getObjectCondition(var);
				
				List	suffs	= new ArrayList();
				OAVObjectType	type	= var.getType();
				Suffix[]	suffixes	= ((PrimaryExpression)value).getSuffixes();
				for(int i=0; i<suffixes.length; i++)
				{
					if(suffixes[i] instanceof FieldAccess)
					{
						OAVAttributeType	attr	= type.getAttributeType(
							((FieldAccess)suffixes[i]).getName());
						suffs.add(attr);
						type	= attr.getType();
					}
					else if(suffixes[i] instanceof MethodAccess)
					{
						if(type instanceof OAVJavaType)
						{
							MethodAccess	ma	= (MethodAccess)suffixes[i];
							Object[]	params	= new Object[ma.getParameterValues()!=null ? ma.getParameterValues().length : 0];
							Class[]	paramtypes	= new Class[params.length];
							for(int j=0; j<params.length; j++)
							{
								Expression	p	= flattenToPrimary(ma.getParameterValues()[j], context);
								if(p instanceof VariableExpression)
								{
									params[j]	= ((VariableExpression)p).getVariable();
									if(((Variable) params[j]).getType() instanceof OAVJavaType)
									{
										paramtypes[j]	= ((OAVJavaType)((Variable)params[j]).getType()).getClazz(); 
									}
									else
									{
										throw new RuntimeException("Cannot build method call: Only Java types supported for parameters: "+ma.getParameterValues()[j]);
									}
								}
								else //if(p instanceof LiteralExpression)
								{
									params[j]	= ((LiteralExpression)p).getValue();
									paramtypes[j]	= params[j]!=null ? params[j].getClass() : null;
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
							type	= context.getTypeModel().getJavaType(methods[matches[0]].getReturnType());
						}
						else
						{
							throw new RuntimeException("Method invocation not supported on type: "+type);
						}
					}
					else
					{
						throw new RuntimeException("Unknown suffix element: "+suffixes[i]);
					}
				}

				BoundConstraint	bc	= context.getBoundConstraint(var);
				if(bc.getValueSource()!=null)
				{
					suffs	= combineValueSources(bc.getValueSource(), suffs);
				}
				ret	= suffs.size()==1 ? new Object[]{ocon, suffs.get(0)} : new Object[]{ocon, suffs};
			}
			else
			{
				throw new RuntimeException("Primary expression must start with variable: "+value);
			}
		}
		else
		{
			throw new RuntimeException("Unsupported left hand side of constraint: "+value);
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
	protected static Expression	flattenToPrimary(Expression value, BuildContext context)
	{
		Expression	ret;

		if(value instanceof VariableExpression)
		{
			ret	= value;
		}
		else if(value instanceof LiteralExpression)
		{
			ret	= value;
		}
		else if(value instanceof PrimaryExpression)
		{
			Object[]	ocvs	= getObjectConditionAndValueSource(value, context);
			ret	= new VariableExpression(context.generateVariableBinding((ObjectCondition)ocvs[0], ocvs[1]));
		}
		else
		{
			throw new UnsupportedOperationException("Todo: flatten expressions of type: "+value);
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
}
