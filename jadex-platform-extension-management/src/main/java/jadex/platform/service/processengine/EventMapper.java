package jadex.platform.service.processengine;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class EventMapper
{
	/** The map of event types to mapping infos. */
	protected Map<String, List<MappingInfo>> modelmappings;
	
	/** The map of process to mapping infos. */
	protected Map<String, List<MappingInfo>> modelprocs;
	
	/** The map of event types to process models. */
	protected Map<String, List<MappingInfo>> instancemappings;

	
	/**
	 *  Create a new event mapper.
	 */
	public EventMapper()
	{
		this.modelmappings = new HashMap<String, List<MappingInfo>>();
		this.modelprocs = new HashMap<String, List<MappingInfo>>();
	}
	
	/**
	 *  Map an event to a process model.
	 *  @param event The event object.
	 *  @return The process model.
	 */
	public String map(Object event)
	{
		String ret = null;
		String name = event.getClass().getName();
		List<MappingInfo> mis = modelmappings.get(name);
		if(mis!=null)
		{
			for(MappingInfo mi: mis)
			{
				if(mi.getFilter()==null || mi.getFilter().filter(event))
				{
					ret = (String)mi.getInfo();
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Added an instance mapping.
	 *  @param event The event name.
	 *  @param filter The optional filter.
	 *  @param info The modelname.
	 */
//	public void addModelMapping(IFilter<Object> filter)
	public void addInstanceMapping(UnparsedExpression uexp, String[] events, Map<String, Object> vals, String[] imports, ICommand<Object> cmd)
	{
		final IParsedExpression exp = SJavaParser.parseExpression(uexp, imports, null);
		final SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValues(vals);
		
		IFilter<Object> filter = new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				exp.getValue(fetcher);
				return false;
			}
		};
		
		for(String event: events)
		{
			List<MappingInfo> mis = instancemappings.get(event);
			if(mis==null)
			{
				mis = new ArrayList<MappingInfo>();
				modelmappings.put(event, mis);
			}
			MappingInfo mi = new MappingInfo(event, filter, modelname);
		}
		
		
		mis.add(mi);
		
		List<MappingInfo> rems = modelprocs.get(event);
		if(rems==null)
		{
			rems = new ArrayList<MappingInfo>();
			modelprocs.put(modelname, rems);
		}
		rems.add(mi);
	}
	

	/**
	 *  Added a mapping.
	 *  @param event The event name.
	 *  @param filter The optional filter.
	 *  @param modelname The modelname.
	 */
	public void addModelMapping(String event, IFilter<Object> filter, String modelname)
	{
		List<MappingInfo> mis = modelmappings.get(event);
		if(mis==null)
		{
			mis = new ArrayList<MappingInfo>();
			modelmappings.put(event, mis);
		}
		MappingInfo mi = new MappingInfo(event, filter, modelname);
		mis.add(mi);
		
		List<MappingInfo> rems = modelprocs.get(event);
		if(rems==null)
		{
			rems = new ArrayList<MappingInfo>();
			modelprocs.put(modelname, rems);
		}
		rems.add(mi);
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
}
