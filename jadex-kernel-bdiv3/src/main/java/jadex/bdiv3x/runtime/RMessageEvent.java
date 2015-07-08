package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IValueFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The runtime message event.
 */
public class RMessageEvent extends RProcessableElement implements IMessageEvent 
{
	//-------- attributes --------
	
	/** The message. */
	protected Map<String, Object> msg;
	
	/** The message type. */
	protected MessageType mt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, IInternalAccess agent, MConfigParameterElement config)
	{
		this(modelelement, new HashMap<String, Object>(), SFipa.FIPA_MESSAGE_TYPE, agent, config);
	}
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, Map<String, Object> msg, MessageType mt, IInternalAccess agent, MConfigParameterElement config)
	{
		super(modelelement, null, agent, msg, config);
		this.msg = msg;
		this.mt = mt;
		
		// Must be done after msg has been assigned :-(
		super.initParameters(null, wrapFetcher(RBeliefbase.getFetcher(agent, modelelement)), config);
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	@Override
	public void initParameters(Map<String, Object> vals, IValueFetcher fetcher, MConfigParameterElement config)
	{
		// do nothing in super constructor init 
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$event";
	}
	
	/**
	 * 
	 */
	@Override
	public IParameter createParameter(MParameter modelelement, IInternalAccess agent, UnparsedExpression inival, IValueFetcher fetcher)
	{
		return new RParam(modelelement, modelelement.getName(), agent, inival, fetcher, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameterSet createParameterSet(MParameter modelelement, IInternalAccess agent, List<UnparsedExpression> inivals, IValueFetcher fetcher)
	{
		return new RParamSet(modelelement, modelelement.getName(), agent, inivals, fetcher, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameter createParameter(MParameter modelelement, IInternalAccess agent, Object value)
	{
		return new RParam(modelelement, modelelement.getName(), agent, value, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameterSet createParameterSet(MParameter modelelement, IInternalAccess agent, Object[] values)
	{
		return new RParamSet(modelelement, modelelement.getName(), agent, values, getModelElement().getName());
	}
	
	//-------- methods --------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[] getParameters()
	{
		List<IParameter> ret = new ArrayList<IParameter>();
		for(String name: mt.getParameterNames())
		{
			ret.add(getParameter(name));
		}
		return ret.toArray(new IParameter[ret.size()]);
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[] getParameterSets()
	{
		List<IParameterSet> ret = new ArrayList<IParameterSet>();
		for(String name: mt.getParameterSetNames())
		{
			ret.add(getParameterSet(name));
		}
		return ret.toArray(new IParameterSet[ret.size()]);
	}

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(String name)
	{
		if(mt.getParameter(name)==null)
			throw new RuntimeException("Unknown parameter: "+name);
		
		IParameter param;
		if(!super.hasParameter(name))
		{
			MParameter mp = getMMessageEvent().getParameter(name);
			param = new RParam(mp, name, getAgent(), null, RBeliefbase.getFetcher(getAgent(), getModelElement()), getModelElement().getName());
			addParameter(param);
		}
		else
		{
			param = super.getParameter(name);
		}
		
		return param;
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		if(mt.getParameterSet(name)==null)
			throw new RuntimeException("Unknown parameter set: "+name);
		
		if(!super.hasParameterSet(name))
		{
			addParameterSet(new RParamSet(null, name, getAgent(), null, RBeliefbase.getFetcher(getAgent(), getModelElement()), getModelElement().getName()));
		}

		return super.getParameterSet(name);
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		return mt.getParameter(name)!=null;
	}
	
	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		return mt.getParameterSet(name)!=null;
	}

	/**
	 *  Get the native (platform specific) message object.
	 *  @return The native message.
	 */
	public Object getMessage()
	{
		return msg;
	}

	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public MessageType getMessageType()
	{
		return mt;
	}
	
//	/**
//	 *  Get the element type (i.e. the name declared in the ADF).
//	 *  @return The element type.
//	 */
//	public String getType()
//	{
//		return getModelElement().getName();
//	}
	
	/**
	 * 
	 */
	public MMessageEvent getMMessageEvent()
	{
		return (MMessageEvent)getModelElement();
	}
	
	/**
	 * 
	 */
	public class RParam extends RParameter
	{
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParam(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival, IValueFetcher fetcher,  String pename)
		{
			super(modelelement, name, agent, inival, fetcher, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParam(MParameter modelelement, String name, IInternalAccess agent, Object value, String pename)
		{
			super(modelelement, name, agent, value, pename);
		}
		
		/**
		 *  Set a value of a parameter.
		 *  @param value The new value.
		 */
		public void setValue(Object value)
		{
			publisher.entryChanged(msg.get(getName()), value, -1);
			msg.put(getName(), value);
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object	getValue()
		{
			return msg.get(getName());
		}
	}
	
	/**
	 * 
	 */
	public class RParamSet extends RParameterSet
	{
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals, IValueFetcher fetcher, String pename)
		{
			super(modelelement, name, agent, inivals, fetcher, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, Object[] values, String pename)
		{
			super(modelelement, name, agent, values, pename);
		}
		
		/**
		 *  The values to set.
		 *  @param values The values to set
		 */
		public void setValues(List<Object> values)
		{
			msg.put(getName(), values);
		}
		
		/**
		 * 
		 */
		protected List<Object> internalGetValues()
		{
			List<Object> vals = (List<Object>)msg.get(getName());
			if(vals==null)
			{
				vals = new ArrayList<Object>();
				msg.put(getName(), vals);
			}
			return vals;
		}
	}
	
//	/**
//	 * 
//	 */
//	public class RParameter extends RElement implements IParameter
//	{
//		/** The name. */
//		protected String name;
//
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParameter(MElement modelelement, String name, IInternalAccess agent)
//		{
//			super(modelelement, agent);
//			this.name = name;
//		}
//
//		/**
//		 *  Get the name.
//		 *  @return The name
//		 */
//		public String getName()
//		{
//			return name;
//		}
//		
//		/**
//		 *  Set a value of a parameter.
//		 *  @param value The new value.
//		 */
//		public void setValue(Object value)
//		{
//			msg.put(name, value);
//		}
//
//		/**
//		 *  Get the value of a parameter.
//		 *  @return The value.
//		 */
//		public Object	getValue()
//		{
//			return msg.get(name);
//		}
//	}
//	
//	/**
//	 * 
//	 */
//	public class RParameterSet extends RElement implements IParameterSet
//	{
//		/** The name. */
//		protected String name;
//		
//		/**
//		 *  Create a new parameter.
//		 *  @param modelelement The model element.
//		 *  @param name The name.
//		 */
//		public RParameterSet(MElement modelelement, String name, IInternalAccess agent)
//		{
//			super(modelelement, agent);
//			this.name = name;
//		}
//
//		/**
//		 *  Get the name.
//		 *  @return The name
//		 */
//		public String getName()
//		{
//			return name;
//		}
//		
//		/**
//		 *  Add a value to a parameter set.
//		 *  @param value The new value.
//		 */
//		public void addValue(Object value)
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			if(values==null)
//			{
//				values = new ArrayList<Object>();
//				msg.put(name, values);
//			}
//			values.add(value);
//		}
//
//		/**
//		 *  Remove a value to a parameter set.
//		 *  @param value The new value.
//		 */
//		public void removeValue(Object value)
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			if(values!=null)
//				values.remove(value);
//		}
//
//		/**
//		 *  Add values to a parameter set.
//		 */
//		public void addValues(Object[] values)
//		{
//			if(values!=null)
//			{
//				for(Object value: values)
//				{
//					addValue(value);
//				}
//			}
//		}
//
//		/**
//		 *  Remove all values from a parameter set.
//		 */
//		public void removeValues()
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			if(values!=null)
//				values.clear();
//		}
//
//		/**
//		 *  Get a value equal to the given object.
//		 *  @param oldval The old value.
//		 */
////		public Object	getValue(Object oldval);
//
//		/**
//		 *  Test if a value is contained in a parameter.
//		 *  @param value The value to test.
//		 *  @return True, if value is contained.
//		 */
//		public boolean containsValue(Object value)
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			return values==null? false: values.contains(value);
//		}
//
//		/**
//		 *  Get the values of a parameterset.
//		 *  @return The values.
//		 */
//		public Object[]	getValues()
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			return values==null? new Object[0]: values.toArray();
//		}
//
//		/**
//		 *  Get the number of values currently
//		 *  contained in this set.
//		 *  @return The values count.
//		 */
//		public int size()
//		{
//			Collection<Object> values = (Collection<Object>)msg.get(name);
//			return values==null? 0: values.size();
//		}
//	}
}
