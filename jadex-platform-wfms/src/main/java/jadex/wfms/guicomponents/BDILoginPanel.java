package jadex.wfms.guicomponents;

import jadex.bdi.planlib.iasteps.DispatchGoalStep;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.service.IExternalWfmsService;

import java.util.HashMap;
import java.util.Map;

public class BDILoginPanel extends AbstractLoginPanel
{
	protected IBDIExternalAccess agent;
	
	public BDILoginPanel(IBDIExternalAccess agent)
	{
		super();
		this.agent = agent;
		updateWfmsList();
	}
	
	protected IFuture discoverWfms()
	{
		final Future ret = new Future();
		agent.scheduleStep(new DispatchGoalStep("clientcap.discover_wfms")).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				
				Map params = (Map) result;
				ret.setResult(params.get("wfms"));
			}
		});
		
		return ret;
	}
	
	protected IFuture getWfmsName(final IExternalWfmsService wfms)
	{
		return agent.scheduleStep(new DispatchGoalStep("clientcap.request_wfms_name", new HashMap()
		{
			{
				put("wfms", wfms);
			}
		}));
	}
}
