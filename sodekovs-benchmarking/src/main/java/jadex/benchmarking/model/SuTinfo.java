/**
 * 
 */
package jadex.benchmarking.model;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

import java.util.ArrayList;

/**
 * @author vilenica
 *
 */
public class SuTinfo {
	// Ordered list (by start time) of sequences
	private ArrayList<Sequence> sortedSequenceList = null;
	// Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	// Exta of System Under Test
	private IExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	//AdaptationAnalysis
	private AdaptationAnalysis adaptationAnalysis = null;
	
	/**
	 * 	
	 * @param sortedSequenceList
	 * @param sutCID
	 * @param sutExta
	 * @param sutSpace
	 */
	public SuTinfo(ArrayList<Sequence> sortedSequenceList, AdaptationAnalysis adaptationAnalysis, IComponentIdentifier sutCID, IExternalAccess sutExta, AbstractEnvironmentSpace sutSpace) {
		super();
		this.sortedSequenceList = sortedSequenceList;
		this.sutCID = sutCID;
		this.sutExta = sutExta;
		this.sutSpace = sutSpace;
		this.adaptationAnalysis = adaptationAnalysis;
	}
	
	public ArrayList<Sequence> getSortedSequenceList() {
		return sortedSequenceList;
	}
	public void setSortedSequenceList(ArrayList<Sequence> sortedSequenceList) {
		this.sortedSequenceList = sortedSequenceList;
	}
	public IComponentIdentifier getSutCID() {
		return sutCID;
	}
	public void setSutCID(IComponentIdentifier sutCID) {
		this.sutCID = sutCID;
	}
	public IExternalAccess getSutExta() {
		return sutExta;
	}
	public void setSutExta(IExternalAccess sutExta) {
		this.sutExta = sutExta;
	}
	public AbstractEnvironmentSpace getSutSpace() {
		return sutSpace;
	}
	public void setSutSpace(AbstractEnvironmentSpace sutSpace) {
		this.sutSpace = sutSpace;
	}	
	public AdaptationAnalysis getAdaptationAnalysis() {
		return adaptationAnalysis;
	}

	public void setAdaptationAnalysis(AdaptationAnalysis adaptationAnalysis) {
		this.adaptationAnalysis = adaptationAnalysis;
	}

	public String toString(){
		return "SuTinfo:  " + sutCID + " - " + sutExta + " - " + sutSpace; 
	}
}
