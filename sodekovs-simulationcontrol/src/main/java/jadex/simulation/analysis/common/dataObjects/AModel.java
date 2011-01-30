package jadex.simulation.analysis.common.dataObjects;

public class AModel implements IAModel {

	private String name;
	private String type;
	// private String identifier;

	private IAParameterCollection inputParameters = new AParameterCollection();
	private IAParameterCollection outputParameters = new AParameterCollection();

	public AModel(String name, String type, IAParameterCollection inputParameters, IAParameterCollection outputParameters) {
		this.name = name;
		this.type = type;
		if (inputParameters != null)
			this.inputParameters = inputParameters;
		if (outputParameters != null)
			this.outputParameters = outputParameters;
	}

	public AModel(String name, String type) {
		this(name, type, null, null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

//	@Override
//	public void setName(String name) {
//		this.name = name;
//	}

//	@Override
//	public void setType(String type) {
//		this.type = type;
//	}

//	@Override
//	public void setInputParameter(IAParameter parameter) {
//		inputParameters.put(parameter.getName(), parameter);
//
//	}

//	@Override
//	public void setOutputParameter(IAParameter parameter) {
//		outputParameters.put(parameter.getName(), parameter);
//	}

//	@Override
//	public void setInputParameters(IAParameterCollection parameters) {
//		this.inputParameters = parameters;
//
//	}

//	@Override
//	public void setOutputParameters(IAParameterCollection parameters) {
//		this.outputParameters = parameters;
//	}

	@Override
	public IAExperimentResult createExperimentResult() {
		IAParameterCollection paramters = new AParameterCollection();
		for (IAParameter outputPara : outputParameters.values()) {
			if (outputPara.isVariable()) paramters.put(outputPara.getName(), outputPara);
		}
		return new AExperimentResult(this, paramters);
	}

	@Override
	public IAExperimentalFrame createExperimentalFrame(IAParameterCollection expParameter) {
		IAParameterCollection paramters = new AParameterCollection();
		for (IAParameter inputPara : inputParameters.values()) {
			if (inputPara.isVariable()) paramters.put(inputPara.getName(), inputPara);
		}
		return new AExperimentalFrame(this, expParameter, paramters);
	}

	@Override
	public IAParameter getInputParameter(String name) {
		return inputParameters.get(name);
	}

	@Override
	public IAParameterCollection getInputParameters() {
		return inputParameters;
	}

	@Override
	public IAParameter getOutputParameter(String name) {
		return outputParameters.get(name);
	}

	@Override
	public IAParameterCollection getOutputParameters() {
		return outputParameters;
	}

}
