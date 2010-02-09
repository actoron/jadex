package jadex.simulation.evaluation;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;


/**
 * TMP eval of MarsWorld Experiments at HAW woth "send target info to nearest Sentry"
 * @author ante
 *
 */
public class TmpEval {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		double[] travelTimes = {175119.634, 176049.63, 100977.68, 144807.076, 138748.87, 175350.55, 152189.38, 165285.89, 128040.239, 104403.93};
		
		//on haw and with "send target to nearest sentry"
		

		//30 for 1 sentry
		double[] travelTimes = {110797, 150453,159047,96094,78500,250297,187156,94015,224485,93203,108407
				,90219,452828,98515,226313,128891,249672,158110,98218,100859,142609,72281,168797,87532,70734
				,108547,95156,111657,121250,82375};
		
		//30 for 2 sentries
//		double[] travelTimes = {60344,163453,77687,53969,82235,110063,62156,53078,56500,52546,107703,86375,54234,113532,117750,67922,
//				97250,67047,84500,62125,79203,75593,90828,50360,59344,84672,70312,91360,103563,65063};

		//30 for 3 sentries
//		double[] travelTimes = {100937,101421,62250,105703,120547,49781,144187,90437,102828,181407,
//				53000,55032,106907,64219,75172,51796,56063,108969,48984,80781,75890,53188,137406,70282,150468,
//				143578,226172,159750,101047,93703};

		//30 for 4 sentries
//		double[] travelTimes = {93234,50469,66578,55032,62937,52968,
//				70328,75125,101922,134421,51922,48985,57859,60984,82953,53515,49078
//				,65391,97750,82500,63484,63187,76219,68609,78000,53063,54172,51281,110937,67750};

		
		DoubleArrayList travelTimesList = new DoubleArrayList(travelTimes);
		travelTimesList.sort();

		double travelTimeMean = Descriptive.mean(travelTimesList);
		double travelTimeMedian = Descriptive.median(travelTimesList);
		double travelTimeSampleVariance = Descriptive.sampleVariance(travelTimesList, travelTimeMean);
		System.out.println( "Travel Time Stats:  Mean value: " + travelTimeMean + ", Median value: " + travelTimeMedian + ", Sample Variance Value: " +  travelTimeSampleVariance);
		System.out.println(travelTimes.length);

	}

}
