package deco4mas.annotation.agent;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Constraint element. <br>
 * Defines when the coordination should trigger element instantiation. 
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="constraint")
public class CoordinationContraint {
	
	//----------attributes----------
	
	/** The (optional) condition when the coordination should be triggered. */
	private Condition condition;
	
	/** Restrict that the element is not activated if the inhibited elements are active. */
	private ArrayList<CoordinationInhibition> inhibitions;

	//----------constructors----------

//	public CoordinationContraint(int cardinality) {
//		super();
//		this.inhibitions = new ArrayList<CoordinationInhibition>();
//	}
	
	public CoordinationContraint() {
		super();
		this.inhibitions = new ArrayList<CoordinationInhibition>();
	}	

//	public CoordinationContraint(int cardinality,
//			ArrayList<CoordinationInhibition> inhibitions) {
//		super();
//		this.inhibitions = inhibitions;
//	}

	//----------methods----------
	
	@XmlElementWrapper(name="inhibited")
	@XmlElement(name="by")
	public ArrayList<CoordinationInhibition> getInhibitions() {
		return inhibitions;
	}

	public void setInhibitions(ArrayList<CoordinationInhibition> inhibitions) {
		this.inhibitions = inhibitions;
	}
	
	public void addInhibition(CoordinationInhibition ci){
		this.inhibitions.add(ci);
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
}
