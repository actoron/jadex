package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

/**
 * AModel Implementation
 * 
 * @author 5Haubeck
 * 
 */
public class AModel extends ADataObject implements IAModel {
	private Modeltype type;
	// private String identifier;

	private IAParameterEnsemble inputParameters = new AParameterEnsemble(
			"Input Parameter");
	private IAParameterEnsemble outputParameters = new AParameterEnsemble(
			"Output Parameter");
	private String path;

	public AModel(String name, Modeltype type, String modelpath,
			IAParameterEnsemble inputParameters,
			IAParameterEnsemble outputParameters) {
		super(name);
		this.type = type;
		this.path = modelpath;
		if (inputParameters != null)
			this.inputParameters = inputParameters;
		if (outputParameters != null)
			this.outputParameters = outputParameters;

		this.inputParameters.setName("Inputparameter");
		this.outputParameters.setName("Outputparameter");
//		view = new AModelView(this);
	}

	public AModel(String name, Modeltype type, String modelpath) {
		this(name, type, modelpath, null, null);
	}

	public AModel() {
		this(
				"Test",
				Modeltype.DesmoJ,
				"/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/desmoJ/models/",
				null, null);
	}

	// ------ Interface IAModel ------

	// Type
	@Override
	public Modeltype getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		synchronized (mutex) {
			this.path = path;
		}
	}

	public void setType(Modeltype type) {
		synchronized (mutex) {
			this.type = type;
			// System.out.println(getName() + ": type=" + type);
		}
		notify(new ADataEvent(this, AConstants.MODEL_TYPE, type));
	}

	// Input

	@Override
	public void addInputParameter(IAParameter parameter) {
		synchronized (mutex) {
			inputParameters.addParameter(parameter);
		}
	}

	@Override
	public void removeInputParameter(String name) {
		synchronized (mutex) {
			inputParameters.removeParameter(name);
		}
	}

	@Override
	public void setInputParameters(IAParameterEnsemble parameters) {
		synchronized (mutex) {
			inputParameters = parameters;
		}

	}

	@Override
	public IAParameter getInputParameter(String name) {
		return inputParameters.getParameter(name);
	}

	@Override
	public IAParameterEnsemble getInputParameters() {
		return inputParameters;
	}

	// Output

	@Override
	public IAParameter getOutputParameter(String name) {
		return outputParameters.getParameter(name);
	}

	@Override
	public IAParameterEnsemble getOutputParameters() {
		return outputParameters;
	}

	@Override
	public void removeOutputParameter(String name) {
		synchronized (mutex) {
			outputParameters.removeParameter(name);
		}
	}

	@Override
	public void setOutputParameters(IAParameterEnsemble parameters) {
		synchronized (mutex) {
			outputParameters = parameters;
		}

	}

	@Override
	public void addOutputParameter(IAParameter parameter) {
		synchronized (mutex) {
			outputParameters.addParameter(parameter);
		}
	}

	@Override
	public void setEditable(Boolean editable) {
		super.setEditable(editable);
		inputParameters.setEditable(editable);
		outputParameters.setEditable(editable);
	}

	@Override
	public void notify(IAEvent event) {
		super.notify(event);

		if (inputParameters != null)
			inputParameters.notify(event);
		if (outputParameters != null)
			outputParameters.notify(event);
	}

	@Override
	public ADataObject clonen() {
		AModel clone = new AModel(name, type, path,
				(IAParameterEnsemble) inputParameters.clonen(),
				(IAParameterEnsemble) outputParameters.clonen());
		clone.setEditable(editable);
		return clone;
	}

	@Override
	public String getModelpath() {
		return path;
	}

	@Override
	public void setModelpath(String path) {
		synchronized (mutex) {
			this.path = path;
		}
	}
}
