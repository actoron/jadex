package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

/**
 *  Transferable information about a belief.
 */
public class BeliefInfo
{
	//-------- attributes --------
	
	/** The belief id. */
	protected Object	id;
	
	/** The belief kind (belief or beliefset). */
	protected String	kind;
	
	/** The belief type (patrol_points). */
	protected String	type;
	
	/** The belief value type (e.g. int). */
	protected String	valuetype;
	
	/** The value. */
	protected String value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new belief info.
	 */
	public BeliefInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new goal info.
	 */
	public BeliefInfo(Object id, String kind, String type, String valuetype, String value)
	{
		this.id	= id;
		this.kind	= kind;
		this.type	= type;
		this.valuetype	= valuetype;
		this.value	= value;
	}
	
	//--------- methods ---------
	
	/**
	 *  Return the id.
	 */
	public Object getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 */
	public void setId(Object id)
	{
		this.id = id;
	}

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
	 *  Return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 */
	public void setType(String type)
	{
		this.type = type;
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
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 */
	public void setValue(String value)
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

	/**
	 *  Test if two objects are equal.
	 */
	public boolean	equals(Object obj)
	{
		return obj instanceof BeliefInfo && SUtil.equals(((BeliefInfo)obj).id, id);
	}
	
	/**
	 *  Get the hashcode
	 */
	public int	hashCode()
	{
		return 31+id.hashCode();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create an info object for a belief.
	 */
	public static BeliefInfo	createBeliefInfo(IOAVState state, Object belief, Object scope)
	{
		String	id	= belief.toString();
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
		Object	mbelief	= state.getAttributeValue(belief, OAVBDIRuntimeModel.element_has_model);
		String	kind	= state.getType(belief).equals(OAVBDIRuntimeModel.belief_type) ? "belief" : "beliefset"; 
		String	type	= (String)state.getAttributeValue(mbelief, OAVBDIMetaModel.modelelement_has_name);
		String	valuetype	= SReflect.getInnerClassName((Class)state.getAttributeValue(mbelief, OAVBDIMetaModel.typedelement_has_class));
		String	value	= state.getType(belief).equals(OAVBDIRuntimeModel.belief_type)
			? ""+state.getAttributeValue(belief, OAVBDIRuntimeModel.belief_has_fact)
			: ""+state.getAttributeValues(belief, OAVBDIRuntimeModel.beliefset_has_facts);
		if(scope!=null)
		{
			BDIInterpreter interpreter	= BDIInterpreter.getInterpreter(state);
			List	path	= new ArrayList();
			if(interpreter.findSubcapability(interpreter.getAgent(), scope, path))
			{
				for(int i=path.size()-1; i>=0; i--)
				{
					type	= state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name) + "." + type;
				}
			}
		}
		return new BeliefInfo(id, kind, type, valuetype, value);
	}
}
