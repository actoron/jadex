package jadex.rules.parser.conditions.javagrammar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.ArraySelector;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.Constant;
import jadex.rules.rulesystem.rules.ConstrainableCondition;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.rulesystem.rules.functions.Identity;
import jadex.rules.rulesystem.rules.functions.MethodCallFunction;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;


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
	 *  @param context	The build context.
	 *  @return The generated condition.
	 */
	public static ICondition	buildConstraints(Expression expression, BuildContext context, IParserHelper helper)
	{
		// Decompose expression into 1) object conditions, 2) collect conditions, 3) remaining constraints, 4) NOT conditions.
		List	conditions	= new ArrayList();
		List	nots	= new ArrayList();
		List	constraints	= new ArrayList();
		constraints.add(expression);
		for(int i=0; i<constraints.size(); i++)
		{
			Expression	exp	= (Expression)constraints.get(i);
			if(exp instanceof ExistentialDeclaration)
			{
				constraints.remove(i);
				conditions.add(exp);
				i--;
			}
			
			// Test if an unary not is a NOT condition (i.e. contains an existential declaration) instead of just a constraint
			else if(exp instanceof UnaryExpression
				&& ((UnaryExpression)exp).getOperator().equals(UnaryExpression.OPERATOR_NOT))
			{
				boolean	notcon	= false;
				List	exps	= new ArrayList();
				exps.add(((UnaryExpression)exp).getValue());
				while(!notcon && !exps.isEmpty())
				{
					if(exps.get(0) instanceof ExistentialDeclaration)
					{
						notcon	= true;
					}
					// Unfold nested NOT
					else if(exps.get(0) instanceof UnaryExpression
						&& ((UnaryExpression)exps.get(0)).getOperator().equals(UnaryExpression.OPERATOR_NOT))
					{
						exps.add(((UnaryExpression)exps.get(0)).getValue());
					}
					// Unfold AND for complex NOTs
					else if(exps.get(0) instanceof OperationExpression
						&& ((OperationExpression)exps.get(0)).getOperator().equals(OperationExpression.OPERATOR_AND))
					{
						exps.add(((OperationExpression)exps.get(0)).getLeftValue());
						exps.add(((OperationExpression)exps.get(0)).getRightValue());
					}
					exps.remove(0);
				}
				
				if(notcon)
				{
					constraints.remove(i);
					nots.add(((UnaryExpression)exp).getValue());
					i--;
				}
			}
			
			// Unfold AND expression
			else if(exp instanceof OperationExpression
				&& ((OperationExpression)exp).getOperator().equals(OperationExpression.OPERATOR_AND))
			{
				constraints.add(i+1, ((OperationExpression)exp).getLeftValue());
				constraints.add(i+2, ((OperationExpression)exp).getRightValue());
				constraints.remove(i);
				i--;	// Decrement to check new condition at i instead of continuing with i+1.
			}
		}
		
		// Build object conditions.
		for(int i=0; i<conditions.size(); i++)
		{
			ExistentialDeclaration	edec	= (ExistentialDeclaration)conditions.get(i);
			
			// Test if type needs replacement (e.g. goal vs. IGoal)
			// Otherwise build normal object condition.
			Object[]	rep	= helper.getReplacementType(edec.getType());
			OAVObjectType	type	= rep!=null ? (OAVObjectType)rep[0] : edec.getType();
			Object	valuesource	= rep!=null ? rep[1] : null;
			
			context.createObjectCondition(type, new IConstraint[]
			{
				new BoundConstraint(valuesource, edec.getVariable())
            });
		}

		// Build remaining constraints.
		for(int i=0; i<constraints.size(); i++)
		{
			buildConstraint((Expression)constraints.get(i), context, false, helper);
			assert context.oconstack==null : "Stack problem: "+(Expression)constraints.get(i);
		}

		// Build NOT conditions.
		for(int i=0; i<nots.size(); i++)
		{
			BuildContext	newcon	= new BuildContext(context);
			ICondition	cond	= buildConstraints((Expression)nots.get(i), newcon, helper);
			context.addCondition(new NotCondition(cond));
		}

		shuffle(context);
		
		// Reassign dummy constraints to previous or next condition.
		List	lcons	= context.getConditions();
		if(context.hasDummyCondition())
		{
			List	cons	= context.getDummyCondition().getConstraints();
			for(int i=0; i<lcons.size(); i++)
			{
				if(lcons.get(i)==context.getDummyCondition())
				{
					ConstrainableCondition	target;
					if(i==0 && lcons.size()>1)
					{
						// Todo: when adding to next condition, dummy constraints should be first!?
						target	= (ConstrainableCondition)lcons.get(i+1);
					}
					else if(i>0)
					{
						target	= (ConstrainableCondition)lcons.get(i-1);
					}
					else
					{
						throw new UnsupportedOperationException("No object conditions produced (todo: test condition): "+expression);
					}
					
					lcons.remove(i);
					for(int j=0; j<cons.size(); j++)
					{
						target.addConstraint((IConstraint)cons.get(j));
					}
				}
			}
		}

		return lcons.size()>1 ? new AndCondition(lcons) : (ICondition)lcons.get(0);
	}
		
	/**
	 *  Build a constraint for a single expression.
	 */
	protected static void	buildConstraint(Expression exp, BuildContext context, boolean invert, IParserHelper helper)
	{
		if(exp instanceof OperationExpression)
		{
			OperationExpression	opex	= (OperationExpression)exp;
			if(opex.getOperator() instanceof IOperator)
			{
				IOperator	operator	= (IOperator)opex.getOperator();
				if(invert)
				{
					IOperator	inverse	= OperationExpression.getInverseOperator0(operator);
					if(inverse==null)
					{
						buildOperatorConstraint(exp, new LiteralExpression(Boolean.TRUE), IOperator.NOTEQUAL, context, helper);
					}
					else
					{
						buildOperatorConstraint(opex.getLeftValue(), opex.getRightValue(), inverse, context, helper);
					}
				}
				else
				{
					buildOperatorConstraint(opex.getLeftValue(), opex.getRightValue(), operator, context, helper);
				}
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
				buildConstraint(unex.getValue(), context, !invert, helper);
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
			buildOperatorConstraint(exp, new LiteralExpression(Boolean.TRUE), invert ? IOperator.NOTEQUAL : IOperator.EQUAL, context, helper);
		}
	}

	/**
	 *  Build an operator constraint.
	 */
	protected static void	buildOperatorConstraint(Expression left, Expression right, IOperator op, BuildContext context, IParserHelper helper)
	{
		// Special treatment for unbound variables -> build test condition.
		// Currently not supported by Rete builder
		if(left instanceof VariableExpression && context.getConstrainableCondition0(((VariableExpression)left).getVariable())==null)
		{
			// Flatten right side to literal or (temporary) variable.
			right	= flattenToPrimary(right, context, helper);
			Object	rightsource	= right instanceof LiteralExpression
				? new Constant(((LiteralExpression)right).getValue())
				: ((VariableExpression)right).getVariable();
			
			// Build test condition for left side (variable).
			TestCondition	testcon	= new TestCondition(new PredicateConstraint(new FunctionCall(
				new OperatorFunction(op), new Object[]{((VariableExpression)left).getVariable(), rightsource})));
			
			context.getConditions().add(testcon);
		}

		// Normal treatment: Find object condition + value source for left part and attach constraint based on right part.
		else
		{
			// Get object condition and value source for left part.
			Object	valuesource	= getObjectConditionAndValueSource(left, context, helper);
	
			// Flatten right side to literal value or (temporary) variable.
			right	= flattenToPrimary(right, context, helper);
	
			// Build literal constraint for constant values
			if(right instanceof LiteralExpression)
			{
				// Right side of literal constraint is not a value source (i.e. Constant)
				context.getCurrentCondition().addConstraint(new LiteralConstraint(valuesource, ((LiteralExpression)right).getValue(), op));
			}
	
			// Build variable constraint for other expressions
			else //if(right instanceof VariableExpression)
			{
				context.getCurrentCondition().addConstraint(new BoundConstraint(valuesource, ((VariableExpression)right).getVariable(), op));
			}
			
			context.popCondition();
		}
	}
	
	/**
	 *  Find or create an object condition for a value and
	 *  return the appropriate value source.
	 *  The condition is pushed on the stack of the build context.
	 *  When the condition is no longer required (e.g. all current constraints added) it has to be popped from the stack (manually).
	 *  @param value	The value to be obtained.
	 *  @param lcons	The existing conditions.
	 *  @param bcons	The conditions for existing variables.
	 *  @param tmodel	The type model.
	 *  @return	The value source.
	 */
	protected static Object getObjectConditionAndValueSource(Expression value, BuildContext context, IParserHelper helper)
	{
		Object	valuesource;
		int	stacksize	= context.oconstack!=null ? context.oconstack.size() : 0;
		
		if(value instanceof VariableExpression)
		{
			context.pushCondition(context.getConstrainableCondition(((VariableExpression)value).getVariable()));
			valuesource	= context.getBoundConstraint(((VariableExpression)value).getVariable()).getValueSource();
		}
		else if(value instanceof PrimaryExpression)
		{
			Expression	prim	= ((PrimaryExpression)value).getPrefix();
			OAVObjectType	type;
						
			if(prim instanceof VariableExpression)
			{
				Variable	var	= ((VariableExpression)prim).getVariable();
				type	= var.getType();
				valuesource	= context.getBoundConstraint(var).getValueSource();
				context.pushCondition(context.getConstrainableCondition(var));
			}
			else if(prim instanceof LiteralExpression)
			{
				Constant	c	= new Constant(((LiteralExpression)prim).getValue());
				valuesource	= new FunctionCall(new Identity(), new Object[]{c});
				if(c.getValue()!=null)
					type	= context.getTypeModel().getJavaType(c.getValue().getClass());
				else
					type	= OAVJavaType.java_object_type;

				context.pushCondition(context.getDummyCondition());
			}
			else if(prim instanceof StaticMethodAccess)
			{
				context.pushCondition(context.getDummyCondition());

				StaticMethodAccess	sma	= (StaticMethodAccess)prim;
				MethodCall mc = createMethodCall(sma.getType(), sma.getName(), sma.getParameterValues(), context, helper);

				List	paramsources	= mc.getParameterSources();
				paramsources.add(0, new Constant(null));	// first param is object to be invoked (use null for static method).
				valuesource	= new FunctionCall(new MethodCallFunction(mc.getMethod()), paramsources);
				type	= context.getTypeModel().getJavaType(mc.getMethod().getReturnType());
			}
			else if(prim instanceof CastExpression)
			{
				type	= ((CastExpression)prim).getType();
				valuesource	= getObjectConditionAndValueSource(((CastExpression)prim).getValue(), context, helper);
				// Skip pop/push of same condition.
			}
			else if(prim instanceof CollectExpression)
			{
				CollectExpression	colex	= (CollectExpression)prim;
				BuildContext	newcon	= new BuildContext(context);
				ICondition	cond	= buildConstraints(colex.getExpression(), newcon, helper);
				CollectCondition	colco; 
				if(cond instanceof ObjectCondition)
				{
					colco	= new CollectCondition((ObjectCondition)cond);
				}
				else if(cond instanceof AndCondition)
				{
					colco	= new CollectCondition(((AndCondition)cond).getConditions(), null);
				}
				else
				{
					throw new RuntimeException("Cannot collect: "+cond);
				}
				context.addCondition(colco);
				context.pushCondition(colco);
				type	= OAVJavaType.java_collection_type;
				valuesource	= null;
			}
			else
			{
				throw new UnsupportedOperationException("Unsupported start of primary expression: "+prim);
			}
			
			List	suffs	= new ArrayList();
			Suffix[]	suffixes	= ((PrimaryExpression)value).getSuffixes();
			for(int i=0; i<suffixes.length; i++)
			{
				if(suffixes[i] instanceof FieldAccess)
				{
					OAVAttributeType	attr	= type.getAttributeType(
						((FieldAccess)suffixes[i]).getName());
					suffs.add(attr);
					if(attr.getMultiplicity().equals(OAVAttributeType.NONE))
						type	= attr.getType();
					else
						type	= OAVJavaType.java_collection_type;
				}
				else if(suffixes[i] instanceof MethodAccess)
				{
					if(type instanceof OAVJavaType)
					{
						MethodAccess	ma	= (MethodAccess)suffixes[i];
						Expression[]	paramvalues	= ma.getParameterValues();
						String	name	= ma.getName();
						MethodCall mc = createMethodCall((OAVJavaType)type, name, paramvalues, context, helper);
						suffs.add(mc);
						type	= context.getTypeModel().getJavaType(mc.getMethod().getReturnType());
					}
					else
					{
						throw new RuntimeException("Method invocation not supported on type: "+type);
					}
				}
				else if(suffixes[i] instanceof ArrayAccess)
				{
					Expression	index	= flattenToPrimary(((ArrayAccess)suffixes[i]).getIndex(), context, helper);
					Object	indexsource;
					if(index instanceof VariableExpression)
					{
						indexsource	= ((VariableExpression)index).getVariable();
					}
					else //if(p instanceof LiteralExpression)
					{
						indexsource	= new Constant(((LiteralExpression)index).getValue());
					}
					suffs.add(new ArraySelector(indexsource));
					type	= context.getTypeModel().getJavaType(((OAVJavaType)type).getClazz().getComponentType());
				}
				else
				{
					throw new RuntimeException("Unknown suffix element: "+suffixes[i]);
				}
			}

			if(valuesource!=null)
			{
				suffs	= combineValueSources(valuesource, suffs);
			}
			valuesource	= suffs.size()==1 ? suffs.get(0) : suffs;
		}
		else if(value instanceof OperationExpression)
		{
			OperationExpression	opex	= (OperationExpression)value;
			IFunction	func;
			if(opex.getOperator() instanceof IFunction)
				func	= (IFunction)opex.getOperator();
			else
				func	= new OperatorFunction((IOperator)opex.getOperator());
			
			Object leftsource	= getObjectConditionAndValueSource(opex.getLeftValue(), context, helper);
			// Skip pop/push of same condition.
			Object	right	= flattenToPrimary(opex.getRightValue(), context, helper);
			if(right instanceof VariableExpression)
			{
				right	= ((VariableExpression)right).getVariable();
			}
			else //if(right instanceof LiteralExpression)
			{
				right	= new Constant(((LiteralExpression)right).getValue());
			}

			valuesource	= new FunctionCall(func, new Object[]{leftsource, right});
		}
		else if(value instanceof LiteralExpression)
		{
			Constant	c	= new Constant(((LiteralExpression)value).getValue());
			valuesource	= new FunctionCall(new Identity(), new Object[]{c});
			context.pushCondition(context.getDummyCondition());
		}
		else if(value instanceof ConditionalExpression)
		{
			ConditionalExpression	coex	= (ConditionalExpression)value;
			Object	choice	= getObjectConditionAndValueSource(coex.getCondition(), context, helper);
			// Skip pop/push of same condition.
			Object	first	= flattenToPrimary(coex.getFirstValue(), context, helper);
			Object	second	= flattenToPrimary(coex.getSecondValue(), context, helper);
			if(first instanceof VariableExpression)
			{
				first	= ((VariableExpression)first).getVariable();
			}
			else //if(first instanceof LiteralExpression)
			{
				first	= new Constant(((LiteralExpression)first).getValue());
			}
			if(second instanceof VariableExpression)
			{
				second	= ((VariableExpression)second).getVariable();
			}
			else //if(second instanceof LiteralExpression)
			{
				second	= new Constant(((LiteralExpression)second).getValue());
			}
			
			valuesource	= new FunctionCall(ConditionalExpression.FUNCTION_CONDITIONAL, new Object[]{choice, first, second});
		}
		else if(value instanceof StaticMethodAccess)
		{
			context.pushCondition(context.getDummyCondition());

			StaticMethodAccess	sma	= (StaticMethodAccess)value;
			MethodCall mc = createMethodCall(sma.getType(), sma.getName(), sma.getParameterValues(), context, helper);

			List	paramsources	= mc.getParameterSources();
			paramsources.add(0, new Constant(null));	// first param is object to be invoked (use null for static method).
			valuesource	= new FunctionCall(new MethodCallFunction(mc.getMethod()), paramsources);
		}
		else if(value instanceof CollectExpression)
		{
			CollectExpression	colex	= (CollectExpression)value;
			BuildContext	newcon	= new BuildContext(context);
			ICondition	cond	= buildConstraints(colex.getExpression(), newcon, helper);
			CollectCondition	colco; 
			if(cond instanceof ObjectCondition)
			{
				colco	= new CollectCondition((ObjectCondition)cond);
			}
			else if(cond instanceof AndCondition)
			{
				colco	= new CollectCondition(((AndCondition)cond).getConditions(), null);
			}
			else
			{
				throw new RuntimeException("Cannot collect: "+cond);
			}
			context.addCondition(colco);
			context.pushCondition(colco);
			valuesource	= null;
		}
		
		// Unary
		// ExistentialDeclaration
		else
		{
			throw new RuntimeException("Unsupported left hand side of constraint: "+value);
		}
		
		assert context.oconstack.size()==stacksize+1;
		return valuesource;
	}

	/**
	 *  Create a method call.
	 * @param type	The object type.
	 * @param name	The method name.
	 * @param paramvalues	The parameter values.
	 * @param context	The build context.
	 * @return	The method call object.
	 */
	protected static MethodCall createMethodCall(OAVJavaType type, String name, Expression[] paramvalues, BuildContext context, IParserHelper helper)
	{
		Object[]	params	= new Object[paramvalues!=null ? paramvalues.length : 0];
		Class[]	paramtypes	= new Class[params.length];
		for(int j=0; j<params.length; j++)
		{
			Expression	p	= flattenToPrimary(paramvalues[j], context, helper);
			if(p instanceof VariableExpression)
			{
				params[j]	= ((VariableExpression)p).getVariable();
				if(((Variable) params[j]).getType() instanceof OAVJavaType)
				{
					paramtypes[j]	= ((OAVJavaType)((Variable)params[j]).getType()).getClazz(); 
				}
				else
				{
					// Hack!!! Support state ids as objects? required for conditions based on state objects (e.g. attr.contains($obj))
					paramtypes[j]	= Object.class;
//					throw new RuntimeException("Cannot build method call: Only Java types supported for parameters: "+paramvalues[j]);
				}
			}
			else //if(p instanceof LiteralExpression)
			{
				Object	val	= ((LiteralExpression)p).getValue();
				params[j]	= new Constant(val);
				paramtypes[j]	= val!=null ? val.getClass() : null;
			}
		}
		
		Class	clazz	= type.getClazz();
		Method[]	methods	= SReflect.getMethods(clazz, name);
		Class[][]	mparamtypes	= new Class[methods.length][];
		for(int j=0; j<methods.length; j++)
		{
			mparamtypes[j]	= methods[j].getParameterTypes(); 
		}
		int[]	matches	= SReflect.matchArgumentTypes(paramtypes, mparamtypes);
		if(matches.length==0)
		{
			throw new RuntimeException("No matching method found for: "+clazz.getName()+"."+name+SUtil.arrayToString(paramvalues));
		}
		else if(matches.length>1)
		{
			System.out.println("Warning: Multiple matching methods found for: "+clazz.getName()+"."+name+SUtil.arrayToString(paramvalues));
		}
		MethodCall	mc	= new MethodCall(type, methods[matches[0]], params);
		return mc;
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
	protected static Expression	flattenToPrimary(Expression value, BuildContext context, IParserHelper helper)
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
			Object	valuesource	= getObjectConditionAndValueSource(value, context, helper);
			ret	= new VariableExpression(context.generateVariableBinding(context.getCurrentCondition(), valuesource));
			context.popCondition();
		}
		else if(value instanceof OperationExpression)
		{
			OperationExpression	opex	= (OperationExpression)value;
			IFunction	func;
			if(opex.getOperator() instanceof IFunction)
				func	= (IFunction)opex.getOperator();
			else
				func	= new OperatorFunction((IOperator)opex.getOperator());
			
			Object	left	= getObjectConditionAndValueSource(opex.getLeftValue(), context, helper);
			Object	right	= flattenToPrimary(opex.getRightValue(), context, helper);
			if(right instanceof VariableExpression)
			{
				right	= ((VariableExpression)right).getVariable();
			}
			else //if(right instanceof LiteralExpression)
			{
				right	= new Constant(((LiteralExpression)right).getValue());
			}

			Object	valuesource	= new FunctionCall(func, new Object[]{left, right});
			ret	= new VariableExpression(context.generateVariableBinding(context.getCurrentCondition(), valuesource));
			context.popCondition();
		}
		else if(value instanceof CastExpression)
		{
			Expression	prim	= flattenToPrimary(((CastExpression)value).getValue(), context, helper);
			if(prim instanceof VariableExpression)
			{
				VariableExpression	varex	= (VariableExpression)prim;
				ConstrainableCondition	ocon	= context.getConstrainableCondition(varex.getVariable());
				Object	valuesource	= context.getBoundConstraint(varex.getVariable()).getValueSource();
				OAVObjectType	type	= ((CastExpression)value).getType();
				ret	= new VariableExpression(context.generateVariableBinding(ocon, context.generateVariableName(), type, valuesource));
			}
			else
			{
				throw new UnsupportedOperationException("Todo: flatten expressions of type: "+value);
			}
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
	protected static void	shuffle(BuildContext context)
	{
		List lcons	= context.getConditions();
		Set	boundvars	= new HashSet();	// Variables, which are bound and therefore can be used.
		if(context.getParent()!=null)
			boundvars.addAll(context.getParent().getBoundVariables());
		boolean	progress	= true;
		int	finished	= 0;
		
		while(progress)
		{
			progress	= false;
			int skipped	= 0;
			for(int i=finished; i<lcons.size(); i++)
			{
				ICondition	con	= (ICondition) lcons.get(i-skipped);
				boolean	check	= true;

				// Find variables, which are bound (i.e. operator EQUAL) in this condition.
				Set	localbound	= new HashSet();
				if(con instanceof ConstrainableCondition)
				{
					List	bcs	= ((ConstrainableCondition)con).getBoundConstraints();
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
				}
				else if(con instanceof TestCondition)
				{
					FunctionCall	func	= ((TestCondition)con).getConstraint().getFunctionCall();
					if(func.getFunction() instanceof OperatorFunction && ((OperatorFunction)func.getFunction()).getOperator().equals(IOperator.EQUAL))
					{
						List	ps	= func.getParameterSources();
						if(ps.get(0) instanceof Variable)
						{
							localbound.add(ps.get(0));
						}
					}
				}
				else if(con instanceof NotCondition)
				{
					// Put not conditions last.
					for(int j=i-skipped+1; check && j<lcons.size(); j++)
						check	= lcons.get(j) instanceof NotCondition;
				}
				
				// Check if all variables are bound.
				if(check)
				{
					List	vars	= con.getVariables();
					for(int j=0; check && j<vars.size(); j++)
					{
						// Variable must be bound before or in this condition.
						check	= boundvars.contains(vars.get(j)) || localbound.contains(vars.get(j));
					}
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
					lcons.remove(i-skipped);
					lcons.add(con);
					skipped++;
				}
			}
		}

		// When not all conditions are finished there is a cycle in the unbound variables.
		if(finished<lcons.size())
		{
			// Break the cycle by splitting the first unfinished condition.
			ICondition	con	= (ICondition) lcons.get(finished);
			if(!(con instanceof ObjectCondition))
			{
				throw new UnsupportedOperationException("Cycle(?) in non-object condition: "+lcons);
			}

			// Scan all constraints and retain those, which cannot be evaluated.
			List	constraints	= new ArrayList(((ObjectCondition)con).getConstraints());
			progress	= true;
			while(progress)
			{
				progress	= false;
				for(Iterator it=constraints.iterator(); it.hasNext(); )
				{
					IConstraint	c	= (IConstraint)it.next();
					Set	localbound	= new HashSet(boundvars);
					if(c instanceof BoundConstraint && ((BoundConstraint)c).getOperator().equals(IOperator.EQUAL))
					{
						List	bvars	= ((BoundConstraint)c).getBindVariables();
						for(int k=0; k<bvars.size(); k++)
						{
							localbound.add(bvars.get(k));
						}
					}

					if(localbound.containsAll(c.getVariables()))
					{
						it.remove();
						progress	= true;
						boundvars.addAll(localbound);
					}
				}
			}
			
			BoundConstraint	bc	= new BoundConstraint(null, new Variable(context.generateVariableName(),((ObjectCondition)con).getObjectType(), false, true));
			List	constraints2	= new ArrayList(((ObjectCondition)con).getConstraints());
			constraints2.removeAll(constraints);
			constraints.add(bc);
			constraints2.add(bc);
			context.getConditions().remove(con);
			context.createObjectCondition(((ObjectCondition)con).getObjectType(), (IConstraint[])constraints2.toArray(new IConstraint[constraints2.size()]));
			context.createObjectCondition(((ObjectCondition)con).getObjectType(), (IConstraint[])constraints.toArray(new IConstraint[constraints.size()]));
			shuffle(context);
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
