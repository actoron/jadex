/**
 * 
 */
package deco.distributed.lang.dynamics.convergence;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A key/value pair property.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "entry")
public class Entry {

	private String key = null;
	
	private String value = null;

	/**
	 * @return the key
	 */
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Entry [key=" + key + ", value=" + value + "]";
	}
}