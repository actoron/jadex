package jadex.bdiv3.runtime.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  Parameter info.
 */
public class ParameterInfo 
{
	/** The name. */
	protected String name;
	
	/** The type. */
	protected String type;
	
	/** The value as string. */
	protected String value;
	
	/** Other parameter properties. */
	protected Map<String, String> props;

	/**
	 *  Get the name.
	 *  @return the name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name the name to set
	 */
	public ParameterInfo setName(String name) 
	{
		this.name = name;
		return this;
	}

	/**
	 *  Get the type.
	 *  @return the type
	 */
	public String getType() 
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type the type to set
	 */
	public ParameterInfo setType(String type) 
	{
		this.type = type;
		return this;
	}

	/**
	 *  Get the value.
	 *  @return the value
	 */
	public String getValue() 
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value the value to set
	 */
	public ParameterInfo setValue(String value) 
	{
		this.value = value;
		return this;
	}

	/**
	 * @return the props
	 */
	public Map<String, String> getProps() 
	{
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public ParameterInfo setProps(Map<String, String> props) 
	{
		this.props = props;
		return this;
	}
	
	/**
	 *  Add a property.
	 */
	public ParameterInfo addProp(String name, String val)
	{
		if(props==null)
			props = new HashMap<String, String>();
		props.put(name, val);
		return this;
	}
	
	/**
	 *  Create a parameter info for a rparameter.
	 *  @param p The parameter.
	 *  @return The parameter info.
	 */
	public static ParameterInfo createParameterInfo(IParameter p, ClassLoader cl)
	{
		MParameter mp = ((MParameter)p.getModelElement());
		ClassInfo ci = new ClassInfo(mp.getType(cl));
		ParameterInfo pi = new ParameterInfo()
				.setName(p.getName())
				.setType(ci.getClassNameOnly())
				.setValue(""+p.getValue());
		pi.addProp("evaluationmode", mp.getEvaluationMode().getString());
		if(mp.getDefaultValue()!=null)
			pi.addProp("defaultvalue", mp.getDefaultValue().getValue());
		if(mp.getDefaultValues()!=null)
		{
			StringBuilder sb = new StringBuilder();
			List<UnparsedExpression> lups = mp.getDefaultValues();
			if(lups.size()>0)
			{
				for(int i=0; i<lups.size(); i++)
				{
					sb.append(lups.get(i));
					if(i+1<lups.size())
						sb.append(", ");
				}
			}
			pi.addProp("defaultvalues", sb.toString());
		}
		if(mp.isOptional())
			pi.addProp("optional", ""+mp.isOptional());
		if(mp.getBindingOptions()!=null)
			pi.addProp("bindingoptions", mp.getBindingOptions().getValue());
		pi.addProp("multi", ""+mp.isMulti(cl));
		if(mp.getDescription()!=null)
			pi.addProp("description", mp.getDescription());
		if(mp.getUpdateRate()!=null)
			pi.addProp("updaterate", ""+mp.getUpdateRate());
		if(!mp.getEvaluationMode().equals(EvaluationMode.STATIC))
			pi.addProp("evaluationmode", mp.getEvaluationMode().getString());
		pi.addProp("direction", mp.getDirection().getString());
		
		return pi;
	}
}
