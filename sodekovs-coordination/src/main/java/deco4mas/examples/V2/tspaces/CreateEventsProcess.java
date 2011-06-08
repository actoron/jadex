package deco4mas.examples.V2.tspaces;


import jadex.bridge.service.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import deco.lang.dynamics.AgentElementType;
import deco4mas.annotation.agent.CoordinationAnnotation.CoordinationType;
import deco4mas.coordinate.environment.CoordinationSpaceObject;

/**
 * Process is responsible for the life cycle of the Pheromones.
 */
public class CreateEventsProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;

	/** The round counter. */
	protected int counter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public CreateEventsProcess() {
		// System.out.println("Created createEvents Process!");
	}

	// -------- ISpaceProcess interface --------

	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space) {
		this.lasttick = clock.getTick();
		// System.out.println("create package process started.");
	}

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
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

		Grid2D grid = (Grid2D) space;

		double delta = clock.getTick() - lasttick;

		int rate = getProperty("rate") != null ? ((Integer) getProperty("rate")).intValue() : 5;
		// System.out.println("LastTick: " + lasttick + "-->roundCount: " +
		// roundCounter);
		if (delta > rate) {
			// Update "age" of trace route objects
			lasttick = clock.getTick();

			// if (counter < 5) {
			CoordinationSpaceObject coodSpaceObj = new CoordinationSpaceObject();
			if (counter < 5) {
				coodSpaceObj.setProperty(CoordinationSpaceObject.COORDINATION_TYPE, CoordinationType.POSITIVE);
			} else {
				coodSpaceObj.setProperty(CoordinationSpaceObject.COORDINATION_TYPE, CoordinationType.NEGATIVE);
			}
			coodSpaceObj.setProperty(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
			coodSpaceObj.setProperty(CoordinationSpaceObject.AGENT_ELEMENT_TYPE, AgentElementType.BDI_BELIEF);
			coodSpaceObj.setProperty("Counter", counter);

			// ISpaceObject newObj =
			// space.createSpaceObject("coordinationPrototype",
			// coodSpaceObj.getProperties(), null);

			// SpaceObject
			// Map props = new HashMap();
			// props.put(Space2D.PROPERTY_POSITION, new Vector2Int(0,
			// counter));
			// space.createSpaceObject("ball", props, null);
			// System.out.println("#CreateEventsProcess# Process Executed! Created one ball");

			// ISpaceObject[] objects =
			// (ISpaceObject[])grid.getNearObjects(new
			// Vector2Double(0.0,0.0), new Vector1Double(15.0),
			// null).toArray(new ISpaceObject[0]);

			// if(objects.length > 0)

			// grid.fireEnvironmentEvent(new
			// EnvironmentEvent(CoordinationEvent.COORDINATE_START, space,
			// newObj, new String("Coordinate Event Nr." + counter)));

			// }
			// grid.perceptprocessors
			// String agenttype =
			// ((ApplicationContext)getContext()).getAgentType(objects[0].getId());
			// List procs = (List)perceptprocessors.get(agenttype);
			// List procs = (List)perceptprocessors.get(agenttype);

			counter++;
		}
	}
}
