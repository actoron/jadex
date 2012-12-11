/**
 * 
 */
package deco.distributed.lang.dynamics.convergence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Description of a possible adaption. An adaption consists of a list of affected realizations, a list of constraints and some other properties.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "adaption")
public class Adaption {

	private String id = null;

	private Integer answer = null;

	private Double quorum = null;

	private Long timeout = null;

	private Long delay = null;

	private Boolean reset = null;

	private List<Realization> realizations = new ArrayList<Realization>();

	private List<Constraint> constraints = new ArrayList<Constraint>();

	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the answer
	 */
	@XmlAttribute(name = "answers")
	public Integer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	/**
	 * @return the quorum
	 */
	@XmlAttribute(name = "quorum")
	public Double getQuorum() {
		return quorum;
	}

	/**
	 * @param quorum
	 *            the quorum to set
	 */
	public void setQuorum(Double quorum) {
		this.quorum = quorum;
	}

	/**
	 * @return the timeout
	 */
	@XmlAttribute(name = "timeout")
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the delay
	 */
	@XmlAttribute(name = "delay")
	public Long getDelay() {
		return delay;
	}

	/**
	 * @param delay
	 *            the delay to set
	 */
	public void setDelay(Long delay) {
		this.delay = delay;
	}

	/**
	 * @return the reset
	 */
	@XmlAttribute(name = "reset")
	public Boolean getReset() {
		return reset;
	}

	/**
	 * @param reset
	 *            the reset to set
	 */
	public void setReset(Boolean reset) {
		this.reset = reset;
	}

	/**
	 * @return the realizations
	 */
	@XmlElementWrapper(name = "realizations")
	@XmlElement(name = "realization")
	public List<Realization> getRealizations() {
		return realizations;
	}

	/**
	 * @param realizations
	 *            the realizations to set
	 */
	public void setRealizations(List<Realization> realizations) {
		this.realizations = realizations;
	}

	/**
	 * @return the constraints
	 */
	@XmlElementWrapper(name = "constraints")
	@XmlElement(name = "constraint")
	public List<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Adaption [id=" + id + ", answer=" + answer + ", quorum=" + quorum + ", timeout=" + timeout + ", delay=" + delay + ", reset=" + reset + ", realizations="
				+ (realizations != null ? realizations.subList(0, Math.min(realizations.size(), maxLen)) : null) + ", constraints="
				+ (constraints != null ? constraints.subList(0, Math.min(constraints.size(), maxLen)) : null) + "]";
	}

	/**
	 * Gets all the {@link Constraint}s which reference the given agent id.
	 * 
	 * @param agentId
	 *            the given agent id.
	 * @return all the {@link Constraint}s which reference the given agent id
	 */
	public List<Constraint> getConstraints(String agentId) {
		List<Constraint> constraints = new ArrayList<Constraint>();

		for (Constraint constraint : this.constraints) {
			if (constraint.getAgentId().equals(agentId)) {
				constraints.add(constraint);
			}
		}

		return constraints;
	}
}