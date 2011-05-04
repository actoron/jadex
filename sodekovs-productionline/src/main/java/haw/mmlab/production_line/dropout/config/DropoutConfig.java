/**
 * 
 */
package haw.mmlab.production_line.dropout.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The configuration for the DropOut agent.
 * 
 * @author Peter
 * 
 */
@XmlRootElement(name = "dropout_conf")
public class DropoutConfig {
	private List<Configuration> configurations;

	/**
	 * @return the configurations
	 */
	@XmlElement(name = "configuration")
	public List<Configuration> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations
	 *            the configurations to set
	 */
	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}
}
