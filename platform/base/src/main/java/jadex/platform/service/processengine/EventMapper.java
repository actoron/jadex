package jadex.platform.service.processengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public class EventMapper
{
	/** The process engine component. */
	protected IInternalAccess	component;
	
	/** The map of event types to mapping infos. */
	protected Map<String, List<MappingInfo>> modelmappings;
	
	/** The map of process to mapping infos (for cleanup). */
	protected Map<String, List<MappingInfo>> modelprocs;
	
	
	/** The map of event types to process models. */
	protected Map<String, List<MappingInfo>> instancemappings;
	
	/** The map of registration id to mapping infos (for cleanup). */
	protected Map<String, List<MappingInfo>> instanceprocs;

	
	/** The model to instance wait event types. */
	protected Map<String, Set<String>> instancewaits;
	
	/**
	 *  Create a new event mapper.
	 */
	public EventMapper(IInternalAccess component)
	{
		this.component	= component;
		this.modelmappings = new HashMap<String, List<MappingInfo>>();
		this.instancemappings = new HashMap<String, List<MappingInfo>>();
		this.instanceprocs = new HashMap<String, List<MappingInfo>>();
		this.modelprocs = new HashMap<String, List<MappingInfo>>();
		this.instancewaits = new HashMap<String, Set<String>>();
	}
	
	/**
	 *  Map an event to a process instance.
	 *  @param event The event object.
	 *  @return The process model.
	 */
	public IFuture<Boolean> processInstanceEvent(Object event, String type)
	{
		Future<Boolean> ret = new Future<Boolean>();
		
		type = getEventType(event, type);
		List<MappingInfo> mis = instancemappings.get(type);
		if(mis!=null && !mis.isEmpty())
		{
			// Copy list to allow concurrent changes
			internalProcessInstanceEvent(event, type, new ArrayList<EventMapper.MappingInfo>(mis), 0, ret);
		}
		else
		{
			ret.setResult(false);
		}

		return ret;
	}
	
	/**
	 * 
	 * @param event
	 * @param type
	 * @param mis
	 * @param i
	 * @param ret
	 */
	protected void	internalProcessInstanceEvent(final Object event, final String type, final List<MappingInfo> mis, final int i, final Future<Boolean> ret)
	{
		MappingInfo	mi	= mis.get(i);
		
		boolean	match;
		try
		{
			match	= mi.getFilter()==null || mi.getFilter().filter(event);
		}
		catch(RuntimeException e)
		{
			component.getLogger().warning("Event mapper filter threw exception: "+e);
			match	= false;
		}
				
		if(match)
		{
			try
			{
				IResultCommand<IFuture<Void>, Object> cmd = (IResultCommand<IFuture<Void>, Object>)mi.getInfo();
				cmd.execute(event).addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setResult(true);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(exception instanceof ComponentTerminatedException)
						{
							if(i<mis.size()-1)
							{
								internalProcessInstanceEvent(event, type, mis, i+1, ret);
							}
							else
							{
								ret.setResult(false);
							}
						}
						else
						{
							component.getLogger().severe("Event mapper command threw exception: "+exception);
							ret.setResult(true);
						}
					}
				}));
			}
			catch(ComponentTerminatedException e)
			{
				if(i<mis.size()-1)
				{
					internalProcessInstanceEvent(event, type, mis, i+1, ret);
				}
				else
				{
					ret.setResult(false);
				}
			}
			catch(RuntimeException e)
			{
				component.getLogger().severe("Event mapper command threw exception: "+e);
				ret.setResult(true);
			}
		}
		else if(i<mis.size()-1)
		{
			internalProcessInstanceEvent(event, type, mis, i+1, ret);
		}
		else
		{
			ret.setResult(false);
		}
	}
	
	/**
	 *  Map an event to a process model.
	 *  @param event The event object.
	 *  @return The process model.
	 */
	public ModelDetails processModelEvent(Object event, String type)
	{
		ModelDetails ret = null;
		type = getEventType(event, type);
		List<MappingInfo> mis = modelmappings.get(type);
		if(mis!=null)
		{
			for(MappingInfo mi: mis)
			{
				if(mi.getFilter()==null || mi.getFilter().filter(event))
				{
					ret = (ModelDetails)mi.getInfo();
					break;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the event type.
	 *  Extracs the type from the event object, when the type nulls.
	 */
	public static String getEventType(Object event, String type)
	{
		if(type==null)
		{
			type = event.getClass().getName();
		}
		
		return type;
	}
	
	/**
	 *  Added an instance mapping.
	 *  @param event The event name.
	 *  @param filter The optional filter.
	 *  @param info The modelname.
	 */
//	public void addModelMapping(IFilter<Object> filter)
	public String addInstanceMapping(UnparsedExpression uexp, String[] events, Map<String, Object> vals, String[] imports, boolean remove, IResultCommand<IFuture<Void>, Object> cmd)
	{
		String	id	= SUtil.createUniqueId("EventMapping");
		while(instancemappings.containsKey(id))
		{
			id	= SUtil.createUniqueId("EventMapping");
		}
		
//		System.out.println("adding instance event matcher: "+id+" "+SUtil.arrayToString(events));

		IFilter<Object> filter = null;
		
		if(uexp!=null)
		{
			final IParsedExpression exp = SJavaParser.parseExpression(uexp, imports, null);
			final SimpleValueFetcher fetcher = new SimpleValueFetcher();
			if(vals!=null)
			{
				fetcher.setValues(vals);
			}
			filter = new IFilter<Object>()
			{
				public boolean filter(Object obj)
				{
					fetcher.setValue("$event", obj);
					Object ret = exp.getValue(fetcher);
					return ret instanceof Boolean? ((Boolean)ret).booleanValue(): false;
				}
			};
		}
		
		List<MappingInfo> rems	= new ArrayList<MappingInfo>();
		instanceprocs.put(id, rems);
		
		final IResultCommand<IFuture<Void>, Object> fcmd = cmd;
		final String fid = id;
		if(remove)
		{
			cmd = new IResultCommand<IFuture<Void>, Object>()
			{
				public IFuture<Void> execute(Object args)
				{
					removeInstanceMappings(fid);
					return fcmd.execute(args);
				}
			};
		}
		
		for(String event: events)
		{
			List<MappingInfo> mis = instancemappings.get(event);
			if(mis==null)
			{
				mis = new ArrayList<MappingInfo>();
				instancemappings.put(event, mis);
			}
			MappingInfo mi = new MappingInfo(event, filter, cmd);
			mis.add(mi);
			rems.add(mi);
		}
		
		return id;
	}
	
	/**
	 *  Remove mappings for a process instance.
	 *  @param id The id from the registration.
	 */
	public void removeInstanceMappings(String id)
	{
		System.out.println("removing instance event matcher: "+id);
		
		// Get mappinginfos to remove and remove them
		List<MappingInfo> rems = instanceprocs.remove(id);
		if(rems!=null)
		{
			for(MappingInfo mi: rems)
			{
				List<MappingInfo> mis = instancemappings.get(mi.getEvent());
				mis.remove(mi);
			}
		}
	}

	/**
	 *  Added a mapping.
	 *  @param event The event name.
	 *  @param filter The optional filter.
	 *  @param modelname The modelname.
	 */
	public void addModelMapping(String[] events, IFilter<Object> filter, String modelname, IResourceIdentifier rid, 
		String actid, SubscriptionIntermediateFuture<ProcessEngineEvent> fut, Set<String> waittypes, Set<String> epstarttypes)
	{
		for(String event: events)
		{
			List<MappingInfo> mis = modelmappings.get(event);
			if(mis==null)
			{
				mis = new ArrayList<MappingInfo>();
				modelmappings.put(event, mis);
			}
			MappingInfo mi = new MappingInfo(event, filter, new ModelDetails(rid, modelname, actid, waittypes, epstarttypes, fut));
			mis.add(mi);
			
			List<MappingInfo> rems = modelprocs.get(modelname);
			if(rems==null)
			{
				rems = new ArrayList<MappingInfo>();
				modelprocs.put(modelname, rems);
			}
			rems.add(mi);
		}
		
		Set<String> evs = new HashSet<String>();
		if(waittypes!=null)
		{
			evs.addAll(waittypes);
		}
		if(epstarttypes!=null)
		{
			evs.addAll(epstarttypes);
		}
		if(!evs.isEmpty())
		{
			instancewaits.put(modelname, evs);
		}
	}
	
	/**
	 *  Remove mappings for a process model.
	 *  @param modelname The modelname.
	 */
	public void removeModelMappings(String modelname)
	{
		// Get mappinginfos to remove and remove them
		List<MappingInfo> rems = modelprocs.remove(modelname);
		if(rems!=null)
		{
			for(MappingInfo mi: rems)
			{
				List<MappingInfo> mis = modelmappings.get(mi.getEvent());
				mis.remove(mi);
			}
		}
		
		instancewaits.remove(modelmappings);
	}
	
	/**
	 *  Test if an event type is potentially matched by any of the
	 *  intermediate wait events (domain types). 
	 */
	public boolean isEventInstanceWaitRelevant(String event)
	{
		boolean ret = false;
		
		for(Set<String> events: instancewaits.values())
		{
			ret = events.contains(event);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static class MappingInfo
	{
		/** The event. */
		protected String event;
		
		/** The filter. */
		protected IFilter<Object> filter;
		
		/** The process model file name. */
		protected Object info;

		/**
		 *  Create a new MappingInfo.
		 */
		public MappingInfo(String event, IFilter<Object> filter, Object info)
		{
			this.event = event;
			this.filter = filter;
			this.info = info;
		}

		/**
		 *  Get the event.
		 *  return The event.
		 */
		public String getEvent()
		{
			return event;
		}

		/**
		 *  Set the event. 
		 *  @param event The event to set.
		 */
		public void setEvent(String event)
		{
			this.event = event;
		}

		/**
		 *  Get the filter.
		 *  return The filter.
		 */
		public IFilter<Object> getFilter()
		{
			return filter;
		}

		/**
		 *  Set the filter. 
		 *  @param filter The filter to set.
		 */
		public void setFilter(IFilter<Object> filter)
		{
			this.filter = filter;
		}

		/**
		 *  Get the info.
		 *  return The info.
		 */
		public Object getInfo()
		{
			return info;
		}

		/**
		 *  Set the info. 
		 *  @param info The info to set.
		 */
		public void setInfo(Object info)
		{
			this.info = info;
		}
	}
	
	/**
	 * 
	 */
	public static class ModelDetails
	{
		/** The resource identifier. */
		protected IResourceIdentifier rid; 
		
		/** The model name. */
		protected String model;
		
		/** The start event id. */
		protected String eventid;
		
		/** The registration future. */
		protected SubscriptionIntermediateFuture<ProcessEngineEvent> future;

		/** The intermediate wait events. */
		protected Set<String> waittypes;
		
		/** The event process start events. */
		protected Set<String> epstarttypes;
		
		/**
		 *  Create a new ModelDetails.
		 */
		public ModelDetails(IResourceIdentifier rid, String model, String eventid, Set<String> waittypes, Set<String> epstarttypes,
			SubscriptionIntermediateFuture<ProcessEngineEvent> future)
		{
			this.rid = rid;
			this.model = model;
			this.eventid = eventid;
			this.waittypes = waittypes;
			this.epstarttypes = epstarttypes;
			this.future = future;
		}

		/**
		 *  Get the rid.
		 *  return The rid.
		 */
		public IResourceIdentifier getRid()
		{
			return rid;
		}

		/**
		 *  Set the rid. 
		 *  @param rid The rid to set.
		 */
		public void setRid(IResourceIdentifier rid)
		{
			this.rid = rid;
		}

		/**
		 *  Get the model.
		 *  return The model.
		 */
		public String getModel()
		{
			return model;
		}
		
		/**
		 *  Set the model. 
		 *  @param model The model to set.
		 */
		public void setModel(String model)
		{
			this.model = model;
		}

		/**
		 *  Get the eventid.
		 *  return The eventid.
		 */
		public String getEventId()
		{
			return eventid;
		}

		/**
		 *  Set the eventid. 
		 *  @param eventid The eventid to set.
		 */
		public void setEventId(String eventid)
		{
			this.eventid = eventid;
		}

		/**
		 *  Get the future.
		 *  return The future.
		 */
		public SubscriptionIntermediateFuture<ProcessEngineEvent> getFuture()
		{
			return future;
		}

		/**
		 *  Set the future. 
		 *  @param future The future to set.
		 */
		public void setFuture(SubscriptionIntermediateFuture<ProcessEngineEvent> future)
		{
			this.future = future;
		}

		/**
		 *  Get the waittypes.
		 *  @return The waittypes.
		 */
		public Set<String> getEventWaitTypes()
		{
			return waittypes;
		}

		/**
		 *  Set the waittypes.
		 *  @param waittypes The waittypes to set.
		 */
		public void setEventWaitTypes(Set<String> waittypes)
		{
			this.waittypes = waittypes;
		}

		/**
		 *  Get the epstarttypes.
		 *  @return The epstarttypes.
		 */
		public Set<String> getEventProcessStartTypes()
		{
			return epstarttypes;
		}

		/**
		 *  Set the epstarttypes.
		 *  @param epstarttypes The epstarttypes to set.
		 */
		public void setEventProcessStartTypes(Set<String> epstarttypes)
		{
			this.epstarttypes = epstarttypes;
		}
		
	}
}
