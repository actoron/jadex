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

	private Boolean single = null;
	
	private Long startDelay = null;
	
	/**
	 * @return the startDelay
	 */
	@XmlAttribute(name = "startDelay")
	public Long getStartDelay() {
		return startDelay;
	}

	/**
	 * @param startDelay the startDelay to set
	 */
	public void setStartDelay(Long startDelay) {
		this.startDelay = startDelay;
	}

	/**
	 * @return the single
	 */
	@XmlAttribute(name = "single")
	public Boolean getSingle() {
		return single;
	}

	/**
	 * @param single the single to set
	 */
	public void setSingle(Boolean single) {
		this.single = single;
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Adaption [id=" + id + ", answer=" + answer + ", quorum=" + quorum + ", timeout=" + timeout + ", delay=" + delay + ", reset=" + reset + ", single=" + single + ", startDelay="
				+ startDelay + ", realizations=" + (realizations != null ? realizations.subList(0, Math.min(realizations.size(), maxLen)) : null) + ", constraints="
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + ((delay == null) ? 0 : delay.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((quorum == null) ? 0 : quorum.hashCode());
		result = prime * result + ((realizations == null) ? 0 : realizations.hashCode());
		result = prime * result + ((reset == null) ? 0 : reset.hashCode());
		result = prime * result + ((single == null) ? 0 : single.hashCode());
		result = prime * result + ((startDelay == null) ? 0 : startDelay.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adaption other = (Adaption) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		if (delay == null) {
			if (other.delay != null)
				return false;
		} else if (!delay.equals(other.delay))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (quorum == null) {
			if (other.quorum != null)
				return false;
		} else if (!quorum.equals(other.quorum))
			return false;
		if (realizations == null) {
			if (other.realizations != null)
				return false;
		} else if (!realizations.equals(other.realizations))
			return false;
		if (reset == null) {
			if (other.reset != null)
				return false;
		} else if (!reset.equals(other.reset))
			return false;
		if (single == null) {
			if (other.single != null)
				return false;
		} else if (!single.equals(other.single))
			return false;
		if (startDelay == null) {
			if (other.startDelay != null)
				return false;
		} else if (!startDelay.equals(other.startDelay))
			return false;
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
		return true;
	}
}