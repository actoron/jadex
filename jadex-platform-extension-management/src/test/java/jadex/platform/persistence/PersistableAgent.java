package jadex.platform.persistence;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.persistence.IPersistenceService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Micro agent that tries to persist itself.
 */
@Agent
//@Results(@Result(name="testresults", clazz=Testcase.class))
//Todo: (re)implement persistence
public class PersistableAgent
{
	@Agent
	protected IInternalAccess agent;
	
	protected String pstring;
	
	protected int pint;
	
	public PersistableAgent()
	{
		pstring = "Persistable agent test.";
		pint = 123;
	}
	
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(agent, IPersistenceService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IPersistenceService, Void>(ret)
		{
			public void customResultAvailable(IPersistenceService result)
			{
				IFuture<IPersistInfo> fut = result.snapshot(agent.getComponentIdentifier());
				fut.addResultListener(new IResultListener<IPersistInfo>()
				{
					public void resultAvailable(IPersistInfo result)
					{
						System.out.println("Worked");
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Micro agent that tries to persist itself.", true, null)}));
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Micro agent that tries to persist itself.", exception)}));
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}

	/**
	 *  Gets the pstring.
	 *
	 *  @return The pstring.
	 */
	public String getPstring()
	{
		return pstring;
	}

	/**
	 *  Sets the pstring.
	 *
	 *  @param pstring The pstring to set.
	 */
	public void setPstring(String pstring)
	{
		this.pstring = pstring;
	}

	/**
	 *  Gets the pint.
	 *
	 *  @return The pint.
	 */
	public int getPint()
	{
		return pint;
	}

	/**
	 *  Sets the pint.
	 *
	 *  @param pint The pint to set.
	 */
	public void setPint(int pint)
	{
		this.pint = pint;
	}
	
	
}
