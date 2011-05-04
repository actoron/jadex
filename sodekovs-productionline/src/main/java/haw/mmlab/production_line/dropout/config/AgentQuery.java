/**
 * 
 */
package haw.mmlab.production_line.dropout.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Peter
 * 
 */
public class AgentQuery {
	private String category;
	private List<String> agents;
	private List<String> capabilities;
	private List<String> activeCapabilities;
	private Integer random;

	@XmlElement(name = "category")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@XmlElementWrapper(name = "agents")
	@XmlElement(name = "agent")
	public List<String> getAgents() {
		return agents;
	}

	public void setAgents(List<String> agents) {
		this.agents = agents;
	}

	@XmlElementWrapper(name = "capabilities")
	@XmlElement(name = "capability")
	public List<String> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<String> capabilities) {
		this.capabilities = capabilities;
	}

	@XmlElementWrapper(name = "active_capabilities")
	@XmlElement(name = "capability")
	public List<String> getActiveCapabilities() {
		return activeCapabilities;
	}

	public void setActiveCapabilities(List<String> activeCapabilities) {
		this.activeCapabilities = activeCapabilities;
	}

	@XmlElement(name = "random")
	public Integer getRandom() {
		return random;
	}

	public void setRandom(Integer random) {
		this.random = random;
	}
}
