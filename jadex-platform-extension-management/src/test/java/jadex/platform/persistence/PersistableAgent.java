package jadex.platform.persistence;

import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
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
		
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(agent.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService result)
			{
				IFuture<IPersistInfo> fut = result.getPersistableState(agent.getComponentIdentifier());
				fut.addResultListener(new IResultListener<IPersistInfo>()
				{
					public void resultAvailable(IPersistInfo result)
					{
						System.out.println("Worked");
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
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
