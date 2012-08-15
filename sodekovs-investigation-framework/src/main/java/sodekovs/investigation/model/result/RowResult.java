package sodekovs.investigation.model.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

@XmlRootElement(name = "RowResults")
public class RowResult extends IResult {

	private ArrayList<ExperimentResult> experimentResults = new ArrayList<ExperimentResult>();
	private String optimizationName;
	private String optimizationValue;
	private String optimizationConfiguration;

	// --- contains the FINAL results of the statistical evaluation for
	// each observer type, i.e. the statics for each observed type for this row.
	// private HashMap<String, HashMap<String,String>> finalStatsMap;

	// Contains the transformed observed events of all Experiments of this row. Required in order to access the properties of the object instances in ALL experiments of this row, i.e. the key of the
	// HashMap is the id of the observed objectInstance.
	private HashMap<String, HashMap<String, ArrayList<Object>>> sortedObserveEventsMap;

	// Contains the evaluated data for this row
	private HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> evaluatedRowData;

	@XmlElementWrapper(name = "Experiments")
	@XmlElement(name = "Experiment")
	public ArrayList<ExperimentResult> getExperimentsResults() {
		return experimentResults;
	}

	public void setExperimentsResults(ArrayList<ExperimentResult> experimentResults) {
		this.experimentResults = experimentResults;
	}

	public void addExperimentsResults(ExperimentResult experimentResult) {
		this.experimentResults.add(experimentResult);
	}

	public String getOptimizationName() {
		return optimizationName;
	}

	public void setOptimizationName(String optimizationName) {
		this.optimizationName = optimizationName;
	}

	public String getOptimizationValue() {
		return optimizationValue;
	}

	public void setOptimizationValue(String optimizationValue) {
		this.optimizationValue = optimizationValue;
	}

	/**
	 * Returns the duration of the row
	 * 
	 * @return
	 */
	@XmlAttribute(name = "RowDuration")
	public long getDuraration() {
		return getEndtime() - getStarttime();
	}

	@XmlAttribute(name = "RowNumber")
	public String getId() {
		return id;
	}

	@XmlAttribute(name = "Name")
	public String getName() {
		return name;
	}

	public void setOptimizationConfiguration(String optimizationConfiguration) {
		this.optimizationConfiguration = optimizationConfiguration;
	}

	public String getOptimizationConfiguration() {
		return optimizationConfiguration;
	}

	/**
	 * Contains the transformed observed events of all Experiments of this row. Required in order to access the properties of the object instances in ALL experiments of this row, i.e. the key of the
	 * HashMap is the id of the observed objectInstance.
	 * 
	 * @return
	 */
	public HashMap<String, HashMap<String, ArrayList<Object>>> getSortedObserveEventsMap() {
		return sortedObserveEventsMap;
	}

	/**
	 * Contains the transformed observed events of all Experiments of this row. Required in order to access the properties of the object instances in ALL experiments of this row, i.e. the key of the
	 * HashMap is the id of the observed objectInstance.
	 * 
	 * @param sortedObserveEventsMap
	 */
	public void setSortedObserveEventsMap(HashMap<String, HashMap<String, ArrayList<Object>>> sortedObserveEventsMap) {
		this.sortedObserveEventsMap = sortedObserveEventsMap;
	}

	/**
	 * Contains the evaluated data for this row
	 * 
	 * @return
	 */
	public HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> getEvaluatedRowData() {
		return evaluatedRowData;
	}

	/**
	 * Contains the evaluated data for this row
	 * 
	 * @param evaluatedRowData
	 */
	public void setEvaluatedRowData(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> evaluatedRowData) {
		this.evaluatedRowData = evaluatedRowData;
	}

	// /**
	// * 1. HashMap: Key: Name of observer 2. HashMap: Key: Name of statistical
	// * type, Value: computed value
	// *
	// * @return
	// */
	// public HashMap<String, HashMap<String, String>> getFinalStatsMap() {
	// return finalStatsMap;
	// }
	//
	// public void setFinalStatsMap(HashMap<String, HashMap<String, String>> statsMap) {
	// this.finalStatsMap = statsMap;
	// }

	// public String toStringShortOLD() {
	// // double meanValue = 0.0;
	// DoubleArrayList durations = new DoubleArrayList();
	//
	// StringBuffer buffer = new StringBuffer();
	// buffer.append("Row Number: ");
	// buffer.append(getId());
	// buffer.append("\n");
	// buffer.append("Optimization: Parameter Name and Value: ");
	// buffer.append(getOptimizationConfiguration());
	// /*buffer.append(getOptimizationName());
	// buffer.append(" - ");
	// buffer.append(getOptimizationValue());*/
	// buffer.append("\n\n");
	// buffer.append("Cumulated Stats of Observed Events:");
	//
	// //Print out cumulated stats for each type of observed event
	// //Hack: for agentNegotiation
	// String[] orderOfOutput
	// ={"money_amount","workflows","ChassisbaubilligBlackoutNUMBER","ChassisbaunormalBlackoutNUMBER","ChassisbauteuerBlackoutNUMBER","ChassisbaubilligBlackoutTIME","ChassisbaunormalBlackoutTIME","ChassisbauteuerBlackoutTIME","Chassisbaubillig","Chassisbaunormal","Chassisbauteuer",
	// "ChassisbaubilligTrustValue","ChassisbaunormalTrustValue","ChassisbauteuerTrustValue"};
	// if(finalStatsMap.get("ChassisbaubilligBlackoutNUMBER") != null){//hack!!!
	// for (String key : orderOfOutput) {
	// HashMap<String,String> resForEvent = finalStatsMap.get(key);
	// buffer.append("\n\tName: " + key);
	// buffer.append("\tMeanValue: " + resForEvent.get("MeanValue"));
	// // buffer.append("\tMedianValue: " + resForEvent.get("MedianValue"));
	// // buffer.append("\tSampleVarianceValue: " + resForEvent.get("sampleVarianceValue"));
	// }
	// }else{ //for all other applications
	// for (Iterator it = finalStatsMap.keySet().iterator(); it.hasNext();) {
	// Object key = it.next();
	// HashMap<String,String> resForEvent = finalStatsMap.get(key);
	// buffer.append("\n\tName: " + key);
	// buffer.append("\tMeanValue: " + resForEvent.get("MeanValue"));
	// buffer.append("\tMedianValue: " + resForEvent.get("MedianValue"));
	// buffer.append("\tSampleVarianceValue: " + resForEvent.get("sampleVarianceValue"));
	// }
	// }
	//
	// buffer.append("\n");
	// buffer.append("Results of Single Experiment: ");
	// buffer.append("\n");
	//
	// for (ExperimentResult experiment : getExperimentsResults()) {
	// // buffer.append(experiment.toStringShort());
	// buffer.append("\t");
	// buffer.append("ID: ");
	// buffer.append(experiment.getId());
	// buffer.append("\t");
	// buffer.append("Duration: ");
	// buffer.append(experiment.getDuraration());
	// //Hack for special application: agentNegotiation
	// // buffer.append(";\t Executed Workflows: " + experiment.getLastValueFor("workflows"));
	// // buffer.append(";\t Earned Money: " + experiment.getLastValueFor("money_amount"));
	// buffer.append("\n");
	//
	//
	// // for (Iterator it = finalStatsMap.keySet().iterator(); it.hasNext();) {
	// // Object key = it.next();
	// // HashMap<String,String> resForEvent = finalStatsMap.get(key);
	// // buffer.append("\n\tName: " + key);
	// // buffer.append("\tMeanValue: " + resForEvent.get("MeanValue"));
	// // buffer.append("\tMedianValue: " + resForEvent.get("MedianValue"));
	// // buffer.append("\tSampleVarianceValue: " + resForEvent.get("sampleVarianceValue"));
	// // }
	//
	// // meanValue += experiment.getDuraration();
	// durations.add(experiment.getDuraration());
	// }
	// //Eval:
	// //list has to be ordered according to the Colt API
	// durations.sort();
	// double durationTimeMean = Descriptive.mean(durations);
	// double durationTimeMedian = Descriptive.median(durations);
	// double durationTimeSampleVariance = Descriptive.sampleVariance(durations, durationTimeMean);
	// buffer.append("\t");
	// buffer.append( "Duration Time Stats:  Mean value: " + durationTimeMean + ", Median value: " + durationTimeMedian + ", Sample Variance Value: " + durationTimeSampleVariance);
	//
	//
	// //Hack: to be able to copy the values easily in a new colt.evaluation
	// buffer.append("\n\t");
	// for(int i=0; i<durations.size(); i++){
	// buffer.append(durations.get(i));
	// buffer.append(",");
	// }
	//
	// buffer.append("\n");
	// return buffer.toString();
	// }

	public String toStringShortNew() {
		DoubleArrayList durations = new DoubleArrayList();

		StringBuffer buffer = new StringBuffer();
		buffer.append("\n");
		buffer.append("Row Number: ");
		buffer.append(getId());
		buffer.append("\n");
		buffer.append("Optimization: Parameter Name and Value: ");
		buffer.append(getOptimizationConfiguration());
		buffer.append("\n\n");
		buffer.append("Cumulated Stats of Observed Events:");

		// Print out cumulated stats for each property of each object instance
		buffer.append(getObjectInstanceData());
		
		buffer.append("\n");
		buffer.append("Results of Single Experiment: ");
		buffer.append("\n");

		for (ExperimentResult experiment : getExperimentsResults()) {
			// buffer.append(experiment.toStringShort());
			buffer.append("\t");
			buffer.append("ID: ");
			buffer.append(experiment.getId());
			buffer.append("\t");
			buffer.append("Duration: ");
			buffer.append(experiment.getDuraration());
			// Hack for special application: agentNegotiation
			// buffer.append(";\t Executed Workflows: " + experiment.getLastValueFor("workflows"));
			// buffer.append(";\t Earned Money: " + experiment.getLastValueFor("money_amount"));
			buffer.append("\n");

			// for (Iterator it = finalStatsMap.keySet().iterator(); it.hasNext();) {
			// Object key = it.next();
			// HashMap<String,String> resForEvent = finalStatsMap.get(key);
			// buffer.append("\n\tName: " + key);
			// buffer.append("\tMeanValue: " + resForEvent.get("MeanValue"));
			// buffer.append("\tMedianValue: " + resForEvent.get("MedianValue"));
			// buffer.append("\tSampleVarianceValue: " + resForEvent.get("sampleVarianceValue"));
			// }

			// meanValue += experiment.getDuraration();
			durations.add(experiment.getDuraration());
		}
		// Eval:
		// list has to be ordered according to the Colt API
		durations.sort();
		double durationTimeMean = Descriptive.mean(durations);
		double durationTimeMedian = Descriptive.median(durations);
		double durationTimeSampleVariance = Descriptive.sampleVariance(durations, durationTimeMean);
		buffer.append("\t");
		buffer.append("Duration Time Stats:  Mean value: " + durationTimeMean + ", Median value: " + durationTimeMedian + ", Sample Variance Value: " + durationTimeSampleVariance);

		// Hack: to be able to copy the values easily in a new colt.evaluation
		buffer.append("\n\t");
		for (int i = 0; i < durations.size(); i++) {
			buffer.append(durations.get(i));
			buffer.append(",");
		}

		buffer.append("\n");
		return buffer.toString();
	}

	private String getObjectInstanceData() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nAccumulated Results for this Row: \n");
		// HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> evaluatedRowData

		for (Iterator<String> it1 = evaluatedRowData.keySet().iterator(); it1.hasNext();) {
			String objectInstanceKey = it1.next();
			HashMap<String, HashMap<String, ArrayList<String>>> objectInstancePropertiesMap = evaluatedRowData.get(objectInstanceKey);

			//get object instance
			buffer.append("****************** Object Instance:  " + objectInstanceKey + "*********************");
//			buffer.append(objectInstanceKey);
			buffer.append(":");					

			for (Iterator<String> it2 = objectInstancePropertiesMap.keySet().iterator(); it2.hasNext();) {
				String objectInstancePropertyKey = it2.next();
				HashMap<String, ArrayList<String>> objectInstancePropertyMap = objectInstancePropertiesMap.get(objectInstancePropertyKey);

				//get properties of object instance
				buffer.append("\n   ");
				buffer.append("####### Stats for Property: " + objectInstancePropertyKey  +"  #######");
//				buffer.append(objectInstancePropertyKey);
				buffer.append(":");
				buffer.append("\n      ");				

				for (Iterator<String> it3 = objectInstancePropertyMap.keySet().iterator(); it3.hasNext();) {
					String objectInstancePropertyStatsTypeKey = it3.next();
					ArrayList<String> objectInstancePropertyStatsTypeList = objectInstancePropertyMap.get(objectInstancePropertyStatsTypeKey);

					//get list of stats for current property
					buffer.append("\n   ");
					buffer.append(objectInstancePropertyStatsTypeKey);
					buffer.append(":");
					buffer.append("\n         ");					
					
					for(String value : objectInstancePropertyStatsTypeList){
						buffer.append(value);						
						buffer.append(",");
					}
					buffer.append("\n");
					
				}
			}
		}

		return buffer.toString();
	}
}
