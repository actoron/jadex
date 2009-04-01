package jadex.bdi.interpreter;

import jadex.rules.parser.conditions.javagrammar.BuildContext;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.lang.reflect.Array;
import java.util.List;

/**
 *  Handler for BDI-specific parsing issues ($beliefbase etc.)
 */
public class BDIParserHelper implements IParserHelper
{
	//-------- attributes --------
	
	/** The build context. */
	protected BuildContext	context;
	
	/** The local scope (mcapability). */
	protected Object	mcapa;
	
	/** The local element, if any (e.g. mgoal or mplan). */
	protected Object	melement;
	
	/** The state. */
	protected IOAVState	state;
	
	//-------- constructors --------
	
	/**
	 *  Create a BDI parser helper.
	 *  @param condition	The predefined condition.
	 *  @param mcapa	The local scope (mcapability).
	 *  @param melement	The local element, if any (e.g. mgoal or mplan).
	 *  @param state	The state.
	 *  @param returnvar	The return value variable (if return value condition).
	 */
	public BDIParserHelper(ICondition condition, Object mcapa, Object melement, IOAVState state)
	{
		this.mcapa	= mcapa;
		this.melement	= melement;
		this.state	= state;
		this.context	= new BuildContext(condition, state.getTypeModel());
	}
	
	//-------- IParserHelper interface --------
	
	/**
	 *  Get a variable with a given name.
	 *  @param	name	The variable name.
	 *  @return The variable.
	 */
	public Variable	getVariable(String name)
	{
		Variable	ret	= context.getVariable(name);
		
		if(ret==null && name.startsWith("$beliefbase."))
		{
			// Create condition for belief(set).
			String	belname	= name.substring(12);
			Object	mbel;
			OAVObjectType	type	= null;
			Class	clazz	= null;
			OAVAttributeType	attr1	= null;
			OAVAttributeType	attr2	= null;
			if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, belname))!=null)
			{
				type	= OAVBDIRuntimeModel.belief_type;
				attr1	= OAVBDIRuntimeModel.capability_has_beliefs;
				attr2	= OAVBDIRuntimeModel.belief_has_fact;
				clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, belname))!=null)
			{
				type	= OAVBDIRuntimeModel.beliefset_type;
				attr1	= OAVBDIRuntimeModel.capability_has_beliefsets;
				attr2	= OAVBDIRuntimeModel.beliefset_has_facts;
				// Todo: set contents should be array instead of list
//				clazz	= List.class;
				clazz	= Array.newInstance((Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class), new int[1]).getClass();
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname))!=null)
			{
				throw new UnsupportedOperationException("Belief references not yet supported by parser: "+name);
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, belname))!=null)
			{
				throw new UnsupportedOperationException("Belief set references not yet supported by parser: "+name);
			}

			if(mbel==null)
			{
				throw new RuntimeException("No such belief (set): "+name);
			}
			
			// Build belief (set) condition to bind fact(s) variable.
			ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
			Variable	belvar	= new Variable(name+"_bel", type);
			context.createObjectCondition(type, new IConstraint[]{
				new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel),
				new BoundConstraint(attr2, ret),
				new BoundConstraint(null, belvar)});
			
			// Augment capability condition to check belief (set) variable.
			Variable	capvar	= context.getVariable("?rcapa");
			if(capvar==null)
				throw new RuntimeException("Variable '?rcapa' required to build belief (set) condition: "+name);
			ObjectCondition	rcapcon	= context.getObjectCondition(capvar);
			if(rcapcon==null)
				throw new RuntimeException("Capability condition required to build belief (set) condition: "+name);
			BoundConstraint	bc	= context.getBoundConstraint(capvar);
			if(bc.getValueSource()!=null)
				throw new UnsupportedOperationException("Value source for capability object not yet supported.");
			rcapcon.addConstraint(new BoundConstraint(attr1, belvar, IOperator.CONTAINS));
		}
		
		else if(ret==null && (name.startsWith("$goal.") || name.startsWith("$ref.")))
		{
			String	parname;
			String	varname;
			if(name.startsWith("$goal."))
			{
				parname	= name.substring(6);
				varname	= "?rgoal";
			}
			else
			{
				parname	= name.substring(5);
				varname	= "?refgoal";
			}
			
			OAVObjectType	type	= null;
			OAVAttributeType	attr1	= null;
			OAVAttributeType	attr2	= null;
			OAVAttributeType	attr3	= null;
			Class	clazz	= null;
			Object	mpar;
			if((mpar=state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parameters, parname))!=null)
			{
				type	= OAVBDIRuntimeModel.parameter_type;
				attr1	= OAVBDIRuntimeModel.parameterelement_has_parameters;
				attr2	= OAVBDIRuntimeModel.parameter_has_value;
				attr3	= OAVBDIRuntimeModel.parameter_has_name;
				clazz	= (Class)state.getAttributeValue(mpar, OAVBDIMetaModel.typedelement_has_class);
			}
			else if((mpar=state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parametersets, parname))!=null)
			{
				type	= OAVBDIRuntimeModel.parameterset_type;
				attr1	= OAVBDIRuntimeModel.parameterelement_has_parametersets;
				attr2	= OAVBDIRuntimeModel.parameterset_has_values;
				attr3	= OAVBDIRuntimeModel.parameterset_has_name;
				// Todo: set contents should be array instead of list
//				clazz	= List.class;
				clazz	= Array.newInstance((Class)state.getAttributeValue(mpar, OAVBDIMetaModel.typedelement_has_class), new int[1]).getClass();
			}

			if(mpar==null)
			{
				throw new RuntimeException("No such parameter (set): "+name);
			}

			// Build parameter (set) condition to bind value(s) variable.
			ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
			Variable	parvar	= new Variable(name+"par", type);
			context.createObjectCondition(type, new IConstraint[]{
				new LiteralConstraint(attr3, parname),
				new BoundConstraint(attr2, ret),
				new BoundConstraint(null, parvar)});

			// Augment goal condition to check parameter (set) variable.
			Variable	goalvar	= context.getVariable(varname);
			if(goalvar==null)
				throw new RuntimeException("Variable '"+varname+"' required to build parameter (set) condition: "+name);
			ObjectCondition	rgoalcon	= (ObjectCondition)context.getObjectCondition(goalvar);
			if(rgoalcon==null)
				throw new RuntimeException("Goal condition required to build parameter (set) condition: "+name);
			BoundConstraint	bc	= (BoundConstraint)context.getBoundConstraint(goalvar);
			if(bc!=null && bc.getValueSource()!=null)
				throw new UnsupportedOperationException("Value source for goal object not yet supported.");
			rgoalcon.addConstraint(new BoundConstraint(attr1, parvar, IOperator.CONTAINS));
		}

		return ret;
	}

	/**
	 *  Test, if a name refers to a pseudo variable (e.g. $beliefbase).
	 *  @param	name	The variable name.
	 *  @return True, if the name is a pseudo variable.
	 */
	public boolean	isPseudoVariable(String name)
	{
		return "$beliefbase".equals(name) || "$goal".equals(name) || "$ref".equals(name);
	}

	/**
	 *  Get the conditions after parsing.
	 */
	public List	getConditions()
	{
		return context.getConditions();
	}
}
