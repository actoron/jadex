package jadex.platform.service.df;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.fipa.SearchConstraints;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.bridge.service.types.df.IProperty;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Directory facilitator implementation for standalone platform.
 */
@Service
public class DirectoryFacilitatorService	implements IDF
{
	//-------- attributes --------

	/** The platform. */
	@ServiceComponent
	protected IInternalAccess provider;
	
	/** The cached component management service. */
//	protected IComponentManagementService cms;
	
	/** The cached clock service. */
	protected IClockService clockservice;
	
	/** The registered components. */
	protected IndexMap	components;
	
	//-------- constructors --------

	/**
	 *  Create a standalone df.
	 */
	public DirectoryFacilitatorService()
	{
		this.components	= new IndexMap();
	}
	
	//-------- IDF interface methods --------

	/**
	 *  Register a component description.
	 *  @throws RuntimeException when the component description is already registered.
	 */
	public IFuture<IDFComponentDescription> register(IDFComponentDescription cdesc)
	{
		Future<IDFComponentDescription> ret = new Future<IDFComponentDescription>();
		
		//System.out.println("Registered: "+adesc.getName()+" "+adesc.getLeaseTime());
		IDFComponentDescription clone = SFipa.cloneDFComponentDescription(cdesc, this);

		// Add description, when valid.
		if(clone.getLeaseTime()==null || clone.getLeaseTime().getTime()>clockservice.getTime())
		{
			// Automatically throws exception, when key exists.
			if(components.containsKey(clone.getName()))
				throw new RuntimeException("Component already registered: "+cdesc.getName());
			components.add(clone.getName(), clone);
//				System.out.println("registered: "+clone.getName());
			
			ret.setResult(clone);
		}
		else
		{
			ret.setException(new RuntimeException("Component not registered: "+clone.getName()));
//			System.out.println("not registered: "+clone.getName());			
		}
		
		return ret;
	}

	/**
	 *  Deregister a component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture<Void> deregister(IDFComponentDescription cdesc)
	{
		Future<Void> ret = new Future<Void>();
		
		if(!components.containsKey(cdesc.getName()))
		{
			//throw new RuntimeException("Component not registered: "+adesc.getName());
			ret.setException(new RuntimeException("Component not registered: "+cdesc.getName()));
		}
		else
		{
			components.removeKey(cdesc.getName());
			ret.setResult(null);
			//System.out.println("deregistered: "+adesc.getName());
		}
		
		return ret;
	}

	/**
	 *  Modify a component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture<IDFComponentDescription> modify(IDFComponentDescription cdesc)
	{
		Future<IDFComponentDescription> ret = new Future<IDFComponentDescription>();
		
		// Use clone to avoid caller manipulating object after insertion.
		IDFComponentDescription clone = SFipa.cloneDFComponentDescription(cdesc, this);

		// Change description, when valid.
		if(clone.getLeaseTime()==null || clone.getLeaseTime().getTime()>clockservice.getTime())
		{
			// Automatically throws exception, when key does not exist.
			components.replace(clone.getName(), clone);
			//System.out.println("modified: "+clone.getName());
			ret.setResult(clone);
		}
		else
		{
			//throw new RuntimeException("Invalid lease time: "+clone.getLeaseTime());
			ret.setException(new RuntimeException("Invalid lease time: "+clone.getLeaseTime()));
		}
		
		return ret;
	}

	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture<IDFComponentDescription[]> search(final IDFComponentDescription adesc, final ISearchConstraints con)
	{
		return search(adesc, con, false);
	}
	
//	protected List open = Collections.synchronizedList(new ArrayList());
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture<IDFComponentDescription[]> search(final IDFComponentDescription adesc, final ISearchConstraints con, boolean remote)
	{
		final Future<IDFComponentDescription[]> fut = new Future<IDFComponentDescription[]>();
		
		//System.out.println("Searching: "+adesc.getName());

		final List ret = new ArrayList();

		// If name is supplied, just lookup description.
		if(adesc.getName()!=null)
		{
			if(components.containsKey(adesc.getName()))
			{
				DFComponentDescription ad = (DFComponentDescription)components.get(adesc.getName());
				// Remove description when invalid.
				if(ad.getLeaseTime()!=null && ad.getLeaseTime().getTime()<clockservice.getTime())
					components.removeKey(ad.getName());
				else
					ret.add(ad);
			}
		}

		// Otherwise search for matching descriptions.
		else
		{
			DFComponentDescription[]	descs	= (DFComponentDescription[])components.toArray(new DFComponentDescription[components.size()]);
			for(int i=0; (con==null || con.getMaxResults()==-1 || ret.size()<con.getMaxResults()) && i<descs.length; i++)
			{
				// Remove description when invalid.
				if(descs[i].getLeaseTime()!=null && descs[i].getLeaseTime().getTime()<clockservice.getTime())
				{
					components.removeKey(descs[i].getName());
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
		
//		System.out.println("Started search: "+ret);
//		open.add(fut);
		if(remote)
		{
			provider.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IDF.class, RequiredServiceInfo.SCOPE_GLOBAL)).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					Collection coll = (Collection)result;
//					System.out.println("dfs: "+coll);
					// Ignore search failures of remote dfs
					CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							// Add all services of all remote dfs
							for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
							{
								IDFComponentDescription[] res = (IDFComponentDescription[])it.next();
								if(res!=null)
								{
									for(int i=0; i<res.length; i++)
									{
										ret.add(res[i]);
									}
								}
							}
//							open.remove(fut);
//							System.out.println("Federated search: "+ret);//+" "+open);
							fut.setResult((DFComponentDescription[])ret.toArray(new DFComponentDescription[ret.size()]));
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							open.remove(fut);
							fut.setException(exception);
//								fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
						}
					});
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						IDF remotedf = (IDF)it.next();
						if(remotedf!=DirectoryFacilitatorService.this)
						{
							remotedf.search(adesc, con, false).addResultListener(lis);
						}
						else
						{
							lis.resultAvailable(null);
						}
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					open.remove(fut);
					fut.setResult((DFComponentDescription[])ret.toArray(new DFComponentDescription[ret.size()]));
				}
			});
		}
		else
		{
//			open.remove(fut);
//			System.out.println("Local search: "+ret+" "+open);
			fut.setResult((DFComponentDescription[])ret.toArray(new DFComponentDescription[ret.size()]));
		}

		//System.out.println("Searched: "+ret);
		//return (ComponentDescription[])ret.toArray(new ComponentDescription[ret.size()]);
		
		return fut;
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
	 *  Create a df component description.
	 *  @param component The component.
	 *  @param service The service.
	 *  @return The df component description.
	 */
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription service)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
		ret.setName(component);
		if(service!=null)
			ret.addService(service);
		return ret;
	}
	
	/**
	 *  Create a df component description.
	 *  @param component The component.
	 *  @param service The service.
	 *  @return The df component description.
	 */
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription service, long leasetime)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
		ret.setName(component);
		if(service!=null)
			ret.addService(service);
		
		ret.setLeaseTime(new Date(clockservice.getTime()+leasetime));
		
		return ret;		
	}

	/**
	 *  Create a new df component description.
	 *  @param component The component id.
	 *  @param services The services.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @return The component description.
	 */
	public IDFComponentDescription	createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime)
	{
		DFComponentDescription	ret	= new DFComponentDescription();
		ret.setName(component);
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
	 *  @return The new component identifier.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new ComponentIdentifier(name);
	}*/
	
	/**
	 *  Create a component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new ComponentIdentifier(name, addresses, null);
	}*/

	//-------- IPlatformService interface methods --------
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		final Future<Void> ret = new Future<Void>();
		
		final boolean[]	services	= new boolean[2];
//		cms	= ((IInternalRequiredServicesFeature)provider.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
		boolean	setresult;
		synchronized(services)
		{
			services[0]	= true;
			setresult	= services[0] && services[1];
		}
		if(setresult)
			ret.setResult(null);
//							ret.setResult(getServiceIdentifier());
		
		clockservice	= ((IInternalRequiredServicesFeature)provider.getFeature(IRequiredServicesFeature.class)).getRawService(IClockService.class);
		synchronized(services)
		{
			services[1]	= true;
			setresult	= services[0] && services[1];
		}
		if(setresult)
			ret.setResult(null);
//							ret.setResult(getServiceIdentifier());
		
		return ret;
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
