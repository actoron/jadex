package jadex.micro.testcases.servicereflection;

import java.io.File;
import java.util.Arrays;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  The user agent searches services and checks if the results are ok.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ReflectionInvocationAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(3);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		IComponentIdentifier cid = null;
		TestReport tr = new TestReport("#1", "Test if reflective call works");
		try
		{
			IFuture<IExternalAccess> fut = agent.createComponent(new CreationInfo().setFilename(ProviderAgent.class.getName()+".class"));
			cid = fut.get().getId();
			IService ser = (IService)agent.searchService(new ServiceQuery<>(new ClassInfo("jadex.micro.testcases.servicereflection.IExampleService"))).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}, null).get();
			System.out.println("Got result: "+result);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
//			tr.setFailed("Problem: could not find service: "+e);
			tr.setFailed(e);
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(cid!=null)
					agent.getExternalAccess(cid).killComponent().get();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		cid = null;
		tr = new TestReport("#2", "Test if reflective remote call works");
		IExternalAccess platform2	= null;
		try
		{
			platform2 = Starter.createPlatform(getConfig().clone()).get();
			IFuture<IExternalAccess> fut = platform2.createComponent(new CreationInfo().setFilename(ProviderAgent.class.getName()+".class"));
			cid = fut.get().getId();
			IService ser = (IService)platform2.searchService(new ServiceQuery<>(new ClassInfo("jadex.micro.testcases.servicereflection.IExampleService")).setScope(ServiceScope.PLATFORM)).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}, null).get();
			System.out.println("Got result: "+result);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
//			tr.setFailed("Problem: could not find service: "+e);
			tr.setFailed(e);
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				//if(cid!=null)
				//	agent.getExternalAccess(cid).killComponent().get();
				if(platform2!=null)
					platform2.killComponent();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		cid = null;
		tr = new TestReport("#3", "Test if reflective remote call without service interface class works");
		IExternalAccess platform3	= null;
		try
		{
			File[]	dirs	= SUtil.findOutputDirs("applications/bdiv3", true);
			System.out.println(agent+" outputs: "+Arrays.toString(dirs));
			IPlatformConfiguration	config	= getConfig().clone()
				.setValue("libpath", dirs)
				.setLogging(true);
			platform3 = Starter.createPlatform(config).get();
//			ILibraryService libser = platform.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class)).get();
//			libser.addURL(parid, url).get();
			IFuture<IExternalAccess> fut = platform3.createComponent(new CreationInfo().setFilename("jadex.bdiv3.testcases.servicereflection.NotVisibleProviderAgent.class"));
			cid = fut.get().getId();
			System.out.println("platform local:"+agent.getId().getRoot()+" platform remote: "+platform3);
//			IService lser = (IService)agent.searchService(new ServiceQuery<>(ILibraryService.class).setSearchStart(platform.getId()).setScope(ServiceScope.PLATFORM)).get();
//			System.out.println("libser: "+lser.getServiceId().getProviderId());
			IService ser = (IService)agent.searchService(new ServiceQuery<>(new ClassInfo("jadex.bdiv3.testcases.servicereflection.INotVisibleService")).setSearchStart(platform3.getId()).setScope(ServiceScope.PLATFORM).setOwner(platform3.getId())).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}, null).get();
			System.out.println("Got result: "+result+" "+Arrays.toString(ser.getClass().getInterfaces()));
			tr.setSucceeded(true);
		}
		catch(Throwable e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed(e instanceof Exception ? (Exception)e : new RuntimeException(e));
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				//if(cid!=null)
				//	agent.getExternalAccess(cid).killComponent().get();
				if(platform3!=null)
					platform3.killComponent();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
	}

	@Override
	public IPlatformConfiguration getConfig()
	{
		return super.getConfig()
			.setLogging(true);
	}
	
//	@Override
//	public void setConfig(IPlatformConfiguration config)
//	{
//		config.setValue("superpeerclient.debugservices", "INotVisibleService, ILibraryService");
//		super.setConfig(config);
//	}
}
