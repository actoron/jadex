package jadex.simulation.evaluation;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.evaluation.DataTable;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;
import jadex.extension.envsupport.evaluation.ITableDataProvider;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple consumer that consumes events as "observed events" for processing
 * within the master simulation agent.
 */
public class SimulationDataConsumer extends SimplePropertyObject implements ITableDataConsumer {
	// -------- constants --------

	// -------- attributes --------

	/** The map with all observed event: key=timestamp */
	protected ConcurrentHashMap<Long, ArrayList<ObservedEvent>> allObservedEventsMap = new ConcurrentHashMap<Long, ArrayList<ObservedEvent>>();

	// -------- constructors --------

	/**
	 * 
	 */
	public SimulationDataConsumer() {
	}

	// -------- methods --------

	/**
	 * Consume data from the provider.
	 */
	public void consumeData(long timestamp, double tick) {

		// Things needed for the simulation
								
		String experimentId = (String) getSpace().getProperty(Constants.EXPERIMENT_ID);
		
//		String experimentId = (String) getSpace().getContext().getArguments().get(Constants.EXPERIMENT_ID);
//		String appName = getSpace().getContext().getComponentIdentifier().getLocalName();		
		String appName = getSpace().getExternalAccess().getComponentIdentifier().getLocalName();
		
		
		ArrayList<ObservedEvent> observedEvents = new ArrayList<ObservedEvent>();
//		IServiceContainer container = getSpace().getContext().getServiceContainer();
//		IClockService clockservice = (IClockService) container.getService(IClockService.class);
//		long timestamp = clockservice.getTime();
		// -----

		ITableDataProvider provider = getTableDataProvider();
		DataTable table = provider.getTableData(timestamp, tick);

		List rows = table.getRows();
		String[] colnames = table.getColumnNames();
		if (rows != null) {
			for (int i = 0; i < rows.size(); i++) {
				Object[] row = (Object[]) rows.get(i);
				for (int j = 0; j < row.length; j++) {

					// writer.write(""+row[j]);
					observedEvents.add(new ObservedEvent(appName, experimentId, timestamp, colnames[j], String.valueOf(row[j]), tick));
				}

				// Observe elements: ISpaceObjects, BDI-Agents,
				// MicroAgents
				// Handle BDI-Agents separate due asyn call
				// TODO: Differentiate between periodical and onChange
				// Evaluation
				// if(obs.getData().getObjectSource().getType().equals(Constants.BDI_AGENT))
				// {
				// IComponentIdentifier agentIdentifier =
				// AgentMethods.getIComponentIdentifier(space, agentType);
				// if (agentIdentifier != null) {
				// TODO: Apply / Check if filter has been set on
				// this observer data
				// System.out.println("#DeltaTime4Exec# Starting get result for BDIAgent.");
				//
				// IFuture fut = ((IComponentManagementService)
				// space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(agentIdentifier);
				// fut.addResultListener(new IResultListener() {
				//
				// @Override
				// public void resultAvailable(Object source, Object result)
				// {
				// ExternalAccessFlyweight exta = (ExternalAccessFlyweight)
				// result;
				// // Get Fact from Beliefbase
				// // TODO: Not only for Strings meaning:
				// // read the right class from the
				// // data-field!
				// String currentValue =
				// exta.getBeliefbase().getBelief(obs.getData().getElementSource().getName()).getFact().toString();
				// // System.out.println("MayValue: " +
				// // currentValue);
				// observedEvents.add(new ObservedEvent(appName,
				// experimentId, timestamp, obs.getData(), currentValue));
				//
				// }
				//
				// @Override
				// public void exceptionOccurred(Object source, Exception
				// exception) {
				// // TODO Auto-generated method stub
				// }
				// });

				// Observe ISpaceObject
				// } else {
				// System.err.println("#DeltaTimeExecutor4Simulation# Error on setting type of ObjectSource "
				// + simConf);
				// }

			}
		}

		// write result to hashmap that holds all events - HAS
		// to happen outside the for-loop to get the values for
		// all observer at that timestamp
		allObservedEventsMap.put(timestamp, observedEvents);
	}

	/**
	 * Get the space.
	 * 
	 * @return The space.
	 */
	public AbstractEnvironmentSpace getSpace() {
		return (AbstractEnvironmentSpace) getProperty("envspace");
	}

	/**
	 * Get the table data provider.
	 * 
	 * @return The table data provider.
	 */
	protected ITableDataProvider getTableDataProvider() {
		String providername = (String) getProperty("dataprovider");
		ITableDataProvider provider = getSpace().getDataProvider(providername);
		if (provider == null)
			throw new RuntimeException("Data provider nulls: " + providername);
		return provider;
	}

	/**
	 * Return HashMap that contains all observedEvents, according to the specification within the simulation-configuration file
	 * @return
	 */
	public ConcurrentHashMap<Long, ArrayList<ObservedEvent>> getResults() {
		return this.allObservedEventsMap;
	}
}
