package jadex.bdiv3.runtime.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.bdiv3.model.MBelief;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;


/**
 *  Transferable information about a belief.
 */
public class BeliefInfo	extends AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The belief kind (belief or beliefset). */
	protected String	kind;
	
	/** The belief value type (e.g. int). */
	protected String	valuetype;
	
	/** The value(s) (string for belief or array of strings for belief set). */
	protected Object value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new belief info.
	 */
	public BeliefInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new belief info.
	 */
	public BeliefInfo(Object id, String kind, String type, String valuetype, Object value)
	{
		super(id, type);
		this.kind	= kind;
		this.valuetype	= valuetype;
		this.value	= value;
	}
	
	//--------- methods ---------
	
	/**
	 *  Return the kind.
	 */
	public String getKind()
	{
		return kind;
	}

	/**
	 *  Set the kind.
	 */
	public void setKind(String kind)
	{
		this.kind = kind;
	}

	/**
	 *  Return the value type.
	 */
	public String getValueType()
	{
		return valuetype;
	}

	/**
	 *  Set the value type.
	 */
	public void setValueType(String valuetype)
	{
		this.valuetype = valuetype;
	}

	/**
	 *  Return the value.
	 *  (string for belief or array of strings for belief set)
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  (string for belief or array of strings for belief set)
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "BeliefInfo(id="+id
			+ ", kind=" + this.kind 
			+ ", type=" + this.type
			+ ", valuetype=" + this.valuetype
			+ ", value=" + this.value 
			+ ")";
	}

	//-------- helper methods --------
	
	/**
	 *  Create an info object for a belief.
	 */
	public static BeliefInfo	createBeliefInfo(IInternalAccess agent, MBelief mbel, ClassLoader cl)
	{
		String id = mbel.getName();
//		String	id	= belief.toString();
		if(id.indexOf('@')!=-1)	// 'belief_<num>@stateid'
		{
			id	= id.substring(0, id.indexOf('@'));
		}
		if(id.startsWith("belief_"))	// 'belief_<num>@stateid'
		{
			id	= id.substring(5);
		}
		if(id.startsWith("beliefset_"))	// 'beliefset_<num>@stateid'
		{
			id	= id.substring(8);
		}
		
//		Object	mbelief	= state.getAttributeValue(belief, OAVBDIRuntimeModel.element_has_model);
		String kind	= "belief"; 
		String type	= mbel.getName();
		String valuetype	= SReflect.getInnerClassName(mbel.getType(cl));
		Object value = mbel.getValue(agent);
		
		if(SReflect.isIterable(value))
		{
			List<String> vals = new ArrayList<String>();
			for(Iterator<Object> it=SReflect.getIterator(value); it.hasNext(); )
			{
				vals.add("" + it.next());
			}
			value = vals;
		}
		else if(value!=null)
		{
			value = value.toString();
		}
		else
		{
			value = "null";
		}
		
		return new BeliefInfo(id, kind, type, valuetype, value);
	}
	
//	/**
//	 *  Create an info object for a belief.
//	 */
//	public static BeliefInfo	createBeliefInfo(IOAVState state, Object belief, Object scope)
//	{
//		String	id	= belief.toString();
//		if(id.indexOf('@')!=-1)	// 'belief_<num>@stateid'
//		{
//			id	= id.substring(0, id.indexOf('@'));
//		}
//		if(id.startsWith("belief_"))	// 'belief_<num>@stateid'
//		{
//			id	= id.substring(5);
//		}
//		if(id.startsWith("beliefset_"))	// 'beliefset_<num>@stateid'
//		{
//			id	= id.substring(8);
//		}
//		Object	mbelief	= state.getAttributeValue(belief, OAVBDIRuntimeModel.element_has_model);
//		String	kind	= state.getType(belief).equals(OAVBDIRuntimeModel.belief_type) ? "belief" : "beliefset"; 
//		String	type	= (String)state.getAttributeValue(mbelief, OAVBDIMetaModel.modelelement_has_name);
//		String	valuetype	= SReflect.getInnerClassName((Class)state.getAttributeValue(mbelief, OAVBDIMetaModel.typedelement_has_class));
//		Object	value;
//		if(state.getType(belief).equals(OAVBDIRuntimeModel.belief_type))
//		{
//			value	= "" + state.getAttributeValue(belief, OAVBDIRuntimeModel.belief_has_fact); 
//		}
//		else
//		{
//			Collection	coll	= state.getAttributeValues(belief, OAVBDIRuntimeModel.beliefset_has_facts);
//			if(coll!=null)
//			{
//				String[]	vals	= new String[coll.size()];
//				Iterator	it	= coll.iterator();
//				for(int i=0; i<vals.length; i++)
//				{
//					vals[i]	= "" + it.next();
//				}
//				value	= vals;
//			}
//			else
//			{
//				value	= SUtil.EMPTY_STRING_ARRAY;
//			}
//		}
//		
//		if(scope!=null)
//		{
//			BDIInterpreter interpreter	= BDIInterpreter.getInterpreter(state);
//			List	path	= new ArrayList();
//			if(interpreter.findSubcapability(interpreter.getAgent(), scope, path))
//			{
//				for(int i=path.size()-1; i>=0; i--)
//				{
//					type	= state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name) + "." + type;
//				}
//			}
//		}
//		return new BeliefInfo(id, kind, type, valuetype, value);
//	}
}
