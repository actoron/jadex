package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public class AExperimentResult implements IAExperimentResult {
	
	private IAParameterCollection parameters;
	private IAModel model;
	
	/**
	 * Creates a ExperimentResult with the {@link IAParameterCollection}
	 * 
	 * @param model {@link IAModel}
	 * @param parameters {@link IAParameterCollection}
	 */
	public AExperimentResult(IAModel model, IAParameterCollection parameters) {
		this.parameters = parameters;
		this.model = model;
	}
	
	@Override
	public IAModel getModel() {
		return model;
	}

	@Override
	public IAParameter getResultParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public IAParameterCollection getResultParameters() {
		return parameters;
	}

	@Override
	public void setResultParamterValue(String name, Object value) {
		if (parameters.containsKey(name)) parameters.get(name).setValue(value);
	}

	@Override
	public void setResultParamtersValues(Map<String, Object> values) {
		for (Map.Entry<String, Object> parameter : values.entrySet()) {
			setResultParamterValue(parameter.getKey(), parameter.getValue());
		}
		
	}

}
