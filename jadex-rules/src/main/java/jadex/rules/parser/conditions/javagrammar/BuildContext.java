package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	//-------- constructors --------
	
	/**
	 *  Create a new build context.
	 *  @param condition	The initial condition.
	 */
	public BuildContext(ICondition condition, OAVTypeModel tmodel)
	{
		this.tmodel	= tmodel;
		
		// Unfold AND conditions.
		this.lcons	= new ArrayList();
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

		// Find conditions, bound to specific variables.
		this.variables	= new HashMap();
		this.boundconstraints	= new HashMap(); 
		this.bcons	= new HashMap();
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
		ObjectCondition	ret	= (ObjectCondition)bcons.get(var);
		if(ret==null)
		{
			throw new RuntimeException("No object condition for: "+var);
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
		BoundConstraint	ret	= (BoundConstraint)boundconstraints.get(var);
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
		String	varname;
		for(int i=1; variables.containsKey(varname = "$tmpvar_"+i); i++);
		Variable	tmpvar	= new Variable(varname, getReturnType(valuesource, tmodel));
		variables.put(varname, tmpvar);
		BoundConstraint	bc	= new BoundConstraint(valuesource, tmpvar);
		boundconstraints.put(tmpvar, bc);
		condition.addConstraint(bc);
		return tmpvar;
	}

	/**
	 *  Create a new object condition with the given constraints.
	 *  Also adds mappings corresponding to bound constraints (if any).
	 *  @param type	The object type.
	 *  @param constraints	The constraints (if any).
	 */
	public void createObjectCondition(OAVObjectType type, IConstraint[] constraints)
	{
		ObjectCondition	ocon	= new ObjectCondition(type);
		lcons.add(ocon);
		for(int i=0; constraints!=null && i<constraints.length; i++)
		{
			ocon.addConstraint(constraints[i]);
			if(constraints[i] instanceof BoundConstraint)
			{
				BoundConstraint	bc	= (BoundConstraint)constraints[i];
				List	bvars	= bc.getBindVariables();
				for(int k=0; k<bvars.size(); k++)
				{
					variables.put(((Variable)bvars.get(k)).getName(), bvars.get(k));
					// Todo: by multiple ocurrences use the simplest bc.getValueSource() 
					if(bc.getOperator().equals(IOperator.EQUAL))
					{
						boundconstraints.put(bvars.get(k), bc);
						bcons.put(bvars.get(k), ocon);
					}
				}
			}
		}
	}

	/**
	 *  Get a variable.
	 *  @param name	The name of the variable.
	 *  @return The variable, if any.
	 */
	public Variable getVariable(String name)
	{
		return (Variable)variables.get(name);
	}

	//-------- helper methods --------
	
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
		else if(valuesource instanceof FunctionCall)
		{
			ret	= tmodel.getJavaType(((FunctionCall)valuesource).getFunction().getReturnType());
		}
		else
		{
			throw new RuntimeException("Unknown value source type: "+valuesource);
		}
		
		return ret;
	}
}
