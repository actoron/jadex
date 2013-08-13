package jadex.bridge.service.search;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;

import java.util.Collection;

/**
 *  Default visit decider that implements the following strategy:
 *  - record visited nodes and don't visit any node twice
 *  - Use up and down flags for searching in specific directions only.
 */
public class DefaultVisitDecider implements IVisitDecider
{
	//-------- attributes --------
	
	/** The set of visited nodes. */
//	protected Set visited;

	/** A flag that indicates if node should not be searched when one result is already available. */
	protected boolean abort;
	
//	/** Flag indicating if remote proxies will be visited. */
//	protected boolean remote;
	
	/** The search scope. */
	protected String scope;
	
	//-------- constructors --------

	/**
	 *  Create a new visit decider.
	 *  Abort on first service found is true.
	 */
	public DefaultVisitDecider()
	{
		this(true);
	}
	
	/**
	 *  Create a new visit decider.
	 */
	public DefaultVisitDecider(boolean abort)
	{
		this(abort, RequiredServiceInfo.SCOPE_APPLICATION);
	}
	
	/**
	 *  Create a new visit decider.
	 */
	public DefaultVisitDecider(boolean abort, String scope)
	{
//		this.visited = new HashSet();
		this.abort = abort;
		this.scope = scope;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a specific node should be searched.
	 *  @param source The source data provider.
	 *  @param target The target data provider.
	 *  @param results The preliminary results.
	 */
	public synchronized boolean searchNode(IComponentIdentifier start, IComponentIdentifier source, IComponentIdentifier target, Collection<IService> results)
	{
		boolean ret = !(abort && results.size()>0);
			
		// todo: support other search scopes!!!
		if(ret)
		{
			boolean ischild = false;
			IComponentIdentifier tmp = target;
			while(tmp!=null)
			{
				tmp = tmp.getParent();
				if(start.equals(tmp))
				{
					ischild = true;
					break;
				}
			}
			
			if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
			{
				// Ok when on start component.
				ret = source==null;
			}
			else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
			{
				// Ok when target is child of source or source itself.
				ret = (source==null || ischild) && isSamePlatform(source, target);
			}
			else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
			{
				// Ok when does not cross application boundary.
//				ret = (!isApplication(source) || ischild) && isSamePlatform(source, target);
				ret = (isInApplication(start, target) || ischild) && isSamePlatform(source, target);
			}
			else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
			{
				// Ok when does not cross application boundary.
				// cannot be used as else the proxy services cannot be found
				ret = isSamePlatform(source, target);
			}
			else if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				// Always true if global scope.
			}
			else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
			{
				// True only for parent.
				ret = !ischild && source!=null && source.equals(start);
			}
			else
			{
				throw new RuntimeException("Unknown search scope: "+scope);
			}
		}
		
		// Hack!!!
//		if(ret && !RequiredServiceInfo.GLOBAL_SCOPE.equals(scope) && target!=null && target.getClass().getName().indexOf("RemoteServiceContainer")!=-1)
//			ret = false;
		
//		if(visited.contains(target.getId()))
//			System.out.println("rattenkack");
		
//		if(ret && !visited.contains(target.getId()))
//		{
//			visited.add(target.getId());
//			ret = true;
//		}
//		else
//		{
//			ret = false;
//		}
		
//		System.out.println("search: "+target.getId()+" "+ret+" "+visited);
		
//		if(start.getName().startsWith("User"))
//			System.out.println("search: "+start+" "+source+" "+target+" "+ret);
		
		return ret;
	}
	
	/**
	 *  Get the abort flag.
	 *  @return The abort flag.
	 */
	public boolean isAbort()
	{
		return abort;
	}

	/**
	 *  Set the abort flag.
	 *  @param abort The abort flag to set.
	 */
	public void setAbort(boolean abort)
	{
		this.abort = abort;
	}
	
	// Hack!!! Replace global scope with platform scope when deserializing from XML.
	// Otherwise, scope_global will lead to ping-pong searches that
	// never terminate. todo: make search information more declarative and
	// allow to modify the search strategy on the sending or receiving host.
	
	/**
	 *  Set the scope when deserializing from XML.
	 *  Changes global scope to platform scope to avoid infinite ping-pong searches.
	 *  @param scope The scope to set.
	 */
	public void setXMLScope(String scope)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			scope	= RequiredServiceInfo.SCOPE_PLATFORM;

		this.scope = scope;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public String	getXMLScope()
	{
		return scope;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return this.getClass().getName()+abort+scope;
	}
	
	/**
	 *  Test if a target is an application component.
	 */
	protected boolean isApplication(IComponentIdentifier source)
	{
		return source!=null && source.getParent()!=null && source.getParent().getParent()==null;
	}
	
	/**
	 *  Is in application when target can be traced back to app.
	 */
	protected boolean isInApplication(IComponentIdentifier start, IComponentIdentifier target)
	{
		boolean ret = false;
		
		IComponentIdentifier app = start;
		while(app.getParent()!=null && app.getParent().getParent()!=null)
		{
			app = app.getParent();
		}
		IComponentIdentifier tmp = target;
		while(tmp!=null)
		{
			if(tmp.equals(app))
			{
				ret = true;
				break;
			}
			tmp = tmp.getParent();
		}
		
		return ret;
	}
	
	/**
	 *  Test if a target is a remote component.
	 */
	protected boolean isSamePlatform(IComponentIdentifier source, IComponentIdentifier target)
	{
		return source==null || (target!=null && source.getPlatformName().equals(target.getPlatformName())); 
	}
}
