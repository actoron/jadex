/**
 * 
 */
package deco.distributed.lang.dynamics.convergence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Description of a realization of the convergence component.
 * 
 * @author Thomas Preisler
 */
public class Realization {

	private String id = null;
	
	private Boolean activate = null;
	
	private List<Entry> entries = new ArrayList<Entry>();

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

	/**
	 * @return the activate
	 */
	@XmlAttribute(name = "activate")
	public Boolean getActivate() {
		return activate;
	}

	/**
	 * @param activate the activate to set
	 */
	public void setActivate(Boolean activate) {
		this.activate = activate;
	}

	/**
	 * @return the entries
	 */
	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "entry")
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Realization [id=" + id + ", activate=" + activate + ", entries=" + (entries != null ? entries.subList(0, Math.min(entries.size(), maxLen)) : null) + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activate == null) ? 0 : activate.hashCode());
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Realization other = (Realization) obj;
		if (activate == null) {
			if (other.activate != null)
				return false;
		} else if (!activate.equals(other.activate))
			return false;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
