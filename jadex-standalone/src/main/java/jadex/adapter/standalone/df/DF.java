package jadex.adapter.standalone.df;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.IProperty;
import jadex.adapter.base.fipa.SFipa;
import jadex.adapter.standalone.AbstractPlatform;
import jadex.adapter.standalone.fipaimpl.ComponentIdentifier;
import jadex.adapter.standalone.fipaimpl.DFComponentDescription;
import jadex.adapter.standalone.fipaimpl.DFServiceDescription;
import jadex.adapter.standalone.fipaimpl.SearchConstraints;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.commons.collection.IndexMap;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.clock.IClockService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Directory facilitator implementation for standalone platform.
 */
public class DF implements IDF, IService
{
	//-------- attributes --------

	/** The platform. */
	protected AbstractPlatform platform;
	
	/** The registered agents. */
	protected IndexMap	agents;
	
	/** The logger. */
	//protected Logger logger;
	
	//-------- constructors --------

	/**
	 *  Create a standalone df.
	 */
	public DF(AbstractPlatform platform)
	{
		this.platform = platform;
		this.agents	= new IndexMap();
		//this.logger = Logger.getLogger("DF" + this);
	}
	
	//-------- IDF interface methods --------

	/**
	 *  Register a component description.
	 *  @throws RuntimeException when the agent is already registered.
	 */
	public void	register(IDFComponentDescription adesc, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		//System.out.println("Registered: "+adesc.getName()+" "+adesc.getLeaseTime());
		IDFComponentDescription clone = SFipa.cloneDFComponentDescription(adesc, this);

		// Add description, when valid.
		IClockService clock = (IClockService)platform.getService(IClockService.class);
		if(clone.getLeaseTime()==null || clone.getLeaseTime().getTime()>clock.getTime())
		{
			synchronized(agents)
			{
				// Automatically throws exception, when key exists.
				if(agents.containsKey(clone.getName()))
					throw new RuntimeException("Componentomponent already registered: "+adesc.getName());
				agents.add(clone.getName(), clone);
//				System.out.println("registered: "+clone.getName());
			}
			
			listener.resultAvailable(this, clone);
		}
		else
		{
			listener.exceptionOccurred(this, new RuntimeException("Componentomponent not registered: "+clone.getName()));
			
//			System.out.println("not registered: "+clone.getName());			
		}
		
		
	}

	/**
	 *  Deregister a component description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	deregister(IDFComponentDescription adesc, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(agents)
		{
			if(!agents.containsKey(adesc.getName()))
			{
				//throw new RuntimeException("Component not registered: "+adesc.getName());
				listener.exceptionOccurred(this, new RuntimeException("Component not registered: "+adesc.getName()));
				return;
			}
			agents.removeKey(adesc.getName());
			//System.out.println("deregistered: "+adesc.getName());
		}
		
		listener.resultAvailable(this, null);
	}

	/**
	 *  Modify a component description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	modify(IDFComponentDescription adesc, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		// Use clone to avoid caller manipulating object after insertion.
		IDFComponentDescription clone = SFipa.cloneDFComponentDescription(adesc, this);

		// Change description, when valid.
		IClockService clock = (IClockService)platform.getService(IClockService.class);
		if(clone.getLeaseTime()==null || clone.getLeaseTime().getTime()>clock.getTime())
		{
			// Automatically throws exception, when key does not exist.
			synchronized(agents)
			{
				agents.replace(clone.getName(), clone);
			}
			//System.out.println("modified: "+clone.getName());
			listener.resultAvailable(this, clone);
		}
		else
		{
			//throw new RuntimeException("Invalid lease time: "+clone.getLeaseTime());
			listener.exceptionOccurred(this, new RuntimeException("Invalid lease time: "+clone.getLeaseTime()));
		}
	}

	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions. 
	 */
	public void	search(IDFComponentDescription adesc, ISearchConstraints con, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		//System.out.println("Searching: "+adesc.getName());

		List	ret	= new ArrayList();

		// If name is supplied, just lookup description.
		if(adesc.getName()!=null)
		{
			synchronized(agents)
			{
				if(agents.containsKey(adesc.getName()))
				{
					DFComponentDescription ad = (DFComponentDescription)agents.get(adesc.getName());
					// Remove description when invalid.
					IClockService clock = (IClockService)platform.getService(IClockService.class);
					if(ad.getLeaseTime()!=null && ad.getLeaseTime().getTime()<clock.getTime())
						agents.removeKey(ad.getName());
					else
						ret.add(ad);
				}
			}
		}

		// Otherwise search for matching descriptions.
		else
		{
			synchronized(agents)
			{
				DFComponentDescription[]	descs	= (DFComponentDescription[])agents.toArray(new DFComponentDescription[agents.size()]);
				for(int i=0; (con==null || con.getMaxResults()==-1 || ret.size()<con.getMaxResults()) && i<descs.length; i++)
				{
					// Remove description when invalid.
					IClockService clock = (IClockService)platform.getService(IClockService.class);
					if(descs[i].getLeaseTime()!=null && descs[i].getLeaseTime().getTime()<clock.getTime())
					{
						agents.removeKey(descs[i].getName());
					}
					// Otherwise match against template.
					else
					{
						if(match(descs[i] ,adesc))
						{
							ret.add(descs[i]);
						}
					}
				}
			}
		}

		//System.out.println("Searched: "+ret);
		//return (ComponentDescription[])ret.toArray(new ComponentDescription[ret.size()]);
		
		listener.resultAvailable(this, ret.toArray(new DFComponentDescription[ret.size()]));
	}

	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership)
	{
		return new DFServiceDescription(name, type, ownership);
	}
	
	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @param properties The properties.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership,
		String[] languages, String[] ontologies, String[] protocols, IProperty[] properties)
	{
		DFServiceDescription	ret	= new DFServiceDescription(name, type, ownership);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguage(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntology(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocol(protocols[i]);
		for(int i=0; properties!=null && i<properties.length; i++)
			ret.addProperty(properties[i]);
		return ret;
	}

	/**
	 *  Create a df agent description.
	 *  @param agent The agent.
	 *  @param service The service.
	 *  @return The df agent description.
	 */
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier agent, IDFServiceDescription service)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
		ret.setName(agent);
		if(service!=null)
			ret.addService(service);
		return ret;
	}

	/**
	 *  Create a new df agent description.
	 *  @param agent The agent id.
	 *  @param services The services.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @return The agent description.
	 */
	public IDFComponentDescription	createDFComponentDescription(IComponentIdentifier agent, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
		ret.setName(agent);
		ret.setLeaseTime(leasetime);
		for(int i=0; services!=null && i<services.length; i++)
			ret.addService(services[i]);
		for(int i=0; languages!=null && i<languages.length; i++)
			ret.addLanguage(languages[i]);
		for(int i=0; ontologies!=null && i<ontologies.length; i++)
			ret.addOntology(ontologies[i]);
		for(int i=0; protocols!=null && i<protocols.length; i++)
			ret.addProtocol(protocols[i]);
		return ret;
	}
	
	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth)
	{
		SearchConstraints	ret	= new SearchConstraints();
		ret.setMaxResults(maxresults);
		ret.setMaxDepth(maxdepth);
		return ret;
	}

	/**
	 *  Create a component identifier.
	 *  @param name The name.
	 *  @param local True for local name ().
	 *  @return The new agent identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new ComponentIdentifier(name);
	}
	
	/**
	 *  Create a component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new ComponentIdentifier(name, addresses, null);
	}

	//-------- IPlatformService interface methods --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// nothing to do.
	}
	
	/**
	 *  Called when the platform shuts down.
	 *  Do necessary cleanup here (if any).
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		listener.resultAvailable(this, null);
	}

	//-------- helper methods --------

	/**
	 *  Test if a component description matches a given template.
	 */
	protected boolean	match(IDFComponentDescription desc, IDFComponentDescription template)
	{
		boolean	ret	= true;

		// Match protocols, languages, and ontologies.
		ret	= includes(desc.getLanguages(), template.getLanguages());
		ret	= ret && includes(desc.getOntologies(), template.getOntologies());
		ret	= ret && includes(desc.getProtocols(), template.getProtocols());

		// Match service descriptions.
		if(ret)
		{
			IDFServiceDescription[]	tservices	= template.getServices();
			for(int t=0; ret && t<tservices.length; t++)
			{
				ret	= false;
				IDFServiceDescription[]	dservices	= desc.getServices();
				for(int d=0; !ret && d<dservices.length; d++)
				{
					ret	= match(dservices[d], tservices[t]);
				}
			}
		}

		return ret;
	}

	/**
	 *  Test if a service description matches a given template.
	 */
	protected boolean	match(IDFServiceDescription desc, IDFServiceDescription template)
	{
		// Match name, type, and ownership;
		boolean	ret	= template.getName()==null || template.getName().equals(desc.getName());
		ret	= ret && (template.getType()==null || template.getType().equals(desc.getType()));
		ret	= ret && (template.getOwnership()==null || template.getOwnership().equals(desc.getOwnership()));

		// Match protocols, languages, ontologies, and properties.
		ret	= ret && includes(desc.getLanguages(), template.getLanguages());
		ret	= ret && includes(desc.getOntologies(), template.getOntologies());
		ret	= ret && includes(desc.getProtocols(), template.getProtocols());
		ret	= ret && includes(desc.getProperties(), template.getProperties());

		return ret;
	}

	/**
	 *  Test if one array of objects is included in the other
	 *  (without considering the order).
	 *  Test is performed using equals().
	 */
	protected boolean	includes(Object[] a, Object[] b)
	{
		Set	entries	= new HashSet();
		for(int i=0; i<b.length; i++)
			entries.add(b[i]);
		for(int i=0; i<a.length; i++)
			entries.remove(a[i]);
		return entries.isEmpty();
	}
}
