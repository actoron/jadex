package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.service.SServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Command for getting a remote external access.
 */
public class RemoteGetExternalAccessCommand implements IRemoteCommand
{
	//-------- attributes --------

	/** The cache of proxy infos. */
	protected static Map proxyinfos = Collections.synchronizedMap(new LRU(200));
	
	/** The providerid (i.e. the component to start with searching). */
	protected IComponentIdentifier cid;
	
//	/** The target class (if case of external access). */
//	protected Class targetclass;
	
	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote search command.
	 */
	public RemoteGetExternalAccessCommand()
	{
	}

	/**
	 *  Create a new remote search command.
	 */
	public RemoteGetExternalAccessCommand(IComponentIdentifier cid, String callid) //Class targetclass,
	{
		this.cid = cid;
//		this.targetclass = targetclass;
		this.callid = callid;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(final IMicroExternalAccess component, Map waitingcalls)
	{
		final Future ret = new Future();
		
		// fetch component via provider/component id
		final IComponentIdentifier compid = cid!=null? 
			(IComponentIdentifier)cid: component.getComponentIdentifier();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(compid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						ProxyInfo pi = getProxyInfo(component.getComponentIdentifier(), exta);
						ret.setResult(new RemoteGetResultCommand(pi, null, callid));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setResult(new RemoteGetResultCommand(null, exception, callid));
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
			}
		});
		
		return ret;
	}

	/**
	 *  Get the target id.
	 *  @return the target id.
	 */
	public Object getTargetId()
	{
		return cid;
	}

	/**
	 *  Set the target id.
	 *  @param providerid The target id to set.
	 */
	public void setTargetId(IComponentIdentifier providerid)
	{
		this.cid = providerid;
	}

	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
	}
	
	/**
	 *  Get the targetclass.
	 *  @return the targetclass.
	 * /
	public Class getTargetClass()
	{
		return targetclass;
	}*/

	/**
	 *  Set the targetclass.
	 *  @param targetclass The targetclass to set.
	 * /
	public void setTargetClass(Class targetclass)
	{
		this.targetclass = targetclass;
	}*/

	/**
	 *  Get a proxy info for a component. 
	 */
	protected ProxyInfo getProxyInfo(IComponentIdentifier rms, IExternalAccess target)
	{
		ProxyInfo ret;
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		ret = (ProxyInfo)proxyinfos.get(target);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ProxyInfo)proxyinfos.get(target);
				if(ret==null)
				{
					ret = createProxyInfo(rms, target);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a proxy info for a service. 
	 */
	protected ProxyInfo createProxyInfo(IComponentIdentifier rms, IExternalAccess target)
	{
		Class targetclass = null;
		Class[] inter = target.getClass().getInterfaces();
		for(int i=0; i<inter.length && targetclass==null; i++)
		{
			if(SReflect.isSupertype(IExternalAccess.class, inter[0]));
				targetclass = inter[i]; 
		}
		if(targetclass==null)
			targetclass = IExternalAccess.class;
		
		ProxyInfo ret = new ProxyInfo(rms, cid, targetclass);
		
		// todo: Hack!!!
		// Exclude getServiceProvider() from remote external access interface
		Map props = target.getModel().getProperties();
//		if(props==null)
//		{
//			props = new HashMap();
//			props.put("remote_excluded", new String[]{"getServiceProvider"});
//		}
//		else
//		{
//			Object ex = props.get("remote_excluded");
//			if(ex!=null)
//			{
//				List newex = new ArrayList();
//				for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
//				{
//					newex.add(it.next());
//				}
//				newex.add("getServiceProvider");
//			}
//			else
//			{
//				props.put("remote_excluded", new String[]{"getServiceProvider"});
//			}
//		}
		
		RemoteSearchCommand.fillProxyInfo(ret, target, targetclass, props);
		return ret;
//		System.out.println("Creating proxy for: "+type);
	}
}