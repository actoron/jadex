package jadex.platform.service.componentregistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  The component registry is a component for creating proxy services.
 *  Real services/components are created on demand on service call.
 */
@Agent
@Service
@Imports("jadex.bridge.service.types.cms.*")
@Arguments(
{
	@Argument(name="paargs", clazz=Map.class, description="The parent arguments", defaultvalue="$platformargs"),
	// MonitoringAgent
    @Argument(name="componentinfos", clazz=CreationInfo.class, description="The component models to add initially.",
    	defaultvalue="new CreationInfo[]{"
//    		+ "new CreationInfo(\"jadex/platform/service/componentregistry/HelloAgent.class\"), "
//    		+ "new CreationInfo(\"jadex/platform/service/address/TransportAddressAgent.class\"), "
//    		+ "new CreationInfo(\"jadex/platform/service/message/MessageAgent.class\"), " // message service is raw :-(
//    		+ "new CreationInfo(\"jadex/platform/service/marshal/MarshalAgent.class\"), " // marshal service is raw :-(
    		+ "new CreationInfo(\"jadex/platform/service/chat/ChatAgent.class\"), "
//    		+ "new CreationInfo(\"jadex/platform/service/cli/CliAgent.class\"), "
    		+ "new CreationInfo(\"jadex/platform/service/filetransfer/FileTransferAgent.class\"), "
       		+ "new CreationInfo(\"jadex/platform/service/simulation/SimulationAgent.class\"), "
      		+ "new CreationInfo(\"jadex/platform/service/df/DirectoryFacilitatorAgent.class\"), "
			+ "new CreationInfo(\"jadex/platform/service/monitoring/MonitoringAgent.class\"), "
      		+ "new CreationInfo(\"jadex/platform/service/settings/SettingsAgent.class\"), "
      		+ "new CreationInfo(\"%{$args.paargs.rspublishcomponent}\"), "
      		+ "new CreationInfo(\"jadex/platform/service/context/ContextAgent.class\", null, (java.util.Map)($args.paargs!=null ? jadex.commons.SUtil.createHashMap(new String[]{\"contextserviceclass\"}, new Object[]{$args.paargs.contextserviceclass}): null)) "
//      		+ "new CreationInfo(\"jadex/platform/service/remote/RemoteServiceManagementAgent.class\")" // has no service :-(
    		+ "}")
})
//@Component(name="rspub", type="$args.RSPUBLISHCOMPONENT", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.rspublish)? 1: 0"),

// jcc
//	@NameValue(name="saveonexit", value="$args.saveonexit"),
//	@NameValue(name="platforms", value="$args.jccplatforms")}),
@ProvidedServices(@ProvidedService(type=IComponentRegistryService.class))
public class ComponentRegistryAgent implements IComponentRegistryService
{
    //-------- attributes --------

    /** The agent. */
    @Agent
    protected IInternalAccess agent;

    /** The registered component types (model name -> ComponentInfo). */
    protected Map<String, ComponentInfo> componenttypes;
    
    /** The registered components (future or iexternalaccess). */
    protected Map<String, Object> components;
    
    //-------- interface methods --------

    /**
     *  Called once after agent creation.
     */
    @AgentCreated
    public IFuture<Void> agentCreated()
    {
//  	System.out.println(((Map)agent.getFetcher().fetchValue("$args")).size());
//		Map<String, Object> args = (Map<String, Object>)agent.getFetcher().fetchValue("$args");
//		for(String name: args.keySet())
//		{
//			System.out.println(name+" "+args.get(name));
//		}
//		Object o = SJavaParser.evaluateExpressionPotentially("%{$args.paargs}", null, agent.getFetcher(), null);
//		System.out.println("eva: "+o);
		
        final Future<Void> ret = new Future<Void>();

        CreationInfo[] cis = (CreationInfo[])agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("componentinfos");
        if(cis!=null)
        {
            CounterResultListener<Void> lis = new CounterResultListener<Void>(cis.length, false, new DelegationResultListener<Void>(ret));
            for(CreationInfo ci: cis)
            {
                // todo: rid
                addComponentType(ci).addResultListener(lis);
            }
        }
        else
        {
            ret.setResult(null);
        }

        return ret;
    }

    /**
     *  Add a new component type and a strategy.
     *  @param componentmodel The component model.
     */
    public IFuture<Void> addComponentType(final CreationInfo info)
    {
        final Future<Void> ret = new Future<Void>();

        ILibraryService ls = SServiceProvider.getLocalService(agent, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
        ls.getClassLoader(info.getResourceIdentifier()).addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
		{
        	public void customResultAvailable(ClassLoader cl) throws Exception
        	{       		
                final String fn = (String)SJavaParser.evaluateExpressionPotentially(info.getFilename(), info.getImports(), agent.getFetcher(), cl);
//              System.out.println("reg component model: "+fn);
                
                if(fn != null)
                {
	                SComponentFactory.loadModel(agent.getExternalAccess(), fn, info.getResourceIdentifier()).addResultListener(new ExceptionDelegationResultListener<IModelInfo, Void>(ret)
	                {
	                    public void customResultAvailable(final IModelInfo model) throws Exception
	                    {
	                        ProvidedServiceInfo[] psis = model.getProvidedServices();
	                        ClassLoader cl = ((ModelInfo)model).getClassLoader();
	                        List<IServiceIdentifier> sids = new ArrayList<IServiceIdentifier>();
	                        
	                        if(psis!=null && psis.length>0)
	                        {
	        	                CounterResultListener<Void> lis = new CounterResultListener<Void>(psis.length, new DelegationResultListener<Void>(ret));
	        	                for(ProvidedServiceInfo psi: psis)
	        	                {
	        	                    final Class<?> servicetype = psi.getType().getType(cl);
	        	                    final IServiceIdentifier fsid = BasicService.createServiceIdentifier(agent, SUtil.createPlainRandomId(servicetype.getName(), 3), servicetype, null, model.getResourceIdentifier(), null);
	        	                    
	        	                    Object serviceproxy = ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class<?>[]{servicetype}, new InvocationHandler()
	        	                    {
	        	                        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	        	                        {
	        	                            assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
	
	//        	                            if(servicetype.getName().indexOf("Settings")!=-1)
	//        	                            	System.out.println("settings called: "+method.getName());
	        	                            
	        	                            if(SReflect.isSupertype(IFuture.class, method.getReturnType()))
	        	                            {
	        		                    		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
	        		                            getComponent(info).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
	        									{
	        		                            	public void customResultAvailable(IExternalAccess exta) throws Exception 
	        		                            	{
	        		                            		IFuture<IService> fut = (IFuture)SServiceProvider.getService(exta, exta.getComponentIdentifier(), servicetype);
	        		                            		fut.addResultListener(new ExceptionDelegationResultListener<IService, Object>(ret)
	        											{
	        		                        				public void customResultAvailable(IService service) throws Exception
	        		                        				{
	        		                        					 IFuture<Object> res = (IFuture<Object>)method.invoke(service, args);
	        		                                             FutureFunctionality.connectDelegationFuture(ret, res);
	        		                        				}
	        											});
	        		                            	}
	        									});
	        		                            
	        		                            return ret;
	        	                            }
	        	                            else if(method.getName().equals("getServiceIdentifier"))
	        	                            {
	        	                            	return fsid;
	        	                            }
	        	                            else if(method.getName().equals("getPropertyMap"))
	        	                            {
	        	                            	// todo:?!
	        	                            	return Collections.EMPTY_MAP;
	        	                            }
	        	                            else
	        	                            {
	        	                            	 IExternalAccess exta = getComponent(info).get();
	        	                            	 IService service = (IService)SServiceProvider.getLocalService(agent, servicetype, exta.getComponentIdentifier());
	        	                            	 return method.invoke(service, args);
	        	                            }
	        	                        }
	        	                    });
	        	                    
	        	        			agent.getComponentFeature(IProvidedServicesFeature.class).addService(null, servicetype, serviceproxy, null, null).addResultListener(lis);
	        	        			sids.add(fsid);
	        	                }
	        	                if(componenttypes==null)
	        	                	componenttypes = new HashMap<String, ComponentInfo>();
	        	                componenttypes.put(info.getFilename(), new ComponentInfo());
	                        }
	                        else
	                        {
	                        	System.out.println("Component model has no provided services: "+fn);
	                        	ret.setResult(null);
	                        }
	                    }
	                });
        		}
                else
                {
                	agent.getLogger().warning("ComponentRegistryAgent did not find model: " + info.toString());
                	ret.setResult(null);
                }
        	}
		});
        
        
        
        return ret;
    }

    /**
     *  Get a component per creation info.
     */
    protected IFuture<IExternalAccess> getComponent(final CreationInfo info)
    {
        final Future<IExternalAccess> ret = new Future<IExternalAccess>();

        if(components==null)
        	components = new HashMap<String, Object>();
        
        Object c = components.get(info.getFilename());
        if(c instanceof IFuture)
        {
        	return (IFuture<IExternalAccess>)c;
        }
        else if(c instanceof IExternalAccess)
        {
        	ret.setResult((IExternalAccess)c);
        }
        else
        {
        	components.put(info.getFilename(), ret);
            final IComponentManagementService cms = SServiceProvider.getLocalService(agent, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
            if(info.getParent()==null)
            	info.setParent(agent.getComponentIdentifier());
            cms.createComponent(info.getFilename(), info).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
            {
                public void firstResultAvailable(IComponentIdentifier cid)
                {
                	cms.getExternalAccess(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret)
                	{
                		public void customResultAvailable(IExternalAccess exta)
                		{
                			components.put(info.getFilename(), exta);
                			super.customResultAvailable(exta);
                		}
                	});
                }

                public void secondResultAvailable(Map<String, Object> result)
                {
                }
                
                public void exceptionOccurred(Exception exception)
                {
                }
            });
        }

        return ret;
    }


    /**
     *  Remove a new component type and a strategy.
     *  @param componentmodel The component model.
     */
    public IFuture<Void> removeComponentType(String componentmodel)
    {
    	final Future<Void> ret = new Future<Void>();
    	
    	if(components!=null)
    	{
    		Object comp = components.get(componentmodel);
    		if(comp instanceof IExternalAccess)
    		{
    			cleanupComponent((IExternalAccess)comp).addResultListener(new DelegationResultListener<Void>(ret));
    		}
    		else if(comp instanceof IFuture)
    		{
    			((IFuture<IExternalAccess>)comp).addResultListener(new IResultListener<IExternalAccess>()
				{
    				public void exceptionOccurred(Exception exception)
    				{
    					ret.setException(exception);
    				}
    				
    				public void resultAvailable(IExternalAccess comp)
    				{
    					cleanupComponent(comp).addResultListener(new DelegationResultListener<Void>(ret));
    				}
				});
    		}
    	}
    	
    	return ret;
    }

    /**
     *  Remove service proxies from the registry component.
     *  Possibly kill the created delegation component.
     */
    protected IFuture<Void> cleanupComponent(IExternalAccess comp)
    {
    	final Future<Void> ret = new Future<Void>();
    	
    	IModelInfo model = comp.getModel();
		
		ComponentInfo ci = componenttypes.get(model.getFilename());
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(ci.getSids()!=null? ci.getSids().size()+1: 1, new DelegationResultListener<Void>(ret));
		
		if(ci.getSids()!=null)
		{
			for(IServiceIdentifier sid: ci.getSids())
			{
				 agent.getComponentFeature(IProvidedServicesFeature.class).removeService(sid).addResultListener(lis);
			}
		}
		
		((IExternalAccess)comp).killComponent().addResultListener(new IResultListener<Map<String,Object>>()
		{
			public void exceptionOccurred(Exception exception)
			{
				lis.exceptionOccurred(exception);
			}
			
			public void resultAvailable(Map<String, Object> result)
			{
				lis.resultAvailable(null);
			}
		});
		
		return ret;
    }
    
    /**
     * 
     */
    public static class ComponentInfo
    {
    	/** The creation info. */
    	protected CreationInfo info;
    	
    	/** The services of the component. */
    	protected List<IServiceIdentifier> sids;

    	/**
    	 *  Create a new ComponentInfo.
    	 */
		public ComponentInfo()
		{
		}
    	
    	/**
    	 *  Create a new ComponentInfo.
    	 */
		public ComponentInfo(CreationInfo info, List<IServiceIdentifier> sids)
		{
			this.info = info;
			this.sids = sids;
		}

		/**
		 *  Get the info.
		 *  @return the info
		 */
		public CreationInfo getInfo()
		{
			return info;
		}

		/**
		 *  Set the info.
		 *  @param info The info to set
		 */
		public void setInfo(CreationInfo info)
		{
			this.info = info;
		}

		/**
		 *  Get the sids.
		 *  @return the sids
		 */
		public List<IServiceIdentifier> getSids()
		{
			return sids;
		}

		/**
		 *  Set the sids.
		 *  @param sids The sids to set
		 */
		public void setSids(List<IServiceIdentifier> sids)
		{
			this.sids = sids;
		}
    }
}
