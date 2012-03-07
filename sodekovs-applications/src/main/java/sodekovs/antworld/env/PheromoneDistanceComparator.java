package sodekovs.antworld.env;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector1Double;

import java.util.Comparator;

/**
 * Compares pheromones according to their strength.
 *
 */
public class PheromoneDistanceComparator implements Comparator{

	
	public int compare(Object arg0, Object arg1) {
		Vector1Double obj0 = (Vector1Double) ((ISpaceObject) arg0).getProperty("distance");
		Vector1Double obj1 = (Vector1Double) ((ISpaceObject) arg1).getProperty("distance");

		//a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		if(obj0.getAsDouble() < obj1.getAsDouble()){
			return -1;
		}else if(obj0.getAsDouble() > obj1.getAsDouble()){
			return 1;
		}
		// TODO Auto-generated method stub
		return 0;  
	}
}

