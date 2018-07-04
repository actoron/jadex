package jadex.bdiv3x.runtime;

import java.util.Map;
import java.util.logging.Logger;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  XML version of the capability. 
 *  Is a facade to the old API.
 */
public class CapabilityWrapper implements ICapability
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The scope (i.e., sub capability name). */
	protected String	scope;
	
	/** The capa. */
	protected ICapability capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a new capability.
	 */
	public CapabilityWrapper(IInternalAccess agent, String scope)
	{
		this.agent	= agent;
		this.scope	= scope;
		this.capa	= agent.getComponentFeature(IBDIXAgentFeature.class);
	}
	
	//-------- ICapability interface --------
	
	/**
	 *  Get the scope.
	 *  Method with IExternalAccess return value included
	 *  for compatibility with IInternalAccess. 
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		return agent.getExternalAccess();
	}

//	/**
//	 *  Get the parent (if any).
//	 *  @return The parent.
//	 */
//	public IExternalAccess getParentAccess();

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		return scope!=null ? new BeliefbaseWrapper(capa.getBeliefbase(), scope+MElement.CAPABILITY_SEPARATOR) : capa.getBeliefbase();
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		return scope!=null ? new RGoalbase(agent, scope) : capa.getGoalbase();
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		return scope!=null ? new PlanbaseWrapper(capa.getPlanbase(), scope+MElement.CAPABILITY_SEPARATOR) : capa.getPlanbase();
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		return scope!=null ? new REventbase(agent, scope) : capa.getEventbase();
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		return scope!=null ? new ExpressionbaseWrapper(capa.getExpressionbase(), scope+MElement.CAPABILITY_SEPARATOR) : capa.getExpressionbase();
	}
	
//	/**
//	 * Get the property base.
//	 * @return The property base.
//	 */
//	public IPropertybase getPropertybase();

	/**
	 *  Register a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	registerSubcapability(IMCapabilityReference subcap);

	/**
	 *  Deregister a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	deregisterSubcapability(IMCapabilityReference subcap);*/

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return agent.getLogger();
	}

	/**
	 * Get the agent model.
	 * @return The agent model.
	 */
	public IModelInfo getAgentModel()
	{
		return agent.getModel();
	}

	/**
	 * Get the capability model.
	 * @return The capability model.
	 */
	public IModelInfo getModel()
	{
		return agent.getModel();
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getAgentName()
	{
		return agent.getComponentIdentifier().getLocalName();
	}

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public String getConfigurationName()
	{
		return agent.getConfiguration();
	}

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return agent.getComponentIdentifier();
	}

	/**
	 * Get the component description.
	 * @return The component description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return agent.getComponentDescription();
	}

//	/**
//	 *  Get the platform specific agent object.
//	 *  Allows to do platform specific things.
//	 *  @return The agent object.
//	 */
//	public Object	getPlatformComponent();

//	/**
//	 *  Get the container
//	 *  @return The container.
//	 */
//	public IInternalAccess getServiceContainer();
	
	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class)).getTime();
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return agent.getClassLoader();
	}
	
	/**
	 *  Kill the agent.
	 */
	public IFuture<Map<String, Object>> killAgent()
	{
		return agent.killComponent();
	}
	
//	/**
//	 *  Get the application context.
//	 *  @return The application context (or null).
//	 */
//	public IApplicationContext getApplicationContext();

//	/**
//	 *  Add an agent listener
//	 *  @param listener The listener.
//	 */
//	public IFuture addComponentListener(IComponentListener listener);
//	
//	/**
//	 *  Add an agent listener
//	 *  @param listener The listener.
//	 */
//	public IFuture removeComponentListener(IComponentListener listener);
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm)
	{
		return agent.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
	}
	
//	/**
//	 *  Get subcapability names.
//	 *  @return The future with array of subcapability names.
//	 */
//	public String[]	getSubcapabilityNames()
//	{
//		throw new UnsupportedOperationException();
//	}
//	
//	/**
//	 *  Get a subcapability.
//	 *  @param name The capability name.
//	 *  @return The capability.
//	 */
//	public ICapability	getSubcapability(String name)
//	{
//		throw new UnsupportedOperationException();
//	}
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(final String name);
//	
//	/**
//	 *  Get a required services.
//	 *  @return The services.
//	 */
//	public IFuture getRequiredServices(final String name);

	//-------- helper methods --------

	/**
	 *  Get the capability-specific fetcher (scope==null for agent scope).
	 */
	public static IValueFetcher	getFetcher(final IInternalAccess agent, String scope)
	{
		return getFetcher(agent, scope, null);
	}

	/**
	 *  Get the capability-specific fetcher (scope==null for agent scope).
	 *  Also creates a new fetcher, if values are given.
	 */
	public static IValueFetcher	getFetcher(final IInternalAccess agent, String scope, Map<String, Object> values)
	{
		IValueFetcher	ret	= agent.getFetcher();
		
		if(scope!=null && agent.getComponentFeature0(IBDIXAgentFeature.class)!=null)	
		{
			ICapability	capa	= agent.getComponentFeature(IBDIXAgentFeature.class);
			String	prefix	= scope+MElement.CAPABILITY_SEPARATOR;
			SimpleValueFetcher	fetcher	= new SimpleValueFetcher(ret);
			fetcher.setValue("$beliefbase", new BeliefbaseWrapper(capa.getBeliefbase(), prefix));
			fetcher.setValue("$goalbase", new RGoalbase(agent, scope));
			fetcher.setValue("$planbase", new PlanbaseWrapper(capa.getPlanbase(), prefix));
			fetcher.setValue("$eventbase", new REventbase(agent, scope));
			fetcher.setValue("$expressionbase", new ExpressionbaseWrapper(capa.getExpressionbase(), prefix));
			if(values!=null)
			{
				fetcher.setValues(values);
			}
			ret	= fetcher;
		}
		else if(values!=null && !values.isEmpty())
		{
			SimpleValueFetcher	fetcher	= new SimpleValueFetcher(ret);
			fetcher.setValues(values);
			ret	= fetcher;			
		}
		
		return ret;
	}
}
