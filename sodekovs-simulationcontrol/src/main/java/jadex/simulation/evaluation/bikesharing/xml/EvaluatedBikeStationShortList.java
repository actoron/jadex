package jadex.simulation.evaluation.bikesharing.xml;

import java.util.ArrayList;

/**
 * Required for storage as XML file. Contains the single results of the stations for ONE time slice
 * 
 * @author Vilenica
 * 
 */
public class EvaluatedBikeStationShortList {

	// Contains the short results of the single stations
	private ArrayList<EvaluatedBikeStationShort> stationDataShort = new ArrayList<EvaluatedBikeStationShort>();
	// Denotes the Time Slice where these results have been observed.
	private int timeSliceKey;
	// HACK: Denotes the experimentDescription of the simulation.
	private String experimentDescription;

	public ArrayList<EvaluatedBikeStationShort> getStationDataShort() {
		return stationDataShort;
	}

	public void setStationDataShort(ArrayList<EvaluatedBikeStationShort> stationDataShort) {
		this.stationDataShort = stationDataShort;
	}

	public int getTimeSliceKey() {
		return timeSliceKey;
	}

	public void setTimeSliceKey(int timeSliceKey) {
		this.timeSliceKey = timeSliceKey;
	}

	public String getExperimentDescription() {
		return experimentDescription;
	}

	public void setExperimentDescription(String experimentDescription) {
		this.experimentDescription = experimentDescription;
	}

}
