package jadex.simulation.evaluation;

import jadex.simulation.model.result.ExperimentResult;
import jadex.simulation.model.result.IntermediateResult;

public class IntermediateEvaluation {

	/**
	 * Manages the Evaluation of intermediate results. Adds results from latest experiment to lists. Computes then statistics like mean value, median tec.
	 * @param simConf
	 * @param intermediateRes
	 * @param experimentRes
	 * @return
	 */
	public static IntermediateResult updateIntermediateResults(IntermediateResult intermediateRes, ExperimentResult experimentRes) {
		
		//clean up old values:
//		intermediateRes.reInitSomeHashMaps();
//		
//		//add results of last experiment to intermediate results
//		intermediateRes.addObservedEventsListToSortedList(experimentRes.getEvents());
//		intermediateRes.addLatestObserverResultsList(experimentRes.getEvents());
//		
		// do statistics: compute median etc. for types of observed events
//		HashMap<String, ArrayList<ObservedEvent>> observedEvents = intermediateRes.getSortedObservedEvents();
		
//		for (Iterator it = observedEvents.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			ArrayList<ObservedEvent> list = observedEvents.get(key);
//			
//			DoubleArrayList observedValues = new DoubleArrayList();
//			for(ObservedEvent obs : list){
//				//HACK CHANGE IT - 19-7-12
////				observedValues.add(Double.valueOf(obs.getValue()));
//			}
//			
//			//Eval:		
//			//list has to be ordered according to the Colt API
//			observedValues.sort();
//			double meanValue = Descriptive.mean(observedValues);
//			double medianValue = Descriptive.median(observedValues);
//			double sampleVarianceValue = Descriptive.sampleVariance(observedValues, meanValue);
//
//			//store results to hash map 
//			HashMap<String, HashMap<String,String>> statsMap = intermediateRes.getIntermediateStats();
//			statsMap.get(key).put("MeanValue", String.valueOf(meanValue));
//			statsMap.get(key).put("MedianValue", String.valueOf(medianValue));
//			statsMap.get(key).put("sampleVarianceValue", String.valueOf(sampleVarianceValue));
//			
////			intermediateRes.setIntermediateStats(statsMap);
//			
////			System.out.println(key.toString() + "-->" + value.toString());
//		}
////		System.out.println(res);
//		

		return intermediateRes;
	}

}
