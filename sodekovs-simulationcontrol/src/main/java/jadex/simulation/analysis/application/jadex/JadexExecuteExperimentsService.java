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
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of a DesmoJ service for (single) experiments.
 */
public class JadexExecuteExperimentsService extends ABasicAnalysisSessionService implements IAExecuteExperimentsService
{
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
		// not integrated yet
		// JadexSessionView view = (JadexSessionView) sessionViews.get(session);
		final Integer replicationen = (Integer) exp.getExperimentParameter("Wiederholungen").getValue();

		IResultListener kill = new IResultListener()
		{

			@Override
			public void resultAvailable(Object result)
			{
				Map resMap = (Map) result;
				// for (String outputName : exp.getOutputParameters().getParameters().keySet())
				// {
				// exp.getOutputParameter(outputName).setValue(resMap.get(outputName));
				// }
				exp.getOutputParameter("Chemicals").setValue(resMap.get("Chemicals"));
				exp.getOutputParameter("Fire").setValue(resMap.get("Fire"));
				exp.getOutputParameter("Victims").setValue(resMap.get("Victims"));
				res.setResult(exp);
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{}
		};
		final IClockService clock = (IClockService) SServiceProvider.getService(access.getServiceProvider(), IClockService.class).get(susThread);

		final IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class).get(susThread);
		cms.createComponent("dm", "jadex/simulation/analysis/application/jadex/model/disastermanagement/DisasterManagement.application.xml",
				new CreationInfo("default", null, access.getComponentIdentifier(),
						false, false, false, false, access.getModel().getAllImports(), null), kill).
						addResultListener(new IResultListener()
						{

							@Override
							public void resultAvailable(final Object result)
						{
							final IComponentIdentifier cid = (IComponentIdentifier) result;
							// System.out.println(clock.getTime());
							clock.createTimer(750000L, new ITimedObject()
							{

								@Override
								public void timeEventOccurred(long currenttime)
								{
									cms.destroyComponent(cid);
								}
							});
						}

							@Override
							public void exceptionOccurred(Exception exception)
						{
								}
						});

		// simExperiment(exp);
		// res.setResult(exp);
		return res;
	}

	private void simExperiment(final IAExperiment exp)
	{
		IResultListener kill = new IResultListener()
		{

			@Override
			public void resultAvailable(Object result)
			{
				Map resMap = (Map) result;
				exp.getOutputParameter("Chemicals").setValue(resMap.get("Chemicals"));
				exp.getOutputParameter("Fire").setValue(resMap.get("Fire"));
				exp.getOutputParameter("Victims").setValue(resMap.get("Victims"));
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{}
		};
		final IClockService clock = (IClockService) SServiceProvider.getService(access.getServiceProvider(), IClockService.class).get(susThread);

		final IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class).get(susThread);
		cms.createComponent("dm", "jadex/simulation/analysis/application/jadex/model/disastermanagement/DisasterManagement.application.xml",
				new CreationInfo("default", null, access.getComponentIdentifier(),
						false, false, false, false, access.getModel().getAllImports(), null), kill).addResultListener(new IResultListener()
					{

						@Override
						public void resultAvailable(final Object result)
						{
							final IComponentIdentifier cid = (IComponentIdentifier) result;
							// System.out.println(clock.getTime());
							clock.createTimer(10000L, new ITimedObject()
							{

								@Override
								public void timeEventOccurred(long currenttime)
								{
									cms.destroyComponent(cid);
								}
							});
						}

						@Override
						public void exceptionOccurred(Exception exception)
						{
						}
					});
	}

	@Override
	public Set<Modeltype> supportedModels()
	{
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.Jadex);
		return result;
	}
}
