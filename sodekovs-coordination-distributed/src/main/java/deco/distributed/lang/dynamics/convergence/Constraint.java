/**
 * 
 */
package deco.distributed.lang.dynamics.convergence;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A constraint for the convergence component.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "constraint")
public class Constraint {

	private String agentId = null;
	
	private String element = null;
	
	private String type = null;
	
	private Integer condition = null;
	
	private Integer threshold = null;

	/**
	 * @return the agentId
	 */
	@XmlAttribute(name = "agent_id")
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the element
	 */
	@XmlAttribute(name = "element")
	public String getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(String element) {
		this.element = element;
	}

	/**
	 * @return the type
	 */
	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the condition
	 */
	@XmlAttribute(name = "condition")
	public Integer getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Integer condition) {
		this.condition = condition;
	}

	/**
	 * @return the threshold
	 */
	@XmlAttribute(name = "threshold")
	public Integer getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Constraint [agentId=" + agentId + ", element=" + element + ", type=" + type + ", condition=" + condition + ", threshold=" + threshold + "]";
	}
}