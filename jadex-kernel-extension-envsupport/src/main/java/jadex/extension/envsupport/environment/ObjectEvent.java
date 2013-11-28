package jadex.extension.envsupport.environment;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * An EnvironmentObject event.
 */
public class ObjectEvent implements Serializable
{
	//-------- constants --------

	public static final PrimitiveEventType OBJECT_DESTROYED = new PrimitiveEventType(0);
	public static final PrimitiveEventType OBJECT_REMOVED = new PrimitiveEventType(1);
	
	//-------- attributes --------

	/** Event type. */
	protected Object				type;

	/** The parameters. */
	protected Map					parameters;

	//-------- constructors --------

	/**
	 * Creates a new ObjectEvent
	 * 
	 * @param type event type
	 */
	public ObjectEvent(Object type)
	{
		this.type = type;
		parameters = Collections.synchronizedMap(new HashMap());
	}

	/**
	 * Returns the event type.
	 * 
	 * @return event type
	 */
	public Object getType()
	{
		return type;
	}

	/**
	 * Returns whether the event has parameters.
	 * 
	 * @return true, if the event has parameters
	 */
	public boolean hasParameters()
	{
		return parameters.size() > 0;
	}

	/**
	 * Returns the parameters.
	 * 
	 * @return parameters as Set of Map.Entry
	 */
	public Set getParameters()
	{
		return parameters.entrySet();
	}

	/**
	 * Returns an event parameter.
	 * 
	 * @param parameter parameter name
	 * @return event parameter
	 */
	public Object getParameter(String parameter)
	{
		return parameters.get(parameter);
	}

	/**
	 * Sets an event parameter.
	 * 
	 * @param parameter parameter name
	 * @param obj parameter object
	 */
	public void setParameter(String parameter, Object obj)
	{
		parameters.put(parameter, obj);
	}
	
	/** Primitive Event type
	 */
	public static class PrimitiveEventType
	{
		/** Event ID. */
		private int id_;
		
		/**
		 *  Creates the type.
		 */
		protected PrimitiveEventType(int id)
		{
			id_ = id;
		}
		
		/**
		 *  Get the hash code.
		 */
		public int hashCode()
		{
			return id_;
		}
		
		/**
		 *  Compares the type.
		 */
		public boolean equals(Object obj)
		{
			return ((obj instanceof PrimitiveEventType) &&
					(((PrimitiveEventType) obj).id_ == id_));
		}
	}
}
