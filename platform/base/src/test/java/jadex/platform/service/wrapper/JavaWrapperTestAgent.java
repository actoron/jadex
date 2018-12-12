package jadex.platform.service.wrapper;

import java.io.File;
import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests the rule and timer monitoring of initial events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="wrapperservice", type=IJavaWrapperService.class)
})
@ComponentTypes(@ComponentType(name="wrapagent", filename="jadex/platform/service/wrapper/JavaWrapperAgent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="wrapagent")))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class JavaWrapperTestAgent
{
	//-------- attributes --------
	
	/** The wrapper service. */
	// Todo: allow creation at injection time.
//	@AgentService
	protected IJavaWrapperService	wrapperservice;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body(final IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IJavaWrapperService>	fut	= agent.getFeature(IRequiredServicesFeature.class).getService("wrapperservice");
		fut.addResultListener(new ExceptionDelegationResultListener<IJavaWrapperService, Void>(ret)
		{
			public void customResultAvailable(IJavaWrapperService result)
			{
				wrapperservice	= result;
				
				CollectionResultListener<TestReport>	crl	= new CollectionResultListener<TestReport>(6, false,
					new ExceptionDelegationResultListener<Collection<TestReport>, Void>(ret)
				{
					public void customResultAvailable(Collection<TestReport> results)
					{
						agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), results.toArray(new TestReport[results.size()])));
						ret.setResult(null);
					}
				});
				
				testMainClassSuccess().addResultListener(crl);
				testMainClassFailure().addResultListener(crl);
				
				testJarSuccess(agent).addResultListener(crl);
				testJarFailure(agent).addResultListener(crl);

				testLocalRidSuccess(agent).addResultListener(crl);
				testLocalRidFailure(agent).addResultListener(crl);
				
				// todo: test global rids
			}		
		});
		
		return ret;
	}
	
	/**
	 *  Test successful main class invocation.
	 */
	protected IFuture<TestReport>	testMainClassSuccess()
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport	rep	= new TestReport("#1", "Test successful execution of main class");
		
		wrapperservice.executeJava(TestMain.class, null)
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				rep.setSucceeded(true);
				ret.setResult(rep);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				rep.setFailed("Failed due to "+exception);
				ret.setResult(rep);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test main class invocation with exception.
	 */
	protected IFuture<TestReport>	testMainClassFailure()
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport	rep	= new TestReport("#2", "Test failed execution of main class");
		
		wrapperservice.executeJava(TestMain.class, new String[]{"fail"})
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				rep.setFailed("Failed due to missing exception");
				ret.setResult(rep);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof TestMain.TestException)
				{
					rep.setSucceeded(true);
					ret.setResult(rep);
				}
				else
				{
					rep.setFailed("Failed due to wrong exception: "+exception);
					ret.setResult(rep);					
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Test successful jar invocation.
	 */
	protected IFuture<TestReport>	testJarSuccess(IInternalAccess agent)
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		try
		{
			final TestReport	rep	= new TestReport("#3", "Test successful execution of jar file");
			
			wrapperservice.executeJava("../jadex-platform-extension-management/src/test/testapp/testapp-0.0.1.jar", null)
				.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					rep.setSucceeded(true);
					ret.setResult(rep);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					rep.setFailed("Failed due to "+exception);
					ret.setResult(rep);
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}


	/**
	 *  Test failed jar invocation.
	 */
	protected IFuture<TestReport>	testJarFailure(IInternalAccess agent)
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		try
		{
			final TestReport	rep	= new TestReport("#4", "Test failed execution of jar file");
			
			wrapperservice.executeJava("../jadex-platform-extension-management/src/test/testapp/testapp-0.0.1.jar", new String[]{"fail"})
				.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					rep.setFailed("Failed due to missing exception");
					ret.setResult(rep);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if("testapp.TestMain$TestException".equals(exception.getClass().getName()))
					{
						rep.setSucceeded(true);
						ret.setResult(rep);
					}
					else
					{
						rep.setFailed("Failed due to wrong exception: "+exception);
						ret.setResult(rep);					
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}

	/**
	 *  Test successful local rid invocation.
	 */
	protected IFuture<TestReport>	testLocalRidSuccess(IInternalAccess agent)
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		try
		{
			final TestReport	rep	= new TestReport("#5", "Test successful execution of local rid");
			
			File	url	= new File("../jadex-platform-extension-management/src/test/testapp/testapp-0.0.1.jar").getCanonicalFile();
			IResourceIdentifier	rid	= new ResourceIdentifier(
				new LocalResourceIdentifier(agent.getId().getRoot(), url.toURI().toURL()), null);
			
			wrapperservice.executeJava(rid, null)
				.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					rep.setSucceeded(true);
					ret.setResult(rep);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					rep.setFailed("Failed due to "+exception);
					ret.setResult(rep);
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}


	/**
	 *  Test failed local rid invocation.
	 */
	protected IFuture<TestReport>	testLocalRidFailure(IInternalAccess agent)
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		try
		{
			final TestReport	rep	= new TestReport("#6", "Test failed execution of local rid");
			
			File	url	= new File("../jadex-platform-extension-management/src/test/testapp/testapp-0.0.1.jar").getCanonicalFile();
			IResourceIdentifier	rid	= new ResourceIdentifier(
				new LocalResourceIdentifier(agent.getId().getRoot(), url.toURI().toURL()), null);
			
			wrapperservice.executeJava(rid, new String[]{"fail"})
				.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					rep.setFailed("Failed due to missing exception");
					ret.setResult(rep);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if("testapp.TestMain$TestException".equals(exception.getClass().getName()))
					{
						rep.setSucceeded(true);
						ret.setResult(rep);
					}
					else
					{
						rep.setFailed("Failed due to wrong exception: "+exception);
						ret.setResult(rep);					
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
}
