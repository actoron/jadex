package deco.distributed.lang.dynamics.convergence;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Description of an agent for the convergence description.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name= "agent")
public class Agent {
	
	private String id = new String();

	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Agent [id=" + id + "]";
	}
}