/**
 * 
 */
package haw.mmlab.production_line.simulation.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * A container for the buffer
 * 
 * @author thomas
 */
public class BufferSize {
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