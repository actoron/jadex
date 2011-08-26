package jadex.simulation.analysis.application.jadex;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections15.map.UnmodifiableMap;


/**
 * Implementation of a DesmoJ service for (single) experiments.
 */
public class JadexExecuteExperimentsService extends ABasicAnalysisSessionService implements IAExecuteExperimentsService
{
	static Integer count = 0;

	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public JadexExecuteExperimentsService(IExternalAccess access)
	{
		super(access, IAExecuteExperimentsService.class, true);

	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(UUID session, final IAExperiment exp)
	{
		final Future res = new Future();
		// if (session==null) session = (UUID) createSession(null).get(susThread);

		// not integrated yet
		// JadexSessionView view = (JadexSessionView) sessionViews.get(session);
		// Integer executions = 0;
		// Integer replicationen = (Integer) exp.getExperimentParameter("Wiederholungen").getValue()-1;

		IResultListener kill = new IResultListener()
		{
			
			@Override
			public void resultAvailable(Object result)
			{
					System.out.println(result);	
					System.out.println(result.getClass());	
					UnmodifiableMap resMap = (UnmodifiableMap) result;
					System.out.println(result.getClass());	
					resMap.get("Chimicals");
//					exp.getOutputParameter("Chimicals").setValue(resMap.get("Chimicals"));
//					exp.getOutputParameter("Fire").setValue(resMap.get("Fire"));
//					exp.getOutputParameter("Victims").setValue(resMap.get("Victims"));
					res.setResult(exp);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				
			}
		};
		
		final IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class).get(susThread);
		cms.createComponent("dm" + count, "jadex/simulation/analysis/application/jadex/model/disastermanagement/DisasterManagement.application.xml",
				new CreationInfo("default", null, access.getComponentIdentifier(),
						false, false, false, false, access.getModel().getAllImports(), null), kill).addResultListener(new IResultListener()
						{
							
							@Override
							public void resultAvailable(final Object result)
							{
								final IClockService clock = (IClockService) SServiceProvider.getService(access.getServiceProvider(), IClockService.class).get(susThread);
								final IComponentIdentifier cid = (IComponentIdentifier)result;
//								System.out.println(clock.getTime());
								clock.createTimer(30000L, new ITimedObject()
								{
									
									@Override
									public void timeEventOccurred(long currenttime)
									{
//										System.out.println(clock.getTime());
										cms.destroyComponent(cid);
									}
								});
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// TODO Auto-generated method stub
								
							}
						});
		
		
		return res;
	}

	@Override
	public Set<Modeltype> supportedModels()
	{
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.Jadex);
		return result;
	}
}
