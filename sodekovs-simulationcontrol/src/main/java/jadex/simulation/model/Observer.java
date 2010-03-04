package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Vilenica
 *
 */
@XmlRootElement(name="Observer")
public class Observer {
	
	private Data data;
	private Evaluation evaluation;	
	private Filter filter;	
	
	@XmlElement(name="Filter")
	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@XmlElement(name="Data")
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@XmlElement(name="Evaluation")
	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
}
