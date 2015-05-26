package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameterElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.wrappers.EventPublisher;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.bdiv3x.runtime.IParameterSet;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;
import jadex.javaparser.IMapAccess;
import jadex.javaparser.SJavaParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Base element for elements with parameters such as:
 *  - message event
 *  - internal event
 *  - goal
 *  - plan
 */
public class RParameterElement extends RElement implements IParameterElement, IMapAccess
{
	/** The parameters. */
	protected Map<String, IParameter> parameters;
	
	/** The parameter sets. */
	protected Map<String, IParameterSet> parametersets;
	
	/**
	 *  Create a new parameter element.
	 */
	public RParameterElement(MParameterElement melement, IInternalAccess agent)
	{
		this(melement, agent, null);
	}
	
	/**
	 *  Create a new parameter element.
	 */
	public RParameterElement(MParameterElement melement, IInternalAccess agent, Map<String, Object> vals)
	{
		super(melement, agent);
		initParameters(vals);
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	public void initParameters(Map<String, Object> vals)
	{
		List<MParameter> mparams = ((MParameterElement)getModelElement()).getParameters();
		if(mparams!=null)
		{
			for(MParameter mparam: mparams)
			{
				if(!mparam.isMulti(agent.getClassLoader()))
				{
					if(vals!=null && vals.containsKey(mparam.getName()))
					{
						addParameter(createParameter(mparam, getAgent(), vals.get(mparam.getName())));
					}
					else
					{
						addParameter(createParameter(mparam, getAgent()));
					}
				}
				else
				{
					if(vals!=null && vals.containsKey(mparam.getName()))
					{
						addParameterSet(createParameterSet(mparam, getAgent(), (Object[])vals.get(mparam.getName())));
					}
					else
					{
						addParameterSet(createParameterSet(mparam, getAgent()));
					}
					
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public IParameter createParameter(MParameter modelelement, IInternalAccess agent)
	{
		return new RParameter(modelelement, modelelement.getName(), agent);
	}
	
	/**
	 * 
	 */
	public IParameter createParameter(MParameter modelelement, IInternalAccess agent, Object value)
	{
		return new RParameter(modelelement, modelelement.getName(), agent, value);
	}
	
	/**
	 * 
	 */
	public IParameterSet createParameterSet(MParameter modelelement, IInternalAccess agent)
	{
		return new RParameterSet(modelelement, modelelement.getName(), agent);
	}
	
	/**
	 * 
	 */
	public IParameterSet createParameterSet(MParameter modelelement, IInternalAccess agent, Object[] values)
	{
		return new RParameterSet(modelelement, modelelement.getName(), agent, values);
	}
	
	/**
	 *  Add a parameter.
	 *  @param param The parameter.
	 */
	public void addParameter(IParameter param)
	{
		if(parameters==null)
			parameters = new HashMap<String, IParameter>();
		parameters.put(param.getName(), param);
	}
	
	/**
	 *  Add a parameterset.
	 *  @param paramset The parameterset.
	 */
	public void addParameterSet(IParameterSet paramset)
	{
		if(parametersets==null)
			parametersets = new HashMap<String, IParameterSet>();
		parametersets.put(paramset.getName(), paramset);
	}
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		return parameters==null? new IParameter[0]: parameters.values().toArray(new IParameter[parameters.size()]);
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		return parametersets==null? new IParameterSet[0]: parametersets.values().toArray(new IParameterSet[parametersets.size()]);
	}

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(String name)
	{
		if(parameters==null || !parameters.containsKey(name))
			throw new RuntimeException("Parameter not found: "+name);
		return parameters.get(name);
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		if(parametersets==null || !parametersets.containsKey(name))
			throw new RuntimeException("Parameterset not found: "+name);
		return parametersets.get(name);
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		return parameters==null? false: parameters.containsKey(name);
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		return parametersets==null? false: parametersets.containsKey(name);
	}

	/**
	 *  Get an object from the map.
	 *  @param key The key
	 *  @return The value.
	 */
	public Object get(Object key)
	{
		String name = (String)key;
		Object ret = null;
		if(hasParameter(name))
		{
			ret = getParameter(name).getValue();
		}
		else if(hasParameterSet(name))
		{
			ret = getParameterSet(name).getValues();
		}
		else
		{
			throw new RuntimeException("Unknown parameter/set: "+name);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public static class RParameter extends RElement implements IParameter
	{
		/** The name. */
		protected String name;
		
		/** The value. */
		protected Object value;

		/** The publisher. */
		protected EventPublisher publisher;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name!=null?name: modelelement.getName();
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+name, (MParameter)getModelElement());
			if(modelelement!=null)
				setValue(modelelement.getDefaultValue()==null? null: SJavaParser.parseExpression(modelelement.getDefaultValue(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher()));
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, String name, IInternalAccess agent, Object value)
		{
			super(modelelement, agent);
			this.name = name!=null?name: modelelement.getName();
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+getName(), (MParameter)getModelElement());
			setValue(value);
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 *  Set a value of a parameter.
		 *  @param value The new value.
		 */
		public void setValue(Object value)
		{
			publisher.entryChanged(this.value, value, -1);
			this.value = value;
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object	getValue()
		{
			return value;
		}
	}

	/**
	 * 
	 */
	public static class RParameterSet extends RElement implements IParameterSet
	{
		/** The name. */
		protected String name;
		
		/** The value. */
		protected List<Object> values;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, String name, IInternalAccess agent, Object[] vals)
		{
			super(modelelement, agent);
			
			this.name = name!=null?name: modelelement.getName();
			setValues(new ListWrapper<Object>(vals!=null? SUtil.arrayToList(vals): new ArrayList<Object>(), getAgent(), ChangeEvent.VALUEADDED+"."+getName(), 
				ChangeEvent.VALUEREMOVED+"."+getName(), ChangeEvent.VALUECHANGED+"."+getName(), getModelElement()));
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name!=null?name: modelelement.getName();
			
			List<Object> tmpvalues;
			if(modelelement!=null)
			{
				if(modelelement.getDefaultValue()!=null)
				{
					tmpvalues = (List<Object>)SJavaParser.parseExpression(modelelement.getDefaultValue(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
				}
				else 
				{
					tmpvalues = new ArrayList<Object>();
					if(modelelement.getDefaultValues()!=null)
					{
						for(UnparsedExpression uexp: modelelement.getDefaultValues())
						{
							Object fact = SJavaParser.parseExpression(uexp, agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
							tmpvalues.add(fact);
						}
					}
				}
			}
			else
			{
				tmpvalues = new ArrayList<Object>();
			}
			
			setValues(new ListWrapper<Object>(tmpvalues, getAgent(), ChangeEvent.VALUEADDED+"."+getName(), 
				ChangeEvent.VALUEREMOVED+"."+getName(), ChangeEvent.VALUECHANGED+"."+getName(), getModelElement()));
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 *  Add a value to a parameter set.
		 *  @param value The new value.
		 */
		public void addValue(Object value)
		{
			internalGetValues().add(value);
		}

		/**
		 *  Remove a value to a parameter set.
		 *  @param value The new value.
		 */
		public void removeValue(Object value)
		{
			internalGetValues().remove(value);
		}

		/**
		 *  Add values to a parameter set.
		 */
		public void addValues(Object[] values)
		{
			if(values!=null)
			{
				for(Object value: values)
				{
					addValue(value);
				}
			}
		}

		/**
		 *  Remove all values from a parameter set.
		 */
		public void removeValues()
		{
			internalGetValues().clear();
		}

		/**
		 *  Get a value equal to the given object.
		 *  @param oldval The old value.
		 */
//		public Object	getValue(Object oldval);

		/**
		 *  Test if a value is contained in a parameter.
		 *  @param value The value to test.
		 *  @return True, if value is contained.
		 */
		public boolean containsValue(Object value)
		{
			return internalGetValues().contains(value);
		}

		/**
		 *  Get the values of a parameterset.
		 *  @return The values.
		 */
		public Object[]	getValues()
		{
			return internalGetValues().toArray();
		}

		/**
		 *  Get the number of values currently
		 *  contained in this set.
		 *  @return The values count.
		 */
		public int size()
		{
			return internalGetValues().size();
		}

		/**
		 *  The values to set.
		 *  @param values The values to set
		 */
		public void setValues(List<Object> values)
		{
			this.values = values;
		}
		
		/**
		 * 
		 */
		protected List<Object> internalGetValues()
		{
			return values;
		}
	}
	
//	/**
//	 *  Get the element type (i.e. the name declared in the ADF).
//	 *  @return The element type.
//	 */
//	public String getType()
//	{
//		
//	}
}
