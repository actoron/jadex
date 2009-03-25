package jadex.bdi.interpreter;

import jadex.rules.parser.conditions.javagrammar.ConditionBuilder;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Handler for BDI-specific parsing issues ($beliefbase etc.)
 */
public class BDIParserHelper implements IParserHelper
{
	//-------- attributes --------
	
	/** The conditions. */
	protected List	lcons;
	
	/** The variable map. */
	protected Map	variables;
	
	/** The bound constraints map. */
	protected Map	boundconstraints;
	
	/** The object conditions map. */
	protected Map	bcons;
	
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
	 */
	public BDIParserHelper(ICondition condition, Object mcapa, Object melement, IOAVState state)
	{
		this.mcapa	= mcapa;
		this.melement	= melement;
		this.state	= state;

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
		
		this.variables	= new HashMap();
		this.boundconstraints	= new HashMap();
		this.bcons	= new HashMap();
		ConditionBuilder.buildConditionMap(lcons, variables, boundconstraints, bcons);
	}
	
	//-------- IParserHelper interface --------
	
	/**
	 *  Get a variable with a given name.
	 *  @param	name	The variable name.
	 *  @return The variable.
	 */
	public Variable	getVariable(String name)
	{
		Variable	ret	= (Variable)variables.get(name);
		
		if(ret==null && name.startsWith("$beliefbase."))
		{
			// Create condition for belief(set).
			String	belname	= name.substring(12);
			OAVObjectType	type	= OAVBDIRuntimeModel.belief_type;
			OAVAttributeType	attr1	= OAVBDIRuntimeModel.capability_has_beliefs;
			OAVAttributeType	attr2	= OAVBDIRuntimeModel.belief_has_fact;
			Object	mbel	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
			if(mbel==null)
			{
				type	= OAVBDIRuntimeModel.beliefset_type;
				attr1	= OAVBDIRuntimeModel.capability_has_beliefsets;
				attr2	= OAVBDIRuntimeModel.beliefset_has_facts;
				mbel	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, belname);
			}
			if(mbel==null)
			{
				mbel	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
				if(mbel!=null)
				{
					throw new UnsupportedOperationException("Belief references not yet supported by parser: "+name);
				}
			}
			if(mbel==null)
			{
				mbel	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, belname);
				if(mbel!=null)
				{
					throw new UnsupportedOperationException("Belief set references not yet supported by parser: "+name);
				}
			}

			if(mbel==null)
			{
				throw new RuntimeException("No such belief (set): "+name);
			}
			
			// Build belief (set) condition to bind fact(s) variable.
			Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
			ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
			Variable	belvar	= new Variable(name+"_bel", type);
			ObjectCondition	rbelcon	= new ObjectCondition(type);
			rbelcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel));
			rbelcon.addConstraint(new BoundConstraint(attr2, ret));
			rbelcon.addConstraint(new BoundConstraint(null, belvar));
			lcons.add(rbelcon);
			bcons.put(ret, rbelcon);
			variables.put(name, ret);
			
			// Augment capability condition to check belief (set) variable.
			Variable	capvar	= (Variable)variables.get("?rcapa");
			if(capvar==null)
				throw new RuntimeException("Variable '?rcapa' required to build belief condition: "+name);
			ObjectCondition	rcapcon	= (ObjectCondition)bcons.get(capvar);
			if(rcapcon==null)
				throw new RuntimeException("Capability condition required to build belief condition: "+name);
			BoundConstraint	bc	= (BoundConstraint)boundconstraints.get(capvar);
			if(bc!=null && bc.getValueSource()!=null)
				throw new UnsupportedOperationException("Value source for capability object not yet supported.");
			rcapcon.addConstraint(new BoundConstraint(attr1, belvar, IOperator.CONTAINS));
		}
		
		else if(ret==null && name.startsWith("$goal."))
		{
//			String	parname	= name.substring(6);
//			// Create condition for parameter(set).
//			OAVObjectType	type	= OAVBDIRuntimeModel.parameter_type;
//			OAVAttributeType	attr	= OAVBDIRuntimeModel.parameter_has_value;
//			Object	mpar	= state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parameters, parname);
//			if(mpar==null)
//			{
//				type	= OAVBDIRuntimeModel.parameterset_type;
//				attr	= OAVBDIRuntimeModel.parameterelement_has_parametersets;
//				mpar	= state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parametersets, parname);
//			}
//
//			if(mpar==null)
//			{
//				throw new RuntimeException("No such parameter (set): "+name);
//			}

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
		return "$beliefbase".equals(name) || "$goal".equals(name);
	}

	/**
	 *  Get the conditions after parsing.
	 */
	public List	getConditions()
	{
		return lcons;
	}
}
