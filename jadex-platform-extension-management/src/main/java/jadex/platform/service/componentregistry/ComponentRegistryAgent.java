package jadex.platform.service.componentregistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ProxyFactory;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.IPoolStrategy;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2ResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.servicepool.ServiceHandler;
import jadex.platform.service.servicepool.ServicePoolAgent;

/**
 *
 */
@Agent
@Service
@Imports("jadex.bridge.service.types.cms.*")
@Arguments(
{
	// MonitoringAgent
    @Argument(name="componentinfos", clazz=CreationInfo.class, description="The component models to add initially.",
    	defaultvalue="new CreationInfo[]{"
    		+ "new CreationInfo(\"jadex/platform/service/componentregistry/HelloAgent.class\"), "
    		+ "new CreationInfo(\"jadex/platform/service/message/MessageAgent.class\"), "
    		+ "new CreationInfo(\"jadex/platform/service/chat/ChatAgent.class\"), "
    		+ "new CreationInfo(\"jadex/platform/service/cli/CliAgent.class\"), "
    		+ "new CreationInfo(\"jadex/platform/service/filetransfer/FileTransferAgent.class\"), "
       		+ "new CreationInfo(\"jadex/platform/service/simulation/SimulationAgent.class\"), "
      		+ "new CreationInfo(\"jadex/platform/service/df/DirectoryFacilitatorAgent.class\") "
//      		+ "new CreationInfo(\"jadex/platform/service/remote/RemoteServiceManagementAgent.class\")" // has no service :-(
//      		+ "new CreationInfo(\"%{$args.rspublishcomponent}\"), " // todo 	    		
    		+ "}")
})
@ProvidedServices(@ProvidedService(type=IComponentRegistryService.class))
public class ComponentRegistryAgent implements IComponentRegistryService
{
    //-------- attributes --------

    /** The agent. */
    @Agent
    protected IInternalAccess agent;

    /** The registered component types. */
    protected Map<String, CreationInfo> componenttypes;

    /** The registered components (future or iexternalaccess). */
    protected Map<String, Object> components;

    //-------- interface methods --------

    /**
     *  Called once after agent creation.
     */
    @AgentCreated
    public IFuture<Void> agentCreated()
    {
        final Future<Void> ret = new Future<Void>();

        CreationInfo[] cis = (CreationInfo[])agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("componentinfos");

        if(cis!=null)
        {
            CounterResultListener<Void> lis = new CounterResultListener<Void>(cis.length, true, new DelegationResultListener<Void>(ret));
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

        if(componenttypes==null)
        	componenttypes = new HashMap<String, CreationInfo>();
        componenttypes.put(info.getFilename(), info);
        
        SComponentFactory.loadModel(agent.getExternalAccess(), info.getFilename(), info.getResourceIdentifier()).addResultListener(new ExceptionDelegationResultListener<IModelInfo, Void>(ret)
        {
            public void customResultAvailable(final IModelInfo model) throws Exception
            {
                ProvidedServiceInfo[] psis = model.getProvidedServices();
                ClassLoader cl = ((ModelInfo)model).getClassLoader();
                
                if(psis!=null && psis.length>0)
                {
	                CounterResultListener<Void> lis = new CounterResultListener<Void>(psis.length, new DelegationResultListener<Void>(ret));
	                for(ProvidedServiceInfo psi: psis)
	                {
	                    final Class<?> servicetype = psi.getType().getType(cl);
	
	                    Object serviceproxy = ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class<?>[]{servicetype}, new InvocationHandler()
	                    {
	                    	protected IServiceIdentifier sid = null;
	                    	
	                        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	                        {
	                            assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();

//	                            if(servicetype.getName().indexOf("Chat")!=-1)
//	                            	System.out.println("chat called: "+method.getName());
	                            
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
	                            	if(sid==null)
	                            		sid = BasicService.createServiceIdentifier(agent.getComponentIdentifier(), SUtil.createUniqueId(servicetype.getName(), 3), servicetype, null, model.getResourceIdentifier(), null);
	                            	return sid;
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
	                }
                }
                else
                {
                	ret.setResult(null);
                }
            }
        });
        
        return ret;
    }

    /**
     *
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
    	return IFuture.DONE;
    }


}
