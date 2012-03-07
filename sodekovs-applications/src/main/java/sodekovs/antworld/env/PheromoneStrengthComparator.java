package sodekovs.antworld.env;

import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Comparator;

/**
 * Compares pheromones according to their strength.
 *
 */
public class PheromoneStrengthComparator implements Comparator{

	
	public int compare(Object arg0, Object arg1) {
		Integer obj0 = (Integer) ((ISpaceObject) arg0).getProperty("strength");
		Integer obj1 = (Integer) ((ISpaceObject) arg1).getProperty("strength");

		//a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		if(obj0.intValue() < obj1.intValue()){
			return -1;
		}else if(obj0.intValue() > obj1.intValue()){
			return 1;
		}
		// TODO Auto-generated method stub
		return 0;  
	}
}

