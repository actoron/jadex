package deco.lang.dynamics.causalities;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import deco.lang.dynamics.properties.SystemProperty;
import deco4mas.annotation.agent.CoordinationAnnotation.CoordinationType;

/**
 * The representations causal relationships between MAS properties.<br>
 * Links can be:<br>
 * - direct: influencing each other directly, e.g. by a fixed rate.<br>
 * - decentralized: realized by a decentralized coordination mechanism.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement
public class CausalLink {
	
	//----------attributes----------

	/** The link identifier. */
	private String name;
	
	/** The origination node (identifiers). */
	private ArrayList<String> from = new ArrayList<String>();
	
	/** The destination node (identifiers). */
	private ArrayList<String> to = new ArrayList<String>();
	
	/** The type of link (positive/negative). */
	private CoordinationType type;
	
	/** The interaction rate. */
	private Double rate;
	
	/** A identifier of the configuration of the causality enactment. */
	private String realization;
	
	/** Whether the link represents a causal dependency or a flow (source to sink). */
	private Boolean flow;
	
	//----------methods-------------
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name="froms")
	@XmlElement(name="from")
	public ArrayList<String> getFrom() {
		return from;
	}

	public void setFrom(ArrayList<String> from) {
		this.from = from;
	}
	
	public void addFrom(SystemProperty prop1){
		this.from.add(prop1.getName());
	}

	@XmlElementWrapper(name="tos")
	@XmlElement(name="to")
	public ArrayList<String> getTo() {
		return to;
	}

	public void setTo(ArrayList<String> to) {
		this.to = to;
	}
	
	public void addTo(String p){
		this.to.add(p);
	}
	
	public void addTo(SystemProperty prop){
		this.to.add(prop.getName());
	}

	@XmlAttribute(name="type")
	public CoordinationType getType() {
		return type;
	}

	public void setType(CoordinationType type) {
		this.type = type;
	}
	
	@XmlAttribute(name="rate")
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	@XmlAttribute(name="realization")
	public String getRealization(){
		return realization;
	}
	
	public void setRealization(String realization){
		this.realization = realization;
	}

	@XmlAttribute(name="flow")
	public Boolean getFlow() {
		return flow;
	}

	public void setFlow(Boolean flow) {
		this.flow = flow;
	}
	
}