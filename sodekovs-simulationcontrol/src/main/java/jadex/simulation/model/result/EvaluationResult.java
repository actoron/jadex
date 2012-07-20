package jadex.simulation.model.result;

import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import sodekovs.util.misc.TimeConverter;

/**
 * Contains only selected aspects of the simulation, e.g. the most important facts and results
 * 
 * @author vilenica
 * 
 */
@XmlRootElement(name = "EvaluationResult")
public class EvaluationResult {

	private int numberOfRows;
	private long experimentsPerRow;
	private long simulationStartime;
	private long simulationDuration;
	private ArrayList<RowResult> rowResults = new ArrayList<RowResult>();

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public long getExperimentsPerRow() {
		return experimentsPerRow;
	}

	public void setExperimentsPerRow(long experimentsPerRow) {
		this.experimentsPerRow = experimentsPerRow;
	}

	public long getSimulationStartime() {
		return simulationStartime;
	}

	public void setSimulationStartime(long simulationStartime) {
		this.simulationStartime = simulationStartime;
	}

	public long getSimulationDuration() {
		return simulationDuration;
	}

	public void setSimulationDuration(long simulationDuration) {
		this.simulationDuration = simulationDuration;
	}

	public ArrayList<RowResult> getRowResults() {
		return rowResults;
	}

	public void setRowResults(ArrayList<RowResult> rowResults) {
		this.rowResults = rowResults;
	}	
	
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("Number of Rows: ");
		buffer.append(getRowResults().size());
		buffer.append("\n");
		buffer.append("Experiments per Row: ");
		buffer.append(getRowResults().get(0).getExperimentsResults().size());
		buffer.append("\n");
		buffer.append("Simulation Startime and Duration: ");
		buffer.append("\n");
		buffer.append(TimeConverter.longTime2DateString(getSimulationStartime()));
		buffer.append(" - ");
		buffer.append(getSimulationDuration());
		buffer.append("\n\n");

		sortRowlist();
		for (RowResult row : getRowResults()) {
//			buffer.append(row.toStringShortOLD());
			buffer.append("\n");
		}

		return buffer.toString();
	}
	
	/**
	 * Returns the list of rows ascendingly ordered.
	 */
	public void sortRowlist(){
		Collections.sort(getRowResults(), new Comparator() {
			public int compare(Object arg0, Object arg1) {
//				return new Long(((RowResult) arg0).getId()).compareTo(new Long(((RowResult) arg1).getId()));
				return Integer.valueOf(((RowResult) arg0).getId()).compareTo(Integer.valueOf(((RowResult) arg1).getId()));
			}
		});
	}
}
