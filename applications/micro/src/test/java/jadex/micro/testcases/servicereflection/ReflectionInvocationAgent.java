package jadex.micro.testcases.servicereflection;

import java.io.File;
import java.util.Arrays;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.base.test.util.STest;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
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
	@AgentBody
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
			IService ser = (IService)agent.searchService(new ServiceQuery(new ClassInfo("jadex.micro.testcases.servicereflection.IExampleService"))).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}).get();
			System.out.println("Got result: "+result);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed("Problem: could not find service: "+e);
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
		try
		{
			IExternalAccess platform = Starter.createPlatform().get();
			IFuture<IExternalAccess> fut = platform.createComponent(new CreationInfo().setFilename(ProviderAgent.class.getName()+".class"));
			cid = fut.get().getId();
			IService ser = (IService)platform.searchService(new ServiceQuery(new ClassInfo("jadex.micro.testcases.servicereflection.IExampleService")).setScope(ServiceScope.PLATFORM)).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}).get();
			System.out.println("Got result: "+result);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed("Problem: could not find service: "+e);
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				//if(cid!=null)
				//	agent.getExternalAccess(cid).killComponent().get();
				if(platform!=null)
					platform.killComponent();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		cid = null;
		tr = new TestReport("#3", "Test if reflective remote call without service interface class works");
		try
		{
			//System.out.println(Class.forName("jadex.bdiv3.testcases.servicereflection.INotVisibleService"));
//			System.out.println("curdir: "+new File(".").getAbsolutePath());
			IExternalAccess platform = Starter.createPlatform(STest.getDefaultTestConfig(this.getClass()), new String[]{"-libpath", "../bdiv3/bin/test"}).get();
//			ILibraryService libser = platform.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class)).get();
//			libser.addURL(parid, url).get();
			IFuture<IExternalAccess> fut = platform.createComponent(new CreationInfo().setFilename("jadex.bdiv3.testcases.servicereflection.NotVisibleProviderAgent.class"));
			cid = fut.get().getId();
			System.out.println("platform local:"+agent.getId().getRoot()+" platform remote: "+platform);
			IService lser = (IService)agent.searchService(new ServiceQuery(ILibraryService.class).setSearchStart(platform.getId()).setScope(ServiceScope.PLATFORM)).get();
			System.out.println("libser: "+lser.getServiceId().getProviderId());
			IService ser = (IService)agent.searchService(new ServiceQuery(new ClassInfo("jadex.bdiv3.testcases.servicereflection.INotVisibleService")).setSearchStart(platform.getId()).setScope(ServiceScope.PLATFORM)).get();
			Object result = ser.invokeMethod("add", null, new Object[]{1,2}).get();
			System.out.println("Got result: "+result+" "+Arrays.toString(ser.getClass().getInterfaces()));
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed("Problem: could not find service: "+e);
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				//if(cid!=null)
				//	agent.getExternalAccess(cid).killComponent().get();
				if(platform!=null)
					platform.killComponent();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
	}

	@Override
	public void setConfig(IPlatformConfiguration config)
	{
		config.setValue("superpeerclient.debugservices", "INotVisibleService");
		super.setConfig(config);
	}
}
