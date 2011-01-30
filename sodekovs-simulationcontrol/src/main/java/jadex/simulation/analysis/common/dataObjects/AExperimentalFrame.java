package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public class AExperimentalFrame implements IAExperimentalFrame {

	public ABasicParameter replication = new ABasicParameter("replication", new Integer(1), Integer.class, false,false);
	public ABasicParameter onlineVisualisation = new ABasicParameter("onlineVisualisation", Boolean.FALSE, Boolean.class, false,false);
	
	private IAModel model;
	private IAParameterCollection expParameters = new AParameterCollection();
	private IAParameterCollection inputParameter = new AParameterCollection();
	
	public AExperimentalFrame(IAModel model, IAParameterCollection expParameters, IAParameterCollection inputParameter) {
		this.model = model;
		if (expParameters != null)
			this.expParameters = expParameters;
		else
		{
			this.expParameters.put("replication", replication);
			this.expParameters.put("onlineVisualisation", onlineVisualisation);
		}
		if (inputParameter != null)
			this.inputParameter = inputParameter;
	}
	
	@Override
	public IAParameter getExperimentParameter(String name) {
		return expParameters.get(name);
	}

	@Override
	public IAParameterCollection getExperimentParameters() {
		return expParameters;
	}

	@Override
	public IAParameter getInputParameter(String name) {
		return inputParameter.get(name);
	}

	@Override
	public IAParameterCollection getInputParameters() {
		return inputParameter;
	}

	@Override
	public IAModel getModel() {
		return model;
	}

	@Override
	public void setInputParamterValue(String name, Object value) {
		if (inputParameter.containsKey(name)) inputParameter.get(name).setValue(value);

	}

	@Override
	public void setInputParamtersValues(Map<String, Object> values) {
		for (Map.Entry<String, Object> valueEntry : values.entrySet()) {
			setInputParamterValue(valueEntry.getKey(), valueEntry.getValue());
		}
	}

}
