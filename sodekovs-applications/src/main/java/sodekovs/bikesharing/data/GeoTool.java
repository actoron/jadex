package sodekovs.bikesharing.data;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

/**
 * Utility class for converting real world units (distance, time) into Jadex Space Units.
 * 
 * @author Thomas Preisler
 */
public class GeoTool {

	/**
	 * Calculates the real world width of the simulated area (in km).
	 * 
	 * @return the width of the simulated area in km
	 */
	public static double getRealMapWidth() {
		LatLng nw = new LatLng(RealDataExtractor.NORTH, RealDataExtractor.WEST);
		LatLng ne = new LatLng(RealDataExtractor.NORTH, RealDataExtractor.EAST);

		return LatLngTool.distance(nw, ne, LengthUnit.KILOMETER);
	}

	/**
	 * Calculates the real world height of the simulated area (in km).
	 * 
	 * @return the height of the simulated area in km
	 */
	public static double getRealMapHeight() {
		LatLng nw = new LatLng(RealDataExtractor.NORTH, RealDataExtractor.WEST);
		LatLng sw = new LatLng(RealDataExtractor.SOUTH, RealDataExtractor.WEST);

		return LatLngTool.distance(nw, sw, LengthUnit.KILOMETER);
	}

	/**
	 * Converts the distance Jadex Space Unit into km.
	 * 
	 * @param spaceWidth
	 *            the width of the jadex space
	 * @param spaceHeight
	 *            the height of the jadex space
	 * @return how many km a Jadex Space unit is long
	 */
	public static double convertSpaceUnitToKm(double spaceWidth, double spaceHeight) {
		double realMap = (getRealMapWidth() + getRealMapHeight()) / 2.0;
		double space = (spaceWidth + spaceHeight) / 2.0;

		return realMap / space;
	}

	/**
	 * Converts km into the Jadex Space Unit for distance.
	 * 
	 * @param spaceWidth
	 *            the width of the jadex space
	 * @param spaceHeight
	 *            the height of the jadex space
	 * @return how many Jadex Space Units a km is long
	 */
	public static double convertKmtoSpaceUnit(double spaceWidth, double spaceHeight) {
		double realMap = (getRealMapWidth() + getRealMapHeight()) / 2.0;
		double space = (spaceWidth + spaceHeight) / 2.0;

		return space / realMap;
	}

	/**
	 * Converts the given speed (km/h) into the according Jadex Space speed (Space Unit/Tick).
	 * 
	 * @param speed
	 *            the given speed (km/h)
	 * @param spaceWidth
	 *            the width of the jadex space
	 * @param spaceHeight
	 *            the height of the jadex space
	 * @return the according speed in Jadex
	 */
	public static double convertKMPHtoJadex(double speed, double spaceWidth, double spaceHeight) {
		// we assume that a Jadex tick equals 1 minute
		// so to convert km/h to km/min
		speed = speed / 60.0;

		// now to convert the km to Jadex Space Units
		speed = speed * convertKmtoSpaceUnit(spaceWidth, spaceHeight);

		return speed;
	}

	public static void main(String[] args) {
		System.out.println(convertSpaceUnitToKm(100.0, 100.0));
		System.out.println(convertKmtoSpaceUnit(100.0, 100.0));
		System.out.println(convertKMPHtoJadex(15.0, 100.0, 100.0));
	}
}
