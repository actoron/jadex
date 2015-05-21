package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameterElement;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.bdiv3x.runtime.IParameterSet;
import jadex.bdiv3x.runtime.RBeliefbase.RBelief;
import jadex.bdiv3x.runtime.RBeliefbase.RBeliefSet;
import jadex.bridge.IInternalAccess;
import jadex.javaparser.IMapAccess;

import java.util.ArrayList;
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
	 *  Create a new beliefbase.
	 */
	public RParameterElement(MParameterElement melement, IInternalAccess agent)
	{
		super(melement, agent);
	}
	
//	/**
//	 *  
//	 */
//	public void init()
//	{
//		List<MParameter> mbels = ((MParameterElement)getModelElement()).getParameters();
//		if(mbels!=null)
//		{
//			for(MParameter mbel: mbels)
//			{
//				if(!mbel.isMulti(agent.getClassLoader()))
//				{
//					addParameter(new RBelief(mbel, getAgent()));
//				}
//				else
//				{
//					addParameterSet(new RBeliefSet(mbel, getAgent()));
//				}
//			}
//		}
//	}
	
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

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MElement modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name;
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
		public RParameterSet(MElement modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name;
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
			if(values==null)
				values = new ArrayList<Object>();
			values.add(value);
		}

		/**
		 *  Remove a value to a parameter set.
		 *  @param value The new value.
		 */
		public void removeValue(Object value)
		{
			if(values!=null)
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
			if(values!=null)
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
			return values==null? false: values.contains(value);
		}

		/**
		 *  Get the values of a parameterset.
		 *  @return The values.
		 */
		public Object[]	getValues()
		{
			return values==null? new Object[0]: values.toArray();
		}

		/**
		 *  Get the number of values currently
		 *  contained in this set.
		 *  @return The values count.
		 */
		public int size()
		{
			return values==null? 0: values.size();
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
