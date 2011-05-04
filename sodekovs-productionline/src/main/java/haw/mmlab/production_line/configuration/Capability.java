/**
 * 
 */
package haw.mmlab.production_line.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * A capability contains an id, describing the capabilities action.
 * 
 * @author thomas
 */
public class Capability {

	private String id = null;

	public Capability() {
	}

	public Capability(String id) {
		assert id != null : "The id of a capability must not be null!";
		this.id = id;
	}

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
		assert id != null : "The id of a capability must not be null!";
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		Capability other = (Capability) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}

	public static List<Capability> createList(List<String> caps) {
		List<Capability> result = new ArrayList<Capability>();

		for (String cap : caps) {
			Capability capability = new Capability(cap);
			result.add(capability);
		}

		return result;
	}
}
