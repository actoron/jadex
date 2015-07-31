package jadex.bdiv3.runtime.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3.model.MParameterElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.wrappers.EventPublisher;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.bdiv3x.runtime.IParameterSet;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.javaparser.IMapAccess;
import jadex.javaparser.SJavaParser;

/**
 *  Base element for elements with parameters such as:
 *  - message event
 *  - internal event
 *  - goal
 *  - plan
 */
public abstract class RParameterElement extends RElement implements IParameterElement, IMapAccess
{
	/** The parameters. */
	protected Map<String, IParameter> parameters;
	
	/** The parameter sets. */
	protected Map<String, IParameterSet> parametersets;
	
	/**
	 *  Create a new parameter element.
	 */
	public RParameterElement(MParameterElement melement, IInternalAccess agent, Map<String, Object> vals, IValueFetcher fetcher, MConfigParameterElement config)
	{
		super(melement, agent);
		initParameters(vals, wrapFetcher(fetcher), config);
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	public void initParameters(Map<String, Object> vals, IValueFetcher fetcher, MConfigParameterElement config)
	{
		List<MParameter> mparams = getModelElement()!=null ? ((MParameterElement)getModelElement()).getParameters() : null;
		if(mparams!=null)
		{
			for(MParameter mparam: mparams)
			{
				if(!mparam.isMulti(agent.getClassLoader()))
				{
					if(vals!=null && vals.containsKey(mparam.getName()) && MParameter.EvaluationMode.STATIC.equals(mparam.getEvaluationMode()))
					{
						addParameter(createParameter(mparam, mparam.getName(), getAgent(), vals.get(mparam.getName())));
					}
					else
					{
						addParameter(createParameter(mparam, mparam.getName(), getAgent(), config!=null ? config.getParameter(mparam.getName()) : null, fetcher));
					}
				}
				else
				{
					if(vals!=null && vals.containsKey(mparam.getName()) && MParameter.EvaluationMode.STATIC.equals(mparam.getEvaluationMode()))
					{
						addParameterSet(createParameterSet(mparam, mparam.getName(), getAgent(), vals.get(mparam.getName())));
					}
					else
					{
						addParameterSet(createParameterSet(mparam, mparam.getName(), getAgent(), config!=null ? config.getParameters(mparam.getName()) : null, fetcher));
					}
				}
			}
		}
	}
	
	/**
	 *  Wrap the fetcher to include the element itself.
	 */
	public IValueFetcher wrapFetcher(final IValueFetcher fetcher)
	{
		return new IValueFetcher()
		{
			public Object fetchValue(String name)
			{
				Object ret = null;
				if(getFetcherName().equals(name))
				{
					ret = RParameterElement.this;
				}
				else if(fetcher!=null)
				{
					ret = fetcher.fetchValue(name);
				}
				else
				{
					throw new RuntimeException("Value not found: "+name);
				}
				return ret;
			}
		};
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public abstract String getFetcherName();
	
	/**
	 * 
	 */
	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival, IValueFetcher fetcher)
	{
		return new RParameter(modelelement, name, agent, inival, fetcher, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, Object value)
	{
		return new RParameter(modelelement, name, agent, value, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals, IValueFetcher fetcher)
	{
		return new RParameterSet(modelelement, name, agent, inivals, fetcher, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, Object values)
	{
		return new RParameterSet(modelelement, name, agent, values, getModelElement().getName());
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

		/** The initial value expression (only for push evaluation mode). */
		protected UnparsedExpression inival;
		
		/** The publisher. */
		protected EventPublisher publisher;
		
		/** The fetcher. */
		protected IValueFetcher fetcher;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, String name, IInternalAccess agent, String pename)
		{
			super(modelelement, agent);
			this.name = name!=null? name: modelelement.getName();
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+pename+"."+getName(), (MParameter)getModelElement());
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival, IValueFetcher fetcher, String pename)
		{
			super(modelelement, agent);
			this.name = name!=null? name: modelelement.getName();
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+pename+"."+getName(), (MParameter)getModelElement());
			this.fetcher	= fetcher;
			
			if(modelelement!=null && modelelement.getEvaluationMode()==EvaluationMode.PULL)
			{
				this.inival	= inival;
			}
			
			setValue(evaluateValue(inival));
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MParameter modelelement, String name, IInternalAccess agent, Object value, String pename)
		{
			super(modelelement, agent);
			this.name = name!=null? name: modelelement.getName();
			// RParameterElement.this.getName()
			this.publisher = new EventPublisher(agent, ChangeEvent.VALUECHANGED+"."+pename+"."+getName(), (MParameter)getModelElement());
			
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
			if(value!=null && getModelElement()!=null)
			{
				Class<?>	clazz	= ((MParameter)getModelElement()).getClazz().getType(getAgent().getClassLoader(), getAgent().getModel().getAllImports());
				if(!SReflect.isSupertype(clazz, value.getClass()))
				{
					throw new IllegalArgumentException("Incompatible value for parameter "+getName()+": "+value);
				}
				value	= SReflect.convertWrappedValue(value, clazz);
			}
			
			Object oldvalue = this.value;
			this.value = value;
			publisher.entryChanged(oldvalue, value, -1);
		}
		
		/**
		 *  Update the dynamic value for push or update rate implementation.
		 */
		public void	updateDynamicValue()
		{
			setValue(evaluateValue(inival));
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object	getValue()
		{
			return ((MParameter)getModelElement()).getEvaluationMode()==EvaluationMode.PULL ? evaluateValue(inival) : value;
		}
		
		/**
		 *  Evaluate the (initial or default or pull) value.
		 */
		protected Object evaluateValue(UnparsedExpression inival)
		{
			UnparsedExpression uexp = inival!=null ? inival : getModelElement()!=null ? ((MParameter)getModelElement()).getDefaultValue() : null;
			return uexp!=null ? SJavaParser.parseExpression(uexp, getAgent().getModel().getAllImports(), getAgent().getClassLoader()).getValue(fetcher) : null;
		}
		
		/**
		 *  Test if this parameter has a default value.
		 */
		protected boolean hasDefaultValue()
		{
			return getModelElement()!=null && ((MParameter)getModelElement()).getDefaultValue()!=null;
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
		
		/** The initial values expression(s) (only for push evaluation mode). */
		protected List<UnparsedExpression> inivals;
		
		/** The fetcher. */
		protected IValueFetcher fetcher;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, String name, IInternalAccess agent, String pename)
		{
			super(modelelement, agent);
			this.name = name!=null?name: modelelement.getName();
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, String name, IInternalAccess agent, Object vals, String pename)
		{
			super(modelelement, agent);
			
			this.name = name!=null?name: modelelement.getName();
			
			List<Object> inivals = new ArrayList<Object>();
			if(vals!=null)
			{
				Iterator<?>	it	= SReflect.getIterator(vals);
				while(it.hasNext())
				{
					inivals.add(it.next());
				}
			}
			
			setValues(new ListWrapper<Object>(vals!=null? inivals: new ArrayList<Object>(), getAgent(), ChangeEvent.VALUEADDED+"."+pename+"."+getName(), 
				ChangeEvent.VALUEREMOVED+"."+pename+"."+getName(), ChangeEvent.VALUECHANGED+"."+pename+"."+getName(), getModelElement()));
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals, IValueFetcher fetcher, String pename)
		{
			super(modelelement, agent);
			this.name = name!=null?name: modelelement.getName();
			this.fetcher = fetcher;
			if(modelelement!=null && modelelement.getEvaluationMode()==EvaluationMode.PULL)
			{
				this.inivals	= inivals;
			}
			
			setValues(new ListWrapper<Object>(evaluateValues(inivals), getAgent(), ChangeEvent.VALUEADDED+"."+pename+"."+getName(), 
				ChangeEvent.VALUEREMOVED+"."+pename+"."+getName(), ChangeEvent.VALUECHANGED+"."+pename+"."+getName(), getModelElement()));
		}

		/**
		 *  Evaluate the default values.
		 */
		protected List<Object> evaluateValues(List<UnparsedExpression> inivals)
		{
			MParameter mparam = (MParameter)getModelElement();
			List<Object> tmpvalues = new ArrayList<Object>();
			if(inivals==null && mparam!=null)
			{
				if(mparam.getDefaultValue()!=null)
				{
					inivals	= Collections.singletonList(mparam.getDefaultValue());
				}
				else
				{
					inivals	= mparam.getDefaultValues();
				}
			}
			
			if(inivals!=null)
			{
				if(inivals.size()==1)
				{
					Object	tmpvalue	= SJavaParser.parseExpression(inivals.get(0), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(fetcher);
					if(tmpvalue!=null && getClazz()!=null && SReflect.isSupertype(getClazz(), tmpvalue.getClass()))
					{
						tmpvalues.add(tmpvalue);
					}
					else
					{
						for(Object tmp: SReflect.getIterable(tmpvalue))
						{
							tmpvalues.add(tmp);
						}
					}
				}
				else 
				{
					for(UnparsedExpression uexp: inivals)
					{
						tmpvalues.add(SJavaParser.parseExpression(uexp, agent.getModel().getAllImports(), agent.getClassLoader()).getValue(fetcher));
					}
				}
			}
			
			return tmpvalues;
		}
		
		/**
		 *  Get the class of a value.
		 */
		protected Class<?> getClazz()
		{
			MParameter mparam = (MParameter)getModelElement();
			return mparam.getClazz().getType(agent.getClassLoader(), agent.getModel().getAllImports());
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
			if(value!=null && getModelElement()!=null)
			{
				Class<?>	clazz	= ((MParameter)getModelElement()).getClazz().getType(getAgent().getClassLoader(), getAgent().getModel().getAllImports());
				if(!SReflect.isSupertype(clazz, value.getClass()))
				{
					throw new IllegalArgumentException("Incompatible value for parameter set "+getName()+": "+value);
				}
				value	= SReflect.convertWrappedValue(value, clazz);
			}
			
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
			return getValues(((MParameter)getModelElement()).getType(getAgent().getClassLoader()));
		}
		
		/**
		 *  Update the dynamic values for push or update rate implementation.
		 */
		public void	updateDynamicValues()
		{
			setValues(evaluateValues(inivals));
		}

		/**
		 *  Get the values of a parameterset.
		 *  @return The values.
		 */
		protected Object[]	getValues(Class<?> type)
		{
			Object ret;
			List<Object> vals = internalGetValues();
			
			int size = vals==null? 0: vals.size();
			ret = type!=null? ret = Array.newInstance(SReflect.getWrappedType(type), size): new Object[size];
			
			if(vals!=null)
				System.arraycopy(vals.toArray(new Object[vals.size()]), 0, ret, 0, vals.size());
			
			return (Object[])ret;
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
		// Internal method, overridden for message event.
		protected void setValues(List<Object> values)
		{
			this.values = values;
		}
		
		/**
		 * 
		 */
		protected List<Object> internalGetValues()
		{
			// In case of push the last saved/evaluated value is returned
			return MParameter.EvaluationMode.PULL.equals(((MParameter)getModelElement()).getEvaluationMode())? evaluateValues(inivals): values;
		}
	}
	
	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String getType()
	{
		return getModelElement().getElementName();
	}
}
