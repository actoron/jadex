package jadex.bridge.service.types.monitoring;

import java.util.Map;

import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;

/**
 *  Interface for monitoring events.
 */
public interface IMonitoringEvent
{
	public static IFilter<IMonitoringEvent> TERMINATION_FILTER = new IFilter<IMonitoringEvent>()
	{
		public boolean filter(IMonitoringEvent obj)
		{
			return obj.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL)
				&& obj.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_COMPONENT);
		}
	};
	
	/** Event denoting creation of an element. */
	public static final String EVENT_TYPE_CREATION		= "created";
	
	/** Event denoting disposal of an element. */
	public static final String EVENT_TYPE_DISPOSAL		= "disposed";
	
	/** Event denoting modification of an element. */
	public static final String EVENT_TYPE_MODIFICATION 	= "modified";
	
	/** Event denoting a single occurrence without temporal extension. */
	public static final String EVENT_TYPE_OCCURRENCE	= "noticed";
	
	/** Bulk event composed of sub events. */
	public static final String EVENT_TYPE_BULK = "bulk";
	
	
	/** Events regarding the execution of a step. */
	public static final String SOURCE_CATEGORY_EXECUTION = "Execution";
	
	/** Events regarding a component. */
	public static final String SOURCE_CATEGORY_COMPONENT = "Component";
	
	/** Events regarding a service. */
	public static final String SOURCE_CATEGORY_SERVICE = "Service";

	/** Events regarding a property. */
	public static final String SOURCE_CATEGORY_PROPERTY = "Property";

	
	// BPMN
	
	/** Events regarding a BPMN activity. */
	public static final String SOURCE_CATEGORY_ACTIVITY	   = "Activity";

	// BDI
	
	/** Events regarding a BDI plan. */
	public static final String SOURCE_CATEGORY_PLAN	   = "Plan";
	
	/** Events regarding a BDI goal. */
	public static final String SOURCE_CATEGORY_GOAL	   = "Goal";
	
	/** Events regarding a BDI fact. */
	public static final String SOURCE_CATEGORY_FACT	   = "Fact";
	
	/** Events regarding a BDI internal event. */
	public static final String SOURCE_CATEGORY_IEVENT	   = "Internal Event";
	
	/** Events regarding a message. */
	public static final String SOURCE_CATEGORY_MESSAGE   = "Message";

	
	public static final String TYPE_SUBSCRIPTION_START		= EVENT_TYPE_CREATION+".subscription";
	
	public static final String TYPE_SERVICECALL_START = EVENT_TYPE_CREATION+"."+SOURCE_CATEGORY_SERVICE;
	
	public static final String TYPE_SERVICECALL_END = EVENT_TYPE_DISPOSAL+"."+SOURCE_CATEGORY_SERVICE;

	public static final String TYPE_COMPONENT_CREATED = EVENT_TYPE_CREATION+"."+SOURCE_CATEGORY_COMPONENT; //"component_created";
	
	public static final String TYPE_COMPONENT_DISPOSED = EVENT_TYPE_DISPOSAL+"."+SOURCE_CATEGORY_COMPONENT; //"component_created";

	public static final String TYPE_PROPERTY_ADDED = EVENT_TYPE_CREATION+"."+SOURCE_CATEGORY_PROPERTY;
	
	public static final String TYPE_PROPERTY_REMOVED = EVENT_TYPE_DISPOSAL+"."+SOURCE_CATEGORY_PROPERTY;

	
	/**
	 *  Get the source component.
	 *  @return The source.
	 */
	public IComponentIdentifier getSourceIdentifier();
	
	/**
	 *  Get the source description, e.g. if it is a service.
	 *  @return The source description.
	 */
	public String getSourceDescription();
	
	/**
	 *  Get the source creation time, i.e. the time 
	 *  when the component was created.
	 *  @return The creation time.
	 */
	public long getSourceCreationTime();
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType();

	/**
	 *  Get the event occurrence time.
	 *  @return The time.
	 */
	public long getTime();
	
//	/**
//	 *  Get the cause.
//	 *  @return The cause.
//	 */
//	public Cause getCause();
//	
//	/**
//	 *  Set the cause.
//	 *  @param cause The cause to set.
//	 */
//	public void setCause(Cause cause);

	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Object getProperty(String name);
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Map<String, Object> getProperties();
	
	/**
	 *  Get the event importance level.
	 */
	public PublishEventLevel getLevel();
}
