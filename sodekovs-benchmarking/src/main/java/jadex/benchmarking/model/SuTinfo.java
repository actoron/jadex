/**
 * 
 */
package jadex.benchmarking.model;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bridge.IComponentIdentifier;

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
	private IApplicationExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	
	/**
	 * 	
	 * @param sortedSequenceList
	 * @param sutCID
	 * @param sutExta
	 * @param sutSpace
	 */
	public SuTinfo(ArrayList<Sequence> sortedSequenceList, IComponentIdentifier sutCID, IApplicationExternalAccess sutExta, AbstractEnvironmentSpace sutSpace) {
		super();
		this.sortedSequenceList = sortedSequenceList;
		this.sutCID = sutCID;
		this.sutExta = sutExta;
		this.sutSpace = sutSpace;
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
	public IApplicationExternalAccess getSutExta() {
		return sutExta;
	}
	public void setSutExta(IApplicationExternalAccess sutExta) {
		this.sutExta = sutExta;
	}
	public AbstractEnvironmentSpace getSutSpace() {
		return sutSpace;
	}
	public void setSutSpace(AbstractEnvironmentSpace sutSpace) {
		this.sutSpace = sutSpace;
	}
	public String toString(){
		return "SuTinfo:  " + sutCID + " - " + sutExta + " - " + sutSpace; 
	}
}
