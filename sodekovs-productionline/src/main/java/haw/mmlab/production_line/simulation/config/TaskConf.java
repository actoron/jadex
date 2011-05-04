/**
 * 
 */
package haw.mmlab.production_line.simulation.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author thomas
 * 
 */
public class TaskConf {

	private Integer id = null;
	private Integer noSteps = null;
	private Integer noCaps = null;
	private List<TaskStep> steps = new ArrayList<TaskStep>();

	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the steps
	 */
	@XmlElementWrapper(name = "steps", required = false)
	@XmlElement(name = "step")
	public List<TaskStep> getSteps() {
		return steps;
	}

	/**
	 * @param steps
	 *            the steps to set
	 */
	public void setSteps(List<TaskStep> steps) {
		this.steps = steps;
	}

	/**
	 * @return the noSteps
	 */
	@XmlAttribute(name = "noSteps", required = false)
	public Integer getNoSteps() {
		return noSteps;
	}

	/**
	 * @param noSteps
	 *            the noSteps to set
	 */
	public void setNoSteps(Integer noSteps) {
		this.noSteps = noSteps;
	}

	/**
	 * @return the noCaps
	 */
	@XmlAttribute(name = "noCaps", required = false)
	public Integer getNoCaps() {
		return noCaps;
	}

	/**
	 * @param noCaps
	 *            the noCaps to set
	 */
	public void setNoCaps(Integer noCaps) {
		this.noCaps = noCaps;
	}
}