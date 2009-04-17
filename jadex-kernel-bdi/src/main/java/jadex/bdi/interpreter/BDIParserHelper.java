package jadex.bdi.interpreter;

import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.impl.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.CapabilityFlyweight;
import jadex.commons.SReflect;
import jadex.rules.parser.conditions.javagrammar.DefaultParserHelper;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.Constant;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 *  Handler for BDI-specific parsing issues ($beliefbase etc.)
 */
public class BDIParserHelper extends	DefaultParserHelper
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;

	/** The local scope (mcapability). */
	protected Object	mcapa;
	
	/** The local element, if any (e.g. mgoal or mplan). */
	protected Object	melement;
	
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
		super(condition, state.getTypeModel());
		this.state	= state;
		this.mcapa	= mcapa;
		this.melement	= melement;
	}
	
	//-------- IParserHelper interface --------
	
	/**
	 *  Get a variable with a given name.
	 *  @param	name	The variable name.
	 *  @return The variable.
	 */
	public Variable	getVariable(String name)
	{
		Variable	ret	= super.getVariable(name);
		
		if(ret==null && name.startsWith("$beliefbase."))
		{
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

			// Create condition for belief(set).
			String	belname	= name.substring(12);
			Object	mbel;
			if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, belname))!=null)
			{
				// Build belief condition to bind fact variable.
				Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	belvar	= new Variable(name+"_bel", OAVBDIRuntimeModel.belief_type);
				context.createObjectCondition(OAVBDIRuntimeModel.belief_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel),
					new BoundConstraint(OAVBDIRuntimeModel.belief_has_fact, ret),
					new BoundConstraint(null, belvar)});
				
				rcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, belvar, IOperator.CONTAINS));
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, belname))!=null)
			{
				// Build belief set condition to bind facts variable.
				Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
				clazz	= Array.newInstance(SReflect.getWrappedType(clazz), 0).getClass();
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	belvar	= new Variable(name+"_bel", OAVBDIRuntimeModel.beliefset_type);
				Object	valuesource	= new FunctionCall(new SetToArray(clazz), new Object[]{OAVBDIRuntimeModel.beliefset_has_facts});
				context.createObjectCondition(OAVBDIRuntimeModel.beliefset_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel),
					new BoundConstraint(valuesource, ret),
					new BoundConstraint(null, belvar)});
				
				rcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, belvar, IOperator.CONTAINS));
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname))!=null)
			{
				// (ResolvesTo ?rcapa "mycap.mybelief" ?rbel ?rtargetscope)
				String	ref	= (String)state.getAttributeValue(mbel, OAVBDIMetaModel.elementreference_has_concrete);
				String	tmpbelname	= ref;
				int	idx=tmpbelname.indexOf('.');
				String	capname	= tmpbelname.substring(0, idx);
				tmpbelname = tmpbelname.substring(idx+1);
				Object	mcaparef	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
				if(mcaparef==null)
					throw new RuntimeException("Could not resolve reference to belief: "+name+", "+ref);
				Object	tmpmcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				while((mbel=state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_beliefs, tmpbelname))==null)
				{
					mbel	= state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_beliefrefs, tmpbelname);
					if(mbel==null)
						throw new RuntimeException("Could not resolve reference to belief: "+name+", "+ref);
					tmpbelname	= (String)state.getAttributeValue(mbel, OAVBDIMetaModel.elementreference_has_concrete);
					idx=tmpbelname.indexOf('.');
					capname	= tmpbelname.substring(0, idx);
					tmpbelname = tmpbelname.substring(idx+1);
					mcaparef	= state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
					if(mcaparef==null)
						throw new RuntimeException("Could not resolve reference to belief: "+name+", "+ref);
					tmpmcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				}
				
				// Build belief condition to bind fact variable.
				Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	belvar	= new Variable(name+"_bel", OAVBDIRuntimeModel.belief_type);
				context.createObjectCondition(OAVBDIRuntimeModel.belief_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel),
					new BoundConstraint(OAVBDIRuntimeModel.belief_has_fact, ret),
					new BoundConstraint(null, belvar)});
				Variable	belcapvar	= new Variable(name+"_belcap", OAVBDIRuntimeModel.capability_type);

				context.createObjectCondition(OAVBDIRuntimeModel.capability_type, new IConstraint[]{
						new BoundConstraint(null, belcapvar),
						new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, belvar, IOperator.CONTAINS),
						new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{capvar, new Constant(ref), belvar, belcapvar}))});
			}
			else if((mbel=state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, belname))!=null)
			{
				// (ResolvesTo ?rcapa "mycap.mybelief" ?rbel ?rtargetscope)
				String	ref	= (String)state.getAttributeValue(mbel, OAVBDIMetaModel.elementreference_has_concrete);
				String	tmpbelname	= ref;
				int	idx=tmpbelname.indexOf('.');
				String	capname	= tmpbelname.substring(0, idx);
				tmpbelname = tmpbelname.substring(idx+1);
				Object	mcaparef	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
				if(mcaparef==null)
					throw new RuntimeException("Could not resolve reference to beliefset: "+name+", "+ref);
				Object	tmpmcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				while((mbel=state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_beliefsets, tmpbelname))==null)
				{
					mbel	= state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, tmpbelname);
					if(mbel==null)
						throw new RuntimeException("Could not resolve reference to beliefset: "+name+", "+ref);
					tmpbelname	= (String)state.getAttributeValue(mbel, OAVBDIMetaModel.elementreference_has_concrete);
					idx=tmpbelname.indexOf('.');
					capname	= tmpbelname.substring(0, idx);
					tmpbelname = tmpbelname.substring(idx+1);
					mcaparef	= state.getAttributeValue(tmpmcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
					if(mcaparef==null)
						throw new RuntimeException("Could not resolve reference to beliefset: "+name+", "+ref);
					tmpmcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				}
				
				// Build belief set condition to bind facts variable.
				Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
				clazz	= Array.newInstance(SReflect.getWrappedType(clazz), 0).getClass();
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	belvar	= new Variable(name+"_bel", OAVBDIRuntimeModel.beliefset_type);
				Object	valuesource	= new FunctionCall(new SetToArray(clazz), new Object[]{OAVBDIRuntimeModel.beliefset_has_facts});
				context.createObjectCondition(OAVBDIRuntimeModel.beliefset_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mbel),
					new BoundConstraint(valuesource, ret),
					new BoundConstraint(null, belvar)});
				Variable	belcapvar	= new Variable(name+"_belcap", OAVBDIRuntimeModel.capability_type);

				context.createObjectCondition(OAVBDIRuntimeModel.capability_type, new IConstraint[]{
						new BoundConstraint(null, belcapvar),
						new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, belvar, IOperator.CONTAINS),
						new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{capvar, new Constant(ref), belvar, belcapvar}))});				
			}
			else
			{
				throw new RuntimeException("No such belief (set): "+name);
			}		
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

			Object	mpar;
			if((mpar=state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parameters, parname))!=null)
			{
				// Build parameter condition to bind value variable.
				Class	clazz	= SReflect.getWrappedType((Class)state.getAttributeValue(mpar, OAVBDIMetaModel.typedelement_has_class));
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	parvar	= new Variable(name+"par", OAVBDIRuntimeModel.parameter_type);
				context.createObjectCondition(OAVBDIRuntimeModel.parameter_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.parameter_has_name, parname),
					new BoundConstraint(OAVBDIRuntimeModel.parameter_has_value, ret),
					new BoundConstraint(null, parvar)});

				rgoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parameters, parvar, IOperator.CONTAINS));
			}
			else if((mpar=state.getAttributeValue(melement, OAVBDIMetaModel.parameterelement_has_parametersets, parname))!=null)
			{
				// Build parameter set condition to bind values variable.
				Class	clazz	= SReflect.getWrappedType((Class)state.getAttributeValue(mpar, OAVBDIMetaModel.typedelement_has_class));
				clazz	= Array.newInstance(SReflect.getWrappedType(clazz), 0).getClass();
				ret	= new Variable(name, state.getTypeModel().getJavaType(clazz));
				Variable	parvar	= new Variable(name+"par", OAVBDIRuntimeModel.parameterset_type);
				Object	valuesource	= new FunctionCall(new SetToArray(clazz), new Object[]{OAVBDIRuntimeModel.parameterset_has_values});
				context.createObjectCondition(OAVBDIRuntimeModel.parameterset_type, new IConstraint[]{
					new LiteralConstraint(OAVBDIRuntimeModel.parameterset_has_name, parname),
					new BoundConstraint(valuesource, ret),
					new BoundConstraint(null, parvar)});

				rgoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parametersets, parvar, IOperator.CONTAINS));
			}

			if(mpar==null)
			{
				throw new RuntimeException("No such parameter (set): "+name);
			}
		}
		
		else if(ret==null && "$beliefbase".equals(name))
		{
			Variable	capvar	= context.getVariable("?rcapa");
			if(capvar==null)
				throw new RuntimeException("Variable '?rcapa' required to build beliefbase constraint: "+name);
			ObjectCondition	rcapcon	= context.getObjectCondition(capvar);
			if(rcapcon==null)
				throw new RuntimeException("Capability condition required to build beliefbase constraint: "+name);
			Object	valuesource	= new FunctionCall(new IFunction()
			{
				public Set getRelevantAttributes()
				{
					return Collections.EMPTY_SET;
				}
				public Class getReturnType()
				{
					return IBeliefbase.class;
				}
				public Object invoke(Object[] paramvalues, IOAVState state)
				{
					return BeliefbaseFlyweight.getBeliefbaseFlyweight(state, paramvalues[0]);
				}
			}, new Object[]{capvar});
			ret	= context.generateVariableBinding(rcapcon, name, valuesource);
		}

		else if(ret==null && "$scope".equals(name))
		{
			Variable	capvar	= context.getVariable("?rcapa");
			if(capvar==null)
				throw new RuntimeException("Variable '?rcapa' required to build beliefbase constraint: "+name);
			ObjectCondition	rcapcon	= context.getObjectCondition(capvar);
			if(rcapcon==null)
				throw new RuntimeException("Capability condition required to build beliefbase constraint: "+name);
			Object	valuesource	= new FunctionCall(new IFunction()
			{
				public Set getRelevantAttributes()
				{
					return Collections.EMPTY_SET;
				}
				public Class getReturnType()
				{
					return ICapability.class;
				}
				public Object invoke(Object[] paramvalues, IOAVState state)
				{
					return new CapabilityFlyweight(state, paramvalues[0]);
				}
			}, new Object[]{capvar});
			ret	= context.generateVariableBinding(rcapcon, name, valuesource);
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

	//-------- helper classes --------
	
	/**
	 *  Convert belief set or parameter set contents to array.
	 */
	public static class SetToArray	implements	IFunction
	{
		//-------- attributes --------
		
		/** The array type. */
		protected Class	type;
		
		//-------- constructors --------
		
		/**
		 *  Create a new set-to-array function.
		 *  @param type	The array type.
		 */
		public SetToArray(Class type)
		{
			this.type	= type;
		}
		
		//-------- IFunction interface --------
		
		/**
		 *  Invoke a function and return a value (optional).
		 *  @param paramvalues The parameter values.
		 *  @param state The state.
		 *  @return The function value. 
		 */
		public Object invoke(Object[] paramvalues, IOAVState state)
		{
			if(paramvalues==null || paramvalues.length!=1)
				throw new RuntimeException("SetToArray requires one parameter: "+this);
			
			Collection	coll	= (Collection)paramvalues[0];
			return coll!= null
				? coll.toArray((Object[])Array.newInstance(type.getComponentType(), coll.size()))
				: Array.newInstance(type.getComponentType(), 0);
		}
		
		/**
		 *  Get the return type of this function.
		 */
		public Class getReturnType()
		{
			return type;
		}
		
		/**
		 *  Get the set of relevant attribute types.
		 *  @return The relevant attribute types.
		 */
		public Set	getRelevantAttributes()
		{
			return Collections.EMPTY_SET;
		}

		/**
		 *  Create a string representation
		 */
		public String	toString()
		{
			return "toArray";
		}
	}
}
