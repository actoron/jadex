package jadex.bridge.service.types.cms;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SReflect;

/**
 *  Base change event. If used w/o subclass denotes change in description. 
 */
public class CMSStatusEvent
{
	/** The component description. */
	protected IComponentDescription desc;
	
	/**
	 *  Create a new CMSStatusEvent.
	 */
	public CMSStatusEvent()
	{
	}
	
	/**
	 *  Create a new CMSStatusEvent.
	 */
	public CMSStatusEvent(IComponentDescription desc)
	{
		this.desc = desc;
	}

	/**
	 *  Get the componentIdentifier.
	 *  @return The componentIdentifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return desc.getName();
	}

	/**
	 *  Get the component description.
	 *  @return The description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return desc;
	}

	/**
	 *  Set the component description.
	 *  @param desc The component description to set.
	 */
	public void setComponentDescription(IComponentDescription desc)
	{
		this.desc = desc;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"["+desc+"]";
	}
	
	/**
	 *  Status event for a newly created component.
	 */
	public static class CMSCreatedEvent extends CMSStatusEvent
	{
		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSCreatedEvent()
		{
		}
		
		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSCreatedEvent(IComponentDescription desc)
		{
			super(desc);
		}
	}
	
	/**
	 *  Status event for an intermediate result of a component.
	 */
	public static class CMSIntermediateResultEvent extends CMSStatusEvent
	{
		/** The name of the result. */
		protected String name;
		
		/** The value of the result. */
		protected Object value;

		/**
		 *  Create a new CMSIntermediateResultEvent. 
		 */
		public CMSIntermediateResultEvent()
		{
		}
		
		/**
		 *  Create a new CMSIntermediateResultEvent. 
		 */
		public CMSIntermediateResultEvent(IComponentDescription desc, String name, Object value)
		{
			super(desc);
			this.name = name;
			this.value = value;
		}

		/**
		 *  Get the name.
		 *  @return The name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 *  Set the name.
		 *  @param name The name to set.
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 *  Get the value.
		 *  @return The value.
		 */
		public Object getValue()
		{
			return value;
		}

		/**
		 *  Set the value.
		 *  @param value The value to set.
		 */
		public void setValue(Object value)
		{
			this.value = value;
		}
	}
	
	/**
	 *  Final event of a finished component, including all results. 
	 */
	public static class CMSTerminatedEvent extends CMSStatusEvent
	{
		/** The component results. */
		protected Map<String, Object> results;
		
		/** Component exception if component failed */
		protected Exception exception;

		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSTerminatedEvent()
		{
		}
		
		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSTerminatedEvent(IComponentDescription desc, Map<String, Object> results, Exception exception)
		{
			super(desc);
			this.results = results;
			this.exception = exception;
		}

		/**
		 *  Get the results.
		 *  @return The results.
		 */
		public Map<String, Object> getResults()
		{
			return results;
		}

		/**
		 *  Set the results.
		 *  @param results The results to set.
		 */
		public void setResults(Map<String, Object> results)
		{
			this.results = results;
		}
		
		/**
		 *  Get the component exception if error occurred.
		 *  @return The exception or null if no error.
		 */
		public Exception getException()
		{
			return exception;
		}
		
		/**
		 *  Set the component exception if error occurred.
		 *  @param exception The exception or null if no error.
		 */
		public void setException(Exception exception)
		{
			this.exception = exception;
		}
	}
}