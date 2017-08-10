package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;

/**
 *  The runtime message event.
 */
public class RMessageEvent<T> extends RProcessableElement implements IMessageEvent<T> 
{
	//-------- attributes --------
	
	/** The message. */
	protected T msg;
	
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, IInternalAccess agent, MConfigParameterElement config)
	{
//		this(modelelement, new HashMap<String, Object>(), SFipa.FIPA_MESSAGE_TYPE, agent, config);
		super(modelelement, null, agent, null, config);
//		this.msg = new HashMap<String, Object>();
//		this.mt = SFipa.FIPA_MESSAGE_TYPE;
//		
//		// Must be done after msg has been assigned :-(
//		super.initParameters(null, config);
//		
//		// In case of messages there can be parameters only in the config, not in the model due to underlying message type definition
//		if(config!=null && config.getParameters()!=null)
//		{
//			for(Map.Entry<String, List<UnparsedExpression>> entry: config.getParameters().entrySet())
//			{
//				if(!msg.containsKey(entry.getKey()))
//				{
//					ParameterSpecification ps = mt.getParameter(entry.getKey());
//					if(!ps.isSet())
//					{
//						addParameter(createParameter(null, entry.getKey(), getAgent(), config.getParameter(entry.getKey())));
//					}
//					else
//					{
//						addParameterSet(createParameterSet(null, entry.getKey(), getAgent(), config.getParameters(entry.getKey())));
//					}
//				}
//			}
//		}
	}
	
	/**
	 *  Create a new runtime element.
	 *  
	 *  Constructor Without parameter init for received messages.
	 */
	public RMessageEvent(MMessageEvent modelelement, T msg, IInternalAccess agent)
	{
		super(modelelement, null, agent, null, null);
		this.msg = msg;
//		this.mt = mt;
//		
//		// Tricky, must do init for default values if NOT present in the map
//		// Must be done after msg has been assigned :-(
//		super.initParameters(msg, null);
	}
	
//	/**
//	 *  Create the parameters from model spec.
//	 */
//	@Override
//	public void initParameters(Map<String, Object> vals, MConfigParameterElement config)
//	{
//		// do nothing in super constructor init 
//	}
//	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$event";
	}
//	
//	/**
//	 * 
//	 */
//	@Override
//	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival)
//	{
//		return new RParam(modelelement, name, agent, inival, getModelElement().getName());
//	}
//	
//	/**
//	 * 
//	 */
//	@Override
//	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals)
//	{
//		return new RParamSet(modelelement, name, agent, inivals, getModelElement().getName());
//	}
//	
//	/**
//	 * 
//	 */
//	@Override
//	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, Object value)
//	{
//		return new RParam(modelelement, name, agent, value, getModelElement().getName());
//	}
//	
//	/**
//	 * 
//	 */
//	@Override
//	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, Object values)
//	{
//		return new RParamSet(modelelement, name, agent, values, getModelElement().getName());
//	}
//	
//	//-------- methods --------
//	
//	/**
//	 *  Get all parameters.
//	 *  @return All parameters.
//	 */
//	public IParameter[] getParameters()
//	{
//		List<IParameter> ret = new ArrayList<IParameter>();
//		for(String name: mt.getParameterNames())
//		{
//			ret.add(getParameter(name));
//		}
//		return ret.toArray(new IParameter[ret.size()]);
//	}
//
//	/**
//	 *  Get all parameter sets.
//	 *  @return All parameter sets.
//	 */
//	public IParameterSet[] getParameterSets()
//	{
//		List<IParameterSet> ret = new ArrayList<IParameterSet>();
//		for(String name: mt.getParameterSetNames())
//		{
//			ret.add(getParameterSet(name));
//		}
//		return ret.toArray(new IParameterSet[ret.size()]);
//	}
//
//	/**
//	 *  Get the parameter element.
//	 *  @param name The name.
//	 *  @return The param.
//	 */
//	public IParameter getParameter(String name)
//	{
//		if(mt.getParameter(name)==null)
//			throw new RuntimeException("Unknown parameter: "+name);
//		
//		IParameter param;
//		if(!super.hasParameter(name))
//		{
//			MParameter mp = getMMessageEvent().getParameter(name);
//			// construct param without setting default values!
//			param = new RParam(mp, name, getAgent(), getModelElement().getName());
//			addParameter(param);
//		}
//		else
//		{
//			param = super.getParameter(name);
//		}
//		
//		return param;
//	}
//
//	/**
//	 *  Get the parameter set element.
// 	 *  @param name The name.
//	 *  @return The param set.
//	 */
//	public IParameterSet getParameterSet(String name)
//	{
//		if(mt.getParameterSet(name)==null)
//			throw new RuntimeException("Unknown parameter set: "+name);
//		
//		if(!super.hasParameterSet(name))
//		{
//			// construct param without setting default values!
//			addParameterSet(new RParamSet(null, name, getAgent(), null, getModelElement().getName()));
//		}
//
//		return super.getParameterSet(name);
//	}
//
//	/**
//	 *  Has the element a parameter element.
//	 *  @param name The name.
//	 *  @return True, if it has the parameter.
//	 */
//	public boolean hasParameter(String name)
//	{
//		return mt.getParameter(name)!=null;
//	}
//	
//	/**
//	 *  Has the element a parameter set element.
//	 *  @param name The name.
//	 *  @return True, if it has the parameter set.
//	 */
//	public boolean hasParameterSet(String name)
//	{
//		return mt.getParameterSet(name)!=null;
//	}
//
	/**
	 *  Get the native (platform specific) message object.
	 *  @return The native message.
	 */
	public T getMessage()
	{
		return msg;
	}

//	
////	/**
////	 *  Get the element type (i.e. the name declared in the ADF).
////	 *  @return The element type.
////	 */
////	public String getType()
////	{
////		return getModelElement().getName();
////	}
//	
//	/**
//	 * 
//	 */
//	public MMessageEvent getMMessageEvent()
//	{
//		return (MMessageEvent)getModelElement();
//	}
//	
//	/**
//	 * 
//	 */
//	public class RParam extends RParameter
//	{
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParam(MParameter modelelement, String name, IInternalAccess agent, String pename)
//		{
//			super(modelelement, name, agent, pename);
//		}
//		
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParam(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival, String pename)
//		{
//			super(modelelement, name, agent, inival, pename);
//		}
//		
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParam(MParameter modelelement, String name, IInternalAccess agent, Object value, String pename)
//		{
//			super(modelelement, name, agent, value, pename);
//		}
//		
//		/**
//		 *  Set a value of a parameter.
//		 *  @param value The new value.
//		 */
//		public void setValue(Object value)
//		{
//			publisher.entryChanged(msg.get(getName()), value, -1);
//			msg.put(getName(), value);
//		}
//
//		/**
//		 *  Get the value of a parameter.
//		 *  @return The value.
//		 */
//		public Object	getValue()
//		{
//			return msg.get(getName());
//		}
//	}
//	
//	/**
//	 * 
//	 */
//	public class RParamSet extends RParameterSet
//	{
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, String pename)
//		{
//			super(modelelement, name, agent, pename);
//		}
//		
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals, String pename)
//		{
//			super(modelelement, name, agent, inivals, pename);
//		}
//		
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, Object values, String pename)
//		{
//			super(modelelement, name, agent, values, pename);
//		}
//		
//		/**
//		 *  Get the class of a value.
//		 */
//		@Override
//		protected Class<?> getClazz()
//		{
//			Class<?> ret = getModelElement()!=null? super.getClazz(): null;
//			if(ret==null)
//				ret = getMessageType().getParameter(getName()).getClazz();
//			return ret;
//		}
//		
//		/**
//		 *  The values to set.
//		 *  @param values The values to set
//		 */
//		protected void setValues(List<Object> values)
//		{
//			testWriteOK((MParameter)getModelElement());
//			
//			msg.put(getName(), values);
//		}
//		
//		/**
//		 *  Adapt to message type for implicit parameters.
//		 */
//		@Override
//		public Object[] getValues()
//		{
//			Object[]	ret;
//			if(getModelElement()==null)
//			{
//				ret	= super.getValues(mt.getParameterSet(getName()).getClazz());
//			}
//			else
//			{
//				ret	= super.getValues();
//			}
//			
//			return ret;
//		}
//		
//		/**
//		 * 
//		 */
//		protected List<Object> internalGetValues()
//		{
//			List<Object> vals = (List<Object>)msg.get(getName());
//			if(vals==null)
//			{
//				vals = new ArrayList<Object>();
//				msg.put(getName(), vals);
//			}
//			return vals;
//		}
//	}
//	
//	/** 
//	 *  Get the string represntation.
//	 */
//	public String toString()
//	{
////		return "RGoal(lifecyclestate=" + lifecyclestate + ", processingstate="
////			+ processingstate + ", state=" + state + ", id=" + id + ")";
//		return "RMessageEvent: "+msg;
//	}
//	
////	/**
////	 * 
////	 */
////	public class RParameter extends RElement implements IParameter
////	{
////		/** The name. */
////		protected String name;
////
////		/**
////		 *  Create a new parameter.
////		 *  @param modelelement The model element.
////		 *  @param name The name.
////		 */
////		public RParameter(MElement modelelement, String name, IInternalAccess agent)
////		{
////			super(modelelement, agent);
////			this.name = name;
////		}
////
////		/**
////		 *  Get the name.
////		 *  @return The name
////		 */
////		public String getName()
////		{
////			return name;
////		}
////		
////		/**
////		 *  Set a value of a parameter.
////		 *  @param value The new value.
////		 */
////		public void setValue(Object value)
////		{
////			msg.put(name, value);
////		}
////
////		/**
////		 *  Get the value of a parameter.
////		 *  @return The value.
////		 */
////		public Object	getValue()
////		{
////			return msg.get(name);
////		}
////	}
////	
////	/**
////	 * 
////	 */
////	public class RParameterSet extends RElement implements IParameterSet
////	{
////		/** The name. */
////		protected String name;
////		
////		/**
////		 *  Create a new parameter.
////		 *  @param modelelement The model element.
////		 *  @param name The name.
////		 */
////		public RParameterSet(MElement modelelement, String name, IInternalAccess agent)
////		{
////			super(modelelement, agent);
////			this.name = name;
////		}
////
////		/**
////		 *  Get the name.
////		 *  @return The name
////		 */
////		public String getName()
////		{
////			return name;
////		}
////		
////		/**
////		 *  Add a value to a parameter set.
////		 *  @param value The new value.
////		 */
////		public void addValue(Object value)
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			if(values==null)
////			{
////				values = new ArrayList<Object>();
////				msg.put(name, values);
////			}
////			values.add(value);
////		}
////
////		/**
////		 *  Remove a value to a parameter set.
////		 *  @param value The new value.
////		 */
////		public void removeValue(Object value)
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			if(values!=null)
////				values.remove(value);
////		}
////
////		/**
////		 *  Add values to a parameter set.
////		 */
////		public void addValues(Object[] values)
////		{
////			if(values!=null)
////			{
////				for(Object value: values)
////				{
////					addValue(value);
////				}
////			}
////		}
////
////		/**
////		 *  Remove all values from a parameter set.
////		 */
////		public void removeValues()
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			if(values!=null)
////				values.clear();
////		}
////
////		/**
////		 *  Get a value equal to the given object.
////		 *  @param oldval The old value.
////		 */
//////		public Object	getValue(Object oldval);
////
////		/**
////		 *  Test if a value is contained in a parameter.
////		 *  @param value The value to test.
////		 *  @return True, if value is contained.
////		 */
////		public boolean containsValue(Object value)
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			return values==null? false: values.contains(value);
////		}
////
////		/**
////		 *  Get the values of a parameterset.
////		 *  @return The values.
////		 */
////		public Object[]	getValues()
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			return values==null? new Object[0]: values.toArray();
////		}
////
////		/**
////		 *  Get the number of values currently
////		 *  contained in this set.
////		 *  @return The values count.
////		 */
////		public int size()
////		{
////			Collection<Object> values = (Collection<Object>)msg.get(name);
////			return values==null? 0: values.size();
////		}
////	}
}
