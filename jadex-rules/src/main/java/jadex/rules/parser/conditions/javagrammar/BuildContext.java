package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The build context captures knowledge about
 *  conditions, variables, etc. during constraint
 *  parsing or building.
 */
public class BuildContext
{
	//-------- attributes --------
	
	/** The list of conditions. */
	protected List	lcons;
	
	/** The variables (name -> variable). */
	protected Map	variables;
	
	/** The bound constraints (variable -> boundconstraint (only variable definitions, i.e. EQUAL constraints)). */
	protected Map	boundconstraints;
	
	/** The object conditions (variable -> object conditions (object condition with defining bound constraint)). */
	protected Map	bcons;
	
	/** The OAV type model. */
	protected OAVTypeModel	tmodel;
	
	/** The dummy condition (if any). */
	protected ObjectCondition	dummy;
	
	/** The parent build context (if any). */
	protected BuildContext	parent;
	
	/** Stack for object conditions (for checking if constraints can be generated in current context). */
	protected List	oconstack;
	
	//-------- constructors --------
	
	/**
	 *  Create a new build context.
	 *  @param condition	The initial condition.
	 */
	public BuildContext(ICondition condition, OAVTypeModel tmodel)
	{
		this.tmodel	= tmodel;		
		this.lcons	= new ArrayList();
		this.variables	= new HashMap();
		this.boundconstraints	= new HashMap(); 
		this.bcons	= new HashMap();
		
		if(condition!=null)
			addCondition(condition);
	}
	
	/**
	 *  Create a new build context.
	 *  @param parent	The parent build context.
	 */
	public BuildContext(BuildContext parent)
	{
		this(null, parent.getTypeModel());
		this.parent	= parent;
	}
	
	//-------- methods --------

	/**
	 *  Get the conditions.
	 */
	public List getConditions()
	{
		return lcons;
	}

	/**
	 *  Get the OAV type model.
	 */
	public OAVTypeModel getTypeModel()
	{
		return tmodel;
	}

	/**
	 *  Get an object condition for a variable, i.e. a condition, where
	 *  constraints related to the variable can be added to.
	 *  @param var	The variable
	 *  @return The object condition.
	 *  @throws RuntimeExcpetion	when no condition was found.
	 */
	public ObjectCondition getObjectCondition(Variable var)
	{
		ObjectCondition	ret	= getObjectCondition0(var);
		if(ret==null)
		{
			throw new RuntimeException("No object condition for: "+var);
		}
		return ret;
	}

	/**
	 *  Get an object condition for a variable, i.e. a condition, where
	 *  constraints related to the variable can be added to.
	 *  @param var	The variable
	 *  @return The object condition.
	 *  @throws RuntimeExcpetion	when no condition was found.
	 */
	public ObjectCondition getObjectCondition0(Variable var)
	{
		ObjectCondition	ret	= null;
		try
		{
			ret	= parent!=null ? parent.getObjectCondition(var) : null;
		}
		catch(Exception e)
		{
		}
		if(ret==null)
		{
			ret	= (ObjectCondition)bcons.get(var);
		}
		
		return ret;
	}

	/**
	 *  Get the bound constraint a variable, i.e. the value source required
	 *  for obtaining the variable value from the variables object condition.
	 *  @param var	The variable
	 *  @return The bound constraint.
	 *  @throws RuntimeExcpetion	when no constraint was found.
	 */
	public BoundConstraint getBoundConstraint(Variable var)
	{
		BoundConstraint	ret	= null;
		try
		{
			ret	= parent!=null ? parent.getBoundConstraint(var) : null;
		}
		catch(Exception e)
		{
		}
		if(ret==null)
		{
			ret	= (BoundConstraint)boundconstraints.get(var);
		}
		
		if(ret==null)
		{
			throw new RuntimeException("No bound constraint for: "+var);
		}
		return ret;
	}

	/**
	 *  Create a new variable and bind it using the given object condition and value source.
	 *  @param condition	The object condition.
	 *  @param valuesource	The value source.
	 *  @return	The new variable.
	 */
	public Variable	generateVariableBinding(ObjectCondition	condition, Object valuesource)
	{
		return generateVariableBinding(condition, generateVariableName(), valuesource);
	}

	/**
	 *  Generate a variable name.
	 *  @return An unused variable name.
	 */
	public String generateVariableName()
	{
		String varname;
		for(int i=1; getVariable(varname = "$tmpvar_"+i)!=null; i++);
		return varname;
	}
	
	/**
	 *  Create a new variable and bind it using the given object condition and value source.
	 *  @param condition	The object condition.
	 *  @param name	The variable name.
	 *  @param valuesource	The value source.
	 *  @return	The new variable.
	 */
	public Variable	generateVariableBinding(ObjectCondition	condition, String name, Object valuesource)
	{
		return generateVariableBinding(condition, name, getReturnType(condition, valuesource, tmodel),	valuesource);
	}

	/**
	 *  Create a new variable and bind it using the given object condition and value source.
	 *  @param condition	The object condition.
	 *  @param name	The variable name.
	 *  @param valuesource	The value source.
	 *  @return	The new variable.
	 */
	public Variable	generateVariableBinding(ObjectCondition	condition, String name, OAVObjectType type, Object valuesource)
	{
		Variable	tmpvar	= new Variable(name, type, false, true);
		variables.put(name, tmpvar);
		BoundConstraint	bc	= new BoundConstraint(valuesource, tmpvar);
		boundconstraints.put(tmpvar, bc);
		condition.addConstraint(bc);
		bcons.put(tmpvar, condition);
		return tmpvar;
	}

	/**
	 *  Create a new object condition with the given constraints.
	 *  Also adds mappings corresponding to bound constraints (if any).
	 *  @param type	The object type.
	 *  @param constraints	The constraints (if any).
	 */
	public ObjectCondition	createObjectCondition(OAVObjectType type, IConstraint[] constraints)
	{
		ObjectCondition	ocon	= new ObjectCondition(type);
		for(int i=0; constraints!=null && i<constraints.length; i++)
		{
			ocon.addConstraint(constraints[i]);
		}
		
		addCondition(ocon);
		
		return ocon;
	}

	/**
	 *  Get a variable.
	 *  @param name	The name of the variable.
	 *  @return The variable, if any.
	 */
	public Variable getVariable(String name)
	{
		Variable	ret	= parent!=null ? parent.getVariable(name) : null;
		if(ret==null)
			ret	= (Variable)variables.get(name);
		return ret;
	}

	/**
	 *  Add a variable.
	 *  @param var The variable.
	 */
	public void	addVariable(Variable var)
	{
		variables.put(var.getName(), var);
	}

	/**
	 *  Expressions, which are unrelated to real object
	 *  conditions should be bound to the dummy condition.
	 *  After building all constraints, the dummy condition
	 *  will be removed by reassigning its constraints to
	 *  a suitable object condition (respecting variable assignment order).
	 */
	public ObjectCondition	getDummyCondition()
	{
		if(dummy==null)
		{
			dummy	= new ObjectCondition(OAVJavaType.java_object_type);
			addCondition(dummy);
		}
		return dummy;
	}

	/**
	 *  Test if a dummy condition was used in the context.
	 */
	public boolean hasDummyCondition()
	{
		return dummy!=null;
	}
	
	/**
	 *  Add a condition to the context.
	 * 	@param condition	The condition.
	 */
	public void addCondition(ICondition condition)
	{
		int start	= lcons.size();
		lcons.add(condition);
		
		// Unfold AND conditions.
		for(int i=start; i<lcons.size(); i++)
		{
			if(lcons.get(i) instanceof AndCondition)
			{
				lcons.addAll(i+1, ((AndCondition)lcons.get(i)).getConditions());
				lcons.remove(i);
				i--;	// Decrement to check new condition at i instead of continuing with i+1.
			}
		}

		// Find conditions, bound to specific variables.
		for(int i=start; i<lcons.size(); i++)
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

	//-------- helper methods --------
	
	/**
	 *  Get the return type of a value source.
	 *  @param valuesource	The value source.
	 *  @param tmodel	The type model.
	 *  @return The object type.
	 */
	protected static OAVObjectType	getReturnType(ObjectCondition cond, Object valuesource, OAVTypeModel tmodel)
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
		else if(valuesource instanceof FunctionCall)
		{
			ret	= tmodel.getJavaType(((FunctionCall)valuesource).getFunction().getReturnType());
		}
		else if(valuesource==null)
		{
			ret	= cond.getObjectType();
		}
		else
		{
			throw new RuntimeException("Unknown value source type: "+valuesource);
		}
		
		return ret;
	}

	/**
	 *  Return the parent build context (if any).
	 */
	public BuildContext	getParent()
	{
		return parent;
	}

	/**
	 *  Get the variables, which are available in this build context.
	 */
	public Set	getBoundVariables()
	{
		Set	ret	= new HashSet();
		if(getParent()!=null)
			ret.addAll(getParent().getBoundVariables());
		for(int i=0; i<lcons.size(); i++)
		{
			ICondition	con	= (ICondition) lcons.get(i);
			List	bcs	= null;
			if(con instanceof ObjectCondition)
			{
				bcs	= ((ObjectCondition)con).getBoundConstraints();
			}
			else if(con instanceof CollectCondition)
			{
				bcs	= ((CollectCondition)con).getBoundConstraints();
			}
			else if(con instanceof TestCondition)
			{
				FunctionCall	func	= ((TestCondition)con).getConstraint().getFunctionCall();
				if(func.getFunction() instanceof OperatorFunction && ((OperatorFunction)func.getFunction()).getOperator().equals(IOperator.EQUAL))
				{
					List	ps	= func.getParameterSources();
					if(ps.get(0) instanceof Variable)
					{
						ret.add(ps.get(0));
					}
				}
			}
			
			for(int j=0; bcs!=null && j<bcs.size(); j++)
			{
				BoundConstraint	bc	= (BoundConstraint)bcs.get(j);
				if(bc.getOperator().equals(IOperator.EQUAL))
				{
					List	bvars	= bc.getBindVariables();
					for(int k=0; k<bvars.size(); k++)
					{
						ret.add(bvars.get(k));
					}
				}
			}
		}
		return ret;
	}

	/**
	 *  Push a condition on the stack.
	 */
	public void	pushCondition(ObjectCondition con)
	{
		if(getDefiningScope(con)!=this)
		{
			// Create clone of inconsistent condition in inner scope.
			generateVariableBinding(con, null);	// new null bound constraint to make sure that cloned condition refers to SAME object.
			con	= createObjectCondition(con.getObjectType(), (IConstraint[])con.getConstraints().toArray(new IConstraint[con.getConstraints().size()]));
		}
		
		// Todo: remove stack (obsolete?)
		if(oconstack!=null)
		{
			// Check stack consistency: inner variables may only be defined in same scope or outside.
			BuildContext	scope	= getDefiningScope(con);
			for(int i=0; i<oconstack.size(); i++)
			{
				ObjectCondition	ocon	= (ObjectCondition)oconstack.get(i);
				boolean	consistent	= true;
				BuildContext	prescope	= getDefiningScope(ocon);
				consistent	= prescope==scope;
				while(!consistent && prescope!=null)
				{
					prescope	= prescope.getParent();
					consistent	= prescope==scope;
				}
				if(!consistent)
				{
					// Create clone of inconsistent condition in inner scope.
					Variable	var = scope.generateVariableBinding(ocon, null);
					ocon	= scope.createObjectCondition(ocon.getObjectType(), new IConstraint[]{new BoundConstraint(null, var)});
					oconstack.set(i, ocon);
					System.out.println("Clone for consistency: "+oconstack);
				}
			}
		}
		else
		{
			oconstack	= new ArrayList();
		}
		
		oconstack.add(con);
	}
	
	/**
	 *  Pop a condition from the stack.
	 */
	public void	popCondition()
	{
		if(oconstack==null || oconstack.isEmpty())
			throw new RuntimeException("Condition stack error: "+oconstack);
		
		if(oconstack.size()==1)
			oconstack	= null;
		else
			oconstack.remove(oconstack.size()-1);
	}
	
	/**
	 *  Get the current condition from the stack.
	 */
	public ObjectCondition	getCurrentCondition()
	{
		if(oconstack==null || oconstack.isEmpty())
			throw new RuntimeException("Condition stack error: "+oconstack);
		
		return (ObjectCondition)oconstack.get(oconstack.size()-1);
	}
	
	/**
	 *  Get the context in which the given condition is defined.
	 */
	protected BuildContext	getDefiningScope(ObjectCondition con)
	{
		BuildContext	ret	= null;
		BuildContext	scope	= this;
		while(scope!=null)
		{
			if(scope.lcons.contains(con))
				ret	= scope;
			scope	= scope.getParent();
		}
		return ret;
	}
}
