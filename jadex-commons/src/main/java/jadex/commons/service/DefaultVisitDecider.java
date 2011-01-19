package jadex.commons.service;

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
	public synchronized boolean searchNode(IServiceProvider source, IServiceProvider target, boolean ischild, Collection results)
	{
		boolean ret = !(abort && results.size()>0);
		
		// todo: support other search scopes!!!
		if(ret)
		{
			if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
			{
				// Ok when on start component.
				ret = source==null;
			}
			else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
			{
				// Ok when target is child of source.
				ret = ischild && !isRemoteComponent(target);
			}
//			else if(RequiredServiceInfo.APPLICATION_SCOPE.equals(scope))
//			{
//				// Ok when does not cross application boundry.
//				ret = (!isApplication(source) || ischild) && !isRemoteComponent(target);
//			}
			else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope) || RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
			{
				// Ok when does not cross application boundry.
				ret = !isRemoteComponent(target);
			}
			else if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				// Always true if global scope.
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
	
	// NOTE! This methods currently must be commented for remote searches
	// to work. Otherwise, remote=true will lead to ping-pong searches that
	// never terminate. todo: make search information more declarative and
	// allow to modify the search strategy on the sending or receiving host.
	
//	/**
//	 *  Get the remote.
//	 *  @return the remote.
//	 */
//	public boolean isRemote()
//	{
//		return remote;
//	}
//
//	/**
//	 *  Set the remote.
//	 *  @param remote The remote to set.
//	 */
//	public void setRemote(boolean remote)
//	{
//		this.remote = remote;
//	}

	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return this.getClass().getName()+abort+scope;
	}
	
//	/**
//	 *  Test if a target is a remote component.
//	 */
//	protected boolean isApplication(IServiceProvider target)
//	{
//		return ?;
//	}
	
	/**
	 *  Test if a target is a remote component.
	 */
	protected boolean isRemoteComponent(IServiceProvider target)
	{
		return target!=null && target.getClass().getName().indexOf("RemoteServiceContainer")!=-1;
	}
}
