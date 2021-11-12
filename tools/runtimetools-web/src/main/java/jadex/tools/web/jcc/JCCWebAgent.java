package jadex.tools.web.jcc;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.FutureReturnType;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.IResultCommand;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.extension.rs.publish.mapper.IParameterMapper2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Frontend controller web jcc agent.
 *  
 *  Uses invokeServiceMethod() to delegate calls to the corresponding platforms (accessed by frontend).
 */
@ProvidedServices(
{
	@ProvidedService(name="jccweb", type=IJCCWebService.class)//,
		//scope=ServiceScope.PLATFORM,
		//publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="[http://localhost:8080/]webjcc"))
	//@ProvidedService(name="starterweb", type=IStarterWebService.class)
})
@Agent(autostart=Boolean3.FALSE)//,
	//predecessors="jadex.extension.rs.publish.JettyRSPublishAgent") // Hack! could be other publish agent :-(
public class JCCWebAgent implements IJCCWebService
{
	@Agent
	protected IInternalAccess agent;
	
	/*@AgentCreated
	protected IFuture<Void>	setup()
	{
		//getPlatforms();
		
		//IWebPublishService wps = agent.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(IWebPublishService.class));
		//return wps.publishResources("[http://localhost:8080/]", "META-INF/resources2");
	}*/
	
	@AgentArgument
	protected int port;
	
	@AgentArgument
	protected boolean loginsecurity;
	
	@AgentArgument
	protected boolean footer = false;
	
	@AgentArgument
	protected boolean openbrowser = true;
	
	/**
	 *  Wait for the IWebPublishService and then publish the resources.
	 *  @param pubser The publish service.
	 */
	//@AgentServiceQuery
	@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	protected void publish(IWebPublishService wps)
	{
		if(port==0)
			port = 8080;
		
		//getPlatforms().get();
		
		//System.out.println("publish started: "+pubser);
		IServiceIdentifier sid = ((IService)agent.getProvidedService(IJCCWebService.class)).getServiceId();
		
		wps.setLoginSecurity(loginsecurity);
		
		wps.publishService(sid, new PublishInfo("[http://localhost:"+port+"/]webjcc", IPublishService.PUBLISH_RS, null)).get();
		
		wps.publishResources("[http://localhost:"+port+"/]", "META-INF/resources2").get();
		
		if (openbrowser)
		{
			try
			{
				String pfname = agent.getId().getRoot().toString();
				String url = "http://localhost:" + port + "/launch.html?pf=" + pfname;
				ISecurityService secserv = agent.getLocalService(ISecurityService.class);
				String pw = secserv.getPlatformSecret(null).get();
				url += "&pw=" + pw;
				Desktop.getDesktop().browse(new URI(url));
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/**
	 *  Get the established connections.
	 *  @return A list of connections.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPlatforms()
	{
		Future<Collection<IComponentIdentifier>> ret = new Future<>();
		
		ITerminableIntermediateFuture<IExternalAccess> ret1 = agent.searchServices(new ServiceQuery<>(IExternalAccess.class, ServiceScope.NETWORK).setServiceTags(IExternalAccess.PLATFORM));
		ITerminableIntermediateFuture<IExternalAccess> ret2 = agent.searchServices(new ServiceQuery<>(IExternalAccess.class, ServiceScope.GLOBAL).setServiceTags(IExternalAccess.PLATFORM));
	
		FutureBarrier<Collection<IExternalAccess>> bar = new FutureBarrier<>();
		bar.addFuture(ret1);
		bar.addFuture(ret2);
		
		bar.waitFor().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IComponentIdentifier>>(ret)
		{
			@Override
			public void customResultAvailable(Void result) throws Exception
			{
				Collection<IExternalAccess> col1 = bar.getResult(0);
				Collection<IExternalAccess> col2 = bar.getResult(1);
				Collection<IComponentIdentifier> col = new HashSet<>();
				for(IExternalAccess ex: col1)
					col.add(ex.getId());
				for(IExternalAccess ex: col2)
					col.add(ex.getId());
				
				//System.out.println("found platforms: "+col);
				
				ret.setResult(col);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get events about known platforms.
	 *  @return Events for platforms.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IComponentIdentifier>> subscribeToPlatforms()
	{
		ISubscriptionIntermediateFuture<ServiceEvent<IExternalAccess>> net = agent.addQuery(new ServiceQuery<>(IExternalAccess.class, ServiceScope.NETWORK).setEventMode().setServiceTags(IExternalAccess.PLATFORM));
		ISubscriptionIntermediateFuture<ServiceEvent<IExternalAccess>> glo = agent.addQuery(new ServiceQuery<>(IExternalAccess.class, ServiceScope.GLOBAL).setEventMode().setServiceTags(IExternalAccess.PLATFORM));

		ISubscriptionIntermediateFuture<ServiceEvent<IComponentIdentifier>> ret = SFuture.combineSubscriptionFutures(agent, net, glo, new IResultCommand<ServiceEvent<IComponentIdentifier>, ServiceEvent<IExternalAccess>>()
		{
			@Override
			public ServiceEvent<IComponentIdentifier> execute(ServiceEvent<IExternalAccess> res)
			{
				return new ServiceEvent<IComponentIdentifier>(res.getService().getId(), res.getType());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the JCC plugin html fragments.
	 */
	public IFuture<Map<String, String>> getPluginFragments(IComponentIdentifier cid)
	{
		Future<Map<String, String>> ret = new Future<>();
		
		// format "starter", loadTag("jadex/tools/web/starter.tag")
		
		// search local plugin services
		Map<String, String> res = new HashMap<>();
		Map<String, Integer> es = new HashMap<>();
		Map<String, String> res2 = new LinkedHashMap<>();
		
		Collection<IJCCPluginService> pluginsers = agent.getLocalServices(new ServiceQuery<IJCCPluginService>(IJCCPluginService.class, ServiceScope.PLATFORM));
		
		for(IJCCPluginService ser: pluginsers)
		{
			String name = ser.getPluginName().get();
			String tag = ser.getPluginComponent().get();
			if(name!=null && tag!=null)
			{
				res.put(name, tag);
				Integer prio = ser.getPriority().get();
				es.put(name, prio);
			}
			else
			{
				System.out.println("Plugin problem: "+name);
			}
		}
		
		List<String> names = es.entrySet().stream().sorted((a, b) -> b.getValue()-a.getValue()).map(e->e.getKey()).collect(Collectors.toList());
		names.stream().forEach(n-> res2.put(n, res.get(n)));

		// If not local platform
		if(cid!=null && !cid.hasSameRoot(agent.getId().getRoot()))
		{
			agent.searchService(new ServiceQuery<IJCCWebService>(IJCCWebService.class).setSearchStart(cid.getRoot()))
				.addResultListener(new IResultListener<IJCCWebService>()
			{
				public void resultAvailable(IJCCWebService jccser)
				{
					jccser.getPluginFragments(cid).then(m ->
					{
						for(String name: m.values())
						{
							res2.put(name, m.get(name));
						}
						ret.setResult(res2);
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(res2);
				}
			});
		}
		else
		{
			ret.setResult(res2);
		}
		
		//System.out.println("fragments: "+ret);
		
		return ret;
	}
	
	/**
	 *  Get the JCC plugin infos.
	 *  @param cid The id of the platform to be managed.
	 *  @return The plugin infos.
	 */
	public IFuture<JCCWebPluginInfo[]> getPluginInfos(IComponentIdentifier cid)
	{
		Future<JCCWebPluginInfo[]> ret = new Future<>();
		
		// search local plugin services
		List<JCCWebPluginInfo> res = new ArrayList<>();
		
		Collection<IJCCPluginService> pluginsers = agent.getLocalServices(new ServiceQuery<IJCCPluginService>(IJCCPluginService.class, ServiceScope.PLATFORM));
		
		for(IJCCPluginService service: pluginsers)
		{
			String name = service.getPluginName().get();
			boolean unres = ((IService)service).getServiceId().isUnrestricted();
			IServiceIdentifier sid = ((IService)service).getServiceId();
			int prio = service.getPriority().get();
			byte[] icon = null;
			try
			{
				icon = service.getPluginIcon().get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				SUtil.throwUnchecked(e);
			}
			JCCWebPluginInfo pi = new JCCWebPluginInfo(name, icon, prio, unres, sid);
			res.add(pi);
		}
		
		// If not local platform
		if(cid!=null && !cid.hasSameRoot(agent.getId().getRoot()))
		{
			agent.searchService(new ServiceQuery<IJCCWebService>(IJCCWebService.class).setSearchStart(cid.getRoot()))
				.addResultListener(new IResultListener<IJCCWebService>()
			{
				public void resultAvailable(IJCCWebService jccser)
				{
					jccser.getPluginInfos(cid).then(pis ->
					{
						for(JCCWebPluginInfo pi: pis)
						{
							res.add(pi);
						}
						ret.setResult(res.toArray(new JCCWebPluginInfo[res.size()]));
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Ex: "+exception+" "+cid);
					ret.setResult(res.toArray(new JCCWebPluginInfo[res.size()]));
				}
			});
		}
		else
		{
			ret.setResult(res.toArray(new JCCWebPluginInfo[res.size()]));
		}
		
		//System.out.println("fragments: "+ret);
		
		return ret;
	}
	
	/**
	 *  Get the web component fragment for a plugin.
	 *  @param name The plugin name.
	 *  @return The web component fragment.
	 */
	public IFuture<String> getPluginFragment(IServiceIdentifier sid)
	{
		IJCCPluginService s = (IJCCPluginService)agent.getServiceProxy(sid, null);
		return s.getPluginComponent();
	}
	
	/**
	 *  Check if a platform is available.
	 *  @param cid The platform id.
	 *  @return True if platform is available.
	 */
	public IFuture<Boolean> isPlatformAvailable(IComponentIdentifier cid)
	{
		Future<Boolean> ret = new Future<>();
		
		if(cid.getRoot().equals(agent.getId().getRoot()))
		{
			ret.setResult(Boolean.TRUE);
		}
		else
		{
			new ServiceQuery<IExternalAccess>(IExternalAccess.class);
			
			agent.searchService(new ServiceQuery<IExternalAccess>(IExternalAccess.class).setSearchStart(cid.getRoot()).setScope(ServiceScope.PLATFORM))
				.addResultListener(new IResultListener<IExternalAccess>()
			{
				public void exceptionOccurred(Exception exception) 
				{
					ret.setResult(Boolean.FALSE);
				}
				
				public void resultAvailable(IExternalAccess result) 
				{
					ret.setResult(Boolean.TRUE);
				}
			});
		}
		
		return ret;
	}
	
	
	/**
	 *  Invoke a Jadex service on the managed platform.
	 */
	public IFuture<Object> invokeServiceMethod(IComponentIdentifier cid, ClassInfo servicetype, 
		final String methodname, final Object[] args, final ClassInfo[] argtypes, @FutureReturnType final ClassInfo rettype)
	{
		//if(methodname!=null && methodname.indexOf("getChild")!=-1)
		//	System.out.println("INVOKE: " + methodname + " " + servicetype);
		
		// todo: the return type could not be available on this platform :-(
		Class<?> rtype = rettype!=null? rettype.getType(agent.getClassLoader(), agent.getModel().getAllImports()): null;
		final Future<Object> ret = (Future<Object>)SFuture.getNoTimeoutFuture(rtype, agent);

		//if(methodname.indexOf("getSecurityS")!=-1)
		//	System.out.println("invokeServiceMethod: "+servicetype+" "+methodname+" "+Arrays.toString(args)+" "+rettype);
		
		//final String callid = (String)ServiceCall.getCurrentInvocation().getProperty("callid");
		
		// Search service with startpoint of given platform 
		agent.searchService(new ServiceQuery<IService>(servicetype).setSearchStart(cid.getRoot()).setScope(ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IService, Object>(ret)
		{
			@Override
			public void customResultAvailable(IService ser) throws Exception
			{
				IFuture<Object> fut = checkSecurityAndInvoke(ser, servicetype, methodname, args, argtypes, rettype);
				FutureFunctionality.connectDelegationFuture(ret, fut);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				// Did not find the service, so use it locally with cid
				// (Allows for resusing (having some webjcc plugins only) on the access platform)
				//System.out.println("locally with cid: "+ methodname + " " + servicetype);
				IService ser = (IService)agent.getLocalService(servicetype.getType(agent.getClassLoader()));
				
				Object[] args2 = new Object[args!=null? args.length+1: 1];
				if(args!=null)
					System.arraycopy(args, 0, args2, 0, args.length);
				args2[args2.length-1] = cid;
				
				ClassInfo[] argtypes2 = argtypes;
				if(argtypes!=null)
				{
					argtypes2 = new ClassInfo[argtypes.length+1];
					System.arraycopy(argtypes, 0, argtypes2, 0, argtypes.length);
					argtypes2[argtypes2.length-1] = new ClassInfo(IComponentIdentifier.class);
				}
				
				IFuture<Object> fut = checkSecurityAndInvoke(ser, servicetype, methodname, args2, argtypes2, rettype);
				FutureFunctionality.connectDelegationFuture(ret, fut);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check the security level of a service method.
	 *  Access is granted when:
	 *  a) method/service is unrestricted
	 *  b) method/service is restricted and logged in
	 */
	protected IFuture<Object> checkSecurityAndInvoke(IService ser, ClassInfo servicetype, final String methodname, final Object[] args, 
		final ClassInfo[] argtypes, ClassInfo rettype)
	{
		Class<?> rtype = rettype!=null? rettype.getType(agent.getClassLoader(), agent.getModel().getAllImports()): null;
		final Future<Object> ret = (Future<Object>)SFuture.getFuture(rtype);
		final String callid = ServiceCall.getCurrentInvocation()==null? null: (String)ServiceCall.getCurrentInvocation().getProperty("callid");
		
		BasicService.isUnrestricted(ser.getServiceId(), agent,
			new MethodInfo(methodname, argtypes, servicetype.getTypeName()).getMethod(agent.getClassLoader()))
			.addResultListener(new ExceptionDelegationResultListener<Boolean, Object>(ret)
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				super.exceptionOccurred(exception);
			}
				
			@Override
			public void customResultAvailable(Boolean unres) throws Exception
			{
				// if method is restricted -> check if logged in
				if(!unres.booleanValue())
				{
					if(callid==null)
					{
						// No callid = no session = not logged in
						ret.setException(new SecurityException("Service method has restricted access and not logged in: "+methodname+" "+servicetype));
					}
					else
					{
						IWebPublishService wps = agent.getLocalService(IWebPublishService.class);
						wps.isLoggedIn(callid).addResultListener(new ExceptionDelegationResultListener<Boolean, Object>(ret)
						{
							@Override
							public void customResultAvailable(Boolean loggedin) throws Exception
							{
								if(!loggedin.booleanValue())
								{
									ret.setException(new SecurityException("Service method has restricted access and not logged in"+methodname+" "+servicetype));
								}
								else
								{
									// If found on target platform directly invoke on that platform
									//System.out.println("Invoking service method: "+ser+" "+methodname);
									IFuture<Object> fut = ser.invokeMethod(methodname, argtypes, args, rettype);
									FutureFunctionality.connectDelegationFuture(ret, fut);
								}
							}
						});
					}
				}
				else // method is unrestricted -> can call
				{
					// If found on target platform directly invoke on that platform
					//System.out.println("Invoking service method: "+ser+" "+methodname);
					IFuture<Object> fut = ser.invokeMethod(methodname, argtypes, args, rettype);
					FutureFunctionality.connectDelegationFuture(ret, fut);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the configuration for web clients.
	 *  
	 *  @return Configuration for web clients.
	 */
	public IFuture<Map<String, Object>> getWebClientConfiguration()
	{
		Map<String, Object> conf = new HashMap<>();
		
		conf.put("footer", footer);
		
		return new Future<Map<String, Object>>(conf);
	}
	
	/**
	 *  Login to the webjcc.
	 *  @param platformpass The platform password.
	 *  @return True if logged in.
	 * /
	public IFuture<Boolean> login(String platformpass)
	{
		IWebPublishService wps = agent.getLocalService(IWebPublishService.class);
		wps.login(platformpass);
	}*/
	
	/**
	 * 
	 */
	public static class InvokeServiceMethodMapper implements IParameterMapper2
	{
		/**
		 *  Convert parameters.
		 *  @param values The values map to convert.
		 *  @param pinfos The parameter infos (i.e. annotation meta info). 
		 *  				List<Tuple2<String, String>>: says "kind of param" name, path form, query, no and name of parameter 
		 *  				Map<String, Class<?>>: says for this named param use this type (from method param)
		 *  @param context The context (could be the http servlet request or a custom container request).
		 *  @return The converted parameters.
		 */
		public Object[] convertParameters(Map<String, Object> values, Tuple2<List<Tuple2<String, String>>, Map<String, Class<?>>> pinfos, Object request) throws Exception
		{
			List<Object> args = new ArrayList<Object>();
			for(int i=0; ; i++)
			{
				if(values.containsKey("args_"+i))
				{
					args.add(values.get("args_"+i));
				}
				else
				{
					break;
				}
			}
			if(args.size()>0)
				values.put("args", args);
			List<Object> argtypes = new ArrayList<Object>();
			for(int i=0; ; i++)
			{
				if(values.containsKey("argtypes_"+i))
				{
					argtypes.add(values.get("argtypes_"+i));
				}
				else
				{
					break;
				}
			}
			if(argtypes.size()>0)
				values.put("argtypes", argtypes);
			
			Object[] ret = new Object[6];
			ret[0] = values.get("cid");
			ret[1] = values.get("servicetype");
			ret[2] = values.get("methodname");
			ret[3] = values.get("args");
			ret[4] = values.get("argtypes");
			ret[5] = values.get("returntype");
			
			return ret;
		}
	}
}	
