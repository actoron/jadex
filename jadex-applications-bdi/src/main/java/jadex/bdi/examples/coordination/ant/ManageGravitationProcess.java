package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Process creates and manages gravitation centers and associated gravitation
 * lines.
 */
public class ManageGravitationProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;
	private boolean firstTime = true;
	public final static String GRAVITATION_DISTANCE = "gravitation distance";
	public final static String GRAVITATION_CENTER = "gravitationCenter";
	public final static String GRAVITATION_CENTER_ID = "gravitation center id";
	public final static String GRAVITATION_CENTER_POS = "gravitation center pos";
	public final static String GRAVITATION_FIELD = "gravitationField";
	public final static String ABSORBED_OBJECTS = "absorbedObjects";
	
	// int roundCounter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public ManageGravitationProcess() {
		// System.out.println("Created Process!");
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

		// double delta = clock.getTick() - lasttick;

		int rate = getProperty("rate") != null ? ((Integer) getProperty("rate")).intValue() : 5;

		if (firstTime) {

			// lasttick = clock.getTick();

			// add gravitation center
//			Vector2Int pos = new Vector2Int(4, 5);
			IVector2 pos = grid.getRandomPosition(new Vector2Int(1,1));
			int gravitationDistance = 1;

			Map<Object,Object> props = new HashMap<Object,Object>();
			props.put(Space2D.POSITION, (IVector2)pos);
			props.put(GRAVITATION_DISTANCE, new Integer(gravitationDistance));
			props.put(GRAVITATION_CENTER_ID, "ABC");
			props.put(ABSORBED_OBJECTS, new Integer(0));
			grid.createSpaceObject(GRAVITATION_CENTER, props, null, null);

			// compute and add surrounding gravitation field for 2D-Environment
			int xPos = pos.getXAsInteger();
			int yPos = pos.getYAsInteger();
			ArrayList<IVector2> gravitationField = getGravitationField(xPos, yPos);
			for(int i=0; i < gravitationField.size(); i++){
				props = new HashMap<Object,Object>();
				props.put(Space2D.POSITION, gravitationField.get(i));
				props.put(GRAVITATION_CENTER_ID, "ABC");
				props.put(GRAVITATION_CENTER_POS, pos);
				grid.createSpaceObject(GRAVITATION_FIELD, props, null, null);
			}
			firstTime = false; 
		}

	}

	/**
	 * Compute the 8 surrounding fields on the gravitation center
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	private ArrayList<IVector2> getGravitationField(int xPos, int yPos) {

		ArrayList<IVector2> res = new ArrayList<IVector2>();
		// -/-
		res.add(new Vector2Int(xPos - 1, yPos - 1));
		// -/0
		res.add(new Vector2Int(xPos - 1, yPos));
		// -/+
		res.add(new Vector2Int(xPos - 1, yPos + 1));
		// 0/-
		res.add(new Vector2Int(xPos, yPos -1));
		// 0/+
		res.add(new Vector2Int(xPos, yPos + 1));
		// +/-
		res.add(new Vector2Int(xPos + 1, yPos - 1));
		// +/0
		res.add(new Vector2Int(xPos + 1, yPos));
		// +/+
		res.add(new Vector2Int(xPos + 1, yPos + 1));
		
		return res;
	}
}
