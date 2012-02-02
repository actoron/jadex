package deco.lang.dynamics.mechanism;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Detailed model of a direct behavior interdependency.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="direct_link_configuration")
public class DirectCausality extends CausalityModel {
	
	//-------- attributes ----------

	/** The configuration of this link. */
	DirectLinkConfiguration configuration;

	//-------- methods -------------
	
	@XmlElement(name="configuration")
	public DirectLinkConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DirectLinkConfiguration configuration) {
		this.configuration = configuration;
	}

}
