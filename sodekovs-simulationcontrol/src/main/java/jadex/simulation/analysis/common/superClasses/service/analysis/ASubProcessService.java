package jadex.simulation.analysis.common.superClasses.service.analysis;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.kernelbase.ExternalAccess;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Basis Implementation for a Subprocess
 * @author 5Haubeck
 *
 */
public class ASubProcessService extends ABasicAnalysisSessionService
{
	protected Map<String, IExternalAccess> sessionValues;
	
	//damit nicht Garbage collected?
	protected Set<ASubProcessView> views = new HashSet<ASubProcessView>();

	public ASubProcessService(IExternalAccess access, Class serviceInterface)
	{
		super(access, serviceInterface, true);
		sessionValues = Collections.synchronizedMap(new HashMap<String, IExternalAccess>());
	}

	protected IFuture startSubprocess(String preSession, String name, String model, Map arguments)
	{
		synchronized (mutex)
		{
			final Future ret = new Future();
			String newSession = preSession;
			if (preSession == null) newSession = (String) createSession(null).get(susThread);

			final String session = newSession;

			IResultListener lis = new IResultListener()
			{
				public void resultAvailable(Object result)
			{
				Map results = null;
				if (result != null)
				{
					results = (Map) result;
				}
				else
				{
					results = new HashMap();
				}
				closeSession(session);
				ret.setResult(results);
			}

				public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
			};

			final IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(susThread);
			cms.createComponent(name + "(S: " + session.toString() + ")", model,
					new CreationInfo(null, arguments, access.getComponentIdentifier(),
							false, false, false, false, access.getModel().getAllImports(), null, null), lis).addResultListener(new IResultListener()
						{

							@Override
							public void resultAvailable(Object result)
							{
								final IComponentIdentifier cid = (IComponentIdentifier) result;
								final ExternalAccess access = (ExternalAccess) cms.getExternalAccess(cid).get(susThread);
								((BpmnInterpreter) access.getInterpreter()).setContextVariable("subProcessView", ((ASubProcessView) sessionViews.get(session)));
								ASubProcessView view = ((ASubProcessView) sessionViews.get(session));
								view.init(access, session, sessions.get(session));
								views.add(view);
								sessionValues.put(session, access);
							}

							@Override
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
						});
			return ret;
		}
	}

	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			String id = UUID.randomUUID().toString();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ASubProcessView());
			notify(new AServiceEvent(this, AConstants.SERVICE_SESSION_START, id));
			return new Future(id);
		}
	}

}
