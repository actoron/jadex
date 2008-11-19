package jadex.bdi.examples.cleanerworld2;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

/** Environment configuration.
 */
public class Configuration
{
	public static final String ENVIRONMENT_NAME = "CleanerWorld2";
	
	public static final IVector2 AREA_SIZE = new Vector2Double(20.0, 20.0);
	
	public static final String BACKGROUND_TILE = "jadex/bdi/examples/cleanerworld2/images/background.png";
	public static final IVector2 BACKGROUND_TILE_SIZE = new Vector2Double(2.0);
	
	public static final IVector2 WASTE_BIN_SIZE = new Vector2Double(1.0);
	public static final IVector2 CHARGING_STATION_SIZE = new Vector2Double(1.5);
	
	/** Maximum distance a cleaner can reach.
	 */
	public static final IVector1 REACH_DISTANCE = new Vector1Double(0.2);
	/** Cleaner size
	 */
	public static final IVector1 CLEANER_VISUAL_RANGE = new Vector1Double(3.0);
	public static final IVector2 CLEANER_SIZE = new Vector2Double(0.8);
	public static final IVector1 CLEANER_SPEED = new Vector1Double(1.0);
	public static final IVector1 CLEANER_DISCHARGE_RATE = new Vector1Double(0.5);
	public static final IVector1 LOW_BATTERY_THRESHOLD = new Vector1Double(20.0);
}
