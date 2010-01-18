package jadex.simulation.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Optimization")
public class Optimization {
	
	private Data data;
	private ParameterSweeping parameterSweeping;

	@XmlElement(name="Data")
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@XmlElement(name="ParameterSweeping")
	public ParameterSweeping getParameterSweeping() {
		return parameterSweeping;
	}

	public void setParameterSweeping(ParameterSweeping parameterSweeping) {
		this.parameterSweeping = parameterSweeping;
	}

}
