package haw.mmlab.production_line.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Autonomous robot, i.e. production machine, model.
 * 
 * @author thomas
 */
@XmlRootElement(name = "robot")
public class Robot extends Agent {

	private int bufferSize = 0;

	/**
	 * @return the bufferSize
	 */
	@XmlElement(name = "buffer_size")
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize
	 *            the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Robot ["
				+ (getAgentId() != null ? "getAgentId()=" + getAgentId() + ", " : "")
				+ (getCapabilities() != null ? "getCapabilities()="
						+ getCapabilities().subList(0, Math.min(getCapabilities().size(), maxLen)) + ", " : "")
				+ (getRoles() != null ? "getRoles()=" + getRoles().subList(0, Math.min(getRoles().size(), maxLen)) : "")
				+ "]";
	}
}