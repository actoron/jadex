package deco.distributed.lang.dynamics.mechanism;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Detailed model of a decentralized behavior interdependency.
 * 
 * @author Jan Sudeikat & Thomas Preisler
 * 
 */
@XmlRootElement(name = "realization")
public class DecentralizedCausality extends CausalityModel {

	// ----------constants-----------

	/** The configuration. */
	MechanismConfiguration m_conf;

	Boolean m_active = true;

	// ----------methods-------------

	@XmlElement(name = "mechanism_configuration")
	public MechanismConfiguration getMechanismConfiguration() {
		return m_conf;
	}

	public void setMechanismConfiguration(MechanismConfiguration m_conf) {
		this.m_conf = m_conf;
	}

	@XmlElement(name = "active", defaultValue = "true")
	public Boolean getActive() {
		return m_active;
	}

	public void setActive(Boolean active) {
		this.m_active = active;
	}
}