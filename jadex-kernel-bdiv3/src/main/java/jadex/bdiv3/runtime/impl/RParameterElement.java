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
		super(melement, agent);
		initParameters();
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	public void initParameters()
	{
		List<MParameter> mparams = ((MParameterElement)getModelElement()).getParameters();
		if(mparams!=null)
		{
			for(MParameter mparam: mparams)
			{
				if(!mparam.isMulti(agent.getClassLoader()))
				{
					addParameter(new RParameter(mparam, getAgent()));
				}
				else
				{
					addParameterSet(new RParameterSet(mparam, getAgent()));
				}
			}
		}
	}
	
	/**
	 *  Add a parameter.
	 *  @param bel The parameter.
	 */
	public void addParameter(RParameter bel)
	{
		if(parameters==null)
			parameters = new HashMap<String, IParameter>();
		parameters.put(bel.getName(), bel);
	}
	
	/**
	 *  Add a parameterset.
	 *  @param bel The parameterset.
	 */
	public void addParameterSet(RParameterSet belset)
	{
		if(parametersets==null)
			parametersets = new HashMap<String, IParameterSet>();
		parametersets.put(belset.getName(), belset);
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
		/** The value. */
		protected Object value;

		/** The publisher. */
		protected EventPublisher publisher;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, IInternalAccess agent)
		{
			super(modelelement, agent);
			String name = modelelement.getName();
			this.value = modelelement.getDefaultValue()==null? null: SJavaParser.parseExpression(modelelement.getDefaultValue(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+name, (MParameter)getModelElement());
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return getModelElement().getName();
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
		/** The value. */
		protected List<Object> values;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, IInternalAccess agent)
		{
			super(modelelement, agent);
			
			List<Object> tmpvalues;
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
			
			String name = modelelement.getName();
			this.values = new ListWrapper<Object>(new ArrayList<Object>(), getAgent(), ChangeEvent.VALUEADDED+"."+name, 
				ChangeEvent.VALUEREMOVED+"."+name, ChangeEvent.VALUECHANGED+"."+name, getModelElement());
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return getModelElement().getName();
		}
		
		/**
		 *  Add a value to a parameter set.
		 *  @param value The new value.
		 */
		public void addValue(Object value)
		{
			values.add(value);
		}

		/**
		 *  Remove a value to a parameter set.
		 *  @param value The new value.
		 */
		public void removeValue(Object value)
		{
			values.remove(value);
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
			values.clear();
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
			return values.contains(value);
		}

		/**
		 *  Get the values of a parameterset.
		 *  @return The values.
		 */
		public Object[]	getValues()
		{
			return values.toArray();
		}

		/**
		 *  Get the number of values currently
		 *  contained in this set.
		 *  @return The values count.
		 */
		public int size()
		{
			return values.size();
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
