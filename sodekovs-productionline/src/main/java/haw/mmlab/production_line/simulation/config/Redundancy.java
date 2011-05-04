/**
 * 
 */
package haw.mmlab.production_line.simulation.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * The container for the redundancy element
 * 
 * @author Peter
 * 
 */
public class Redundancy {
	private Integer min = null;
	private Integer max = null;

	@XmlAttribute(name = "min")
	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	@XmlAttribute(name = "max")
	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

}
