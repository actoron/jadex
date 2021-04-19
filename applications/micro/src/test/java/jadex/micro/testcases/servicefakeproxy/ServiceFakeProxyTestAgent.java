package jadex.micro.testcases.servicefakeproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.impl.remotecommands.IMethodReplacement;
import jadex.bridge.component.impl.remotecommands.ProxyInfo;
import jadex.bridge.component.impl.remotecommands.ProxyReference;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.RemoteTestBaseAgent;

/**
 *  Test if service implementations can be omitted when the agent implements them.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ServiceFakeProxyTestAgent extends RemoteTestBaseAgent
{
	@Agent
	protected IInternalAccess agent;

	/**
	 *  The agent body.
	 */
	//@AgentBody()
	@OnStart
	public IFuture<Void> body()
	{
		TestReport tr1 = new TestReport("#1", "Test if local service proxy can be created");
		try 
		{
//			IComponentDescription[] descs = cms.getComponentDescriptions().get();
			IComponentDescription[] descs = SComponentManagementService.getComponentDescriptions(agent.getId()).get();
			
			System.out.println(Arrays.toString(descs));
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			tr1.setFailed(e.getMessage());
		}
		
		TestReport tr2 = new TestReport("#1", "Test if remote service proxy can be created");
		try 
		{
//			String url	= SUtil.getOutputDirsExpression("jadex-applications-micro", true);	// Todo: support RID for all loaded models.
//			IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig(), new String[]{"-libpath", url, "-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
//				"-saveonexit", "false", "-welcome", "false", //"-awareness", "false",
//			//	"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-simulation", "false", "-printsecret", "false", "-wstransport", "false",
//				"-superpeerclient", "false",
//			}).get();
			
			disableLocalSimulationMode().get();
			
			IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig(getClass())).get();
			
			createProxies(plat).get();
			// awareness is disabled in testsuite
//			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000).get();
			
			ILibraryService ls = getServiceProxy(agent, plat.getId(), ILibraryService.class);
//			IComponentDescription[] descs = cms.getComponentDescriptions().get();
			IComponentDescription[] descs = SComponentManagementService.getComponentDescriptions(agent.getId()).get();
			System.out.println(Arrays.toString(descs));
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			tr1.setFailed(e.getMessage());
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
		return IFuture.DONE;
	}
	
	/**
	 *  Gets a proxy for a known service at a target component.
	 *  @return Service proxy.
	 */
	public static <S> S getServiceProxy(IInternalAccess component, final IComponentIdentifier providerid, final Class<S> servicetype)
	{				
		S ret = null;
		
		boolean local = component.getId().getRoot().equals(providerid.getRoot());
		if(local)
		{
			ret = component.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>( servicetype).setProvider(providerid));
		}
		else
		{
			try
			{
				final IServiceIdentifier sid = BasicService.createServiceIdentifier(providerid, new ClassInfo(servicetype), null, "NULL", null, ServiceScope.GLOBAL, null, true);

				Class<?>[] interfaces = new Class[]{servicetype, IService.class};
				ProxyInfo pi = new ProxyInfo(interfaces);
				pi.addMethodReplacement(new MethodInfo("equals", new Class[]{Object.class}), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return Boolean.valueOf(args[0]!=null && ProxyFactory.isProxyClass(args[0].getClass())
							&& ProxyFactory.getInvocationHandler(obj).equals(ProxyFactory.getInvocationHandler(args[0])));
					}
				});
				pi.addMethodReplacement(new MethodInfo("hashCode", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return Integer.valueOf(ProxyFactory.getInvocationHandler(obj).hashCode());
					}
				});
				pi.addMethodReplacement(new MethodInfo("toString", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return "Fake proxy for service("+sid+")";
					}
				});
				pi.addMethodReplacement(new MethodInfo("getId", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return sid;
					}
				});
				Method getclass = SReflect.getMethod(Object.class, "getClass", new Class[0]);
				pi.addExcludedMethod(new MethodInfo(getclass));
				
				RemoteReference rr = new RemoteReference(providerid, sid);
				ProxyReference pr = new ProxyReference(pi, rr);
				InvocationHandler handler = new RemoteMethodInvocationHandler(component, pr);
				ret = (S)ProxyFactory.newProxyInstance(component.getClassLoader(), 
					interfaces, handler);
//				ret = (S)ProxyFactory.newProxyInstance(component.getClassLoader(), 
//					interfaces, new RemoteMethodInvocationHandler(component, pr));
			}
			catch(Exception e)
			{
				SUtil.rethrowAsUnchecked(e);
			}
		}
		
		return ret;
	}
}
