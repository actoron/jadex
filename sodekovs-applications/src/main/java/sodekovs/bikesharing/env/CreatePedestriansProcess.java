package sodekovs.bikesharing.env;

import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;

import java.util.HashMap;

import sodekovs.util.math.GetRandom;

/**
 * Process is responsible to create the init setting of pedestrians.
 */
public class CreatePedestriansProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public CreatePedestriansProcess() {
		System.out.println("Created Pedestrians Process!");
	}

	// -------- ISpaceProcess interface --------

	/**
	 * This method will be executed by the object before the process gets added to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space) {
		// this.lasttick = clock.getTick();
		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).get(
				new ThreadSuspendable());

//		createComponents(cms,space);

		System.out.println("Create Pedestrians Process executed.");
		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
	}

	/**
	 * This method will be executed by the object before the process is removed from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space) {
		// System.out.println("create package process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space) {
	}

	/*
	 * Create and start a pedestrian component.
	 */
	private void createComponents(IComponentManagementService cms, IEnvironmentSpace space) {
		cms.createComponent("Pedestrian-" + GetRandom.getRandom(100000), "sodekovs/bikesharing/pedestrian/Pedestrian.agent.xml",
				new CreationInfo(null, new HashMap<String, Object>(), space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				System.out.println("Created Component Pedestrian");
			}
		});
	}
}
