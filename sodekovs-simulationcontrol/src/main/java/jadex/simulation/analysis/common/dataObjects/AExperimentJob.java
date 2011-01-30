package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public class AExperimentJob implements IAExperimentJob{
	
	static Integer ident = 0;
	
	private IAModel model;
	private IAExperimentalFrame frame;
	private IAExperimentResult result;
	private Integer id;
	
	/**
	 * Creates a experiment job with default results. Can be executed by a ExperiementService
	 * @param model {@link IAModel}
	 * @param frame {@link IAExperimentalFrame}
	 * @param result {@link IAExperimentResult}
	 */
	public AExperimentJob(IAModel model, IAExperimentalFrame frame, IAExperimentResult result) {
		this.model = model;
		this.frame = frame;
		this.result = result;
		this.id = ident;
		id++;
	}
	
	/**
	 * Creates a experiment job with default results. Can be executed by a ExperiementService
	 * @param model {@link IAModel}
	 * @param frame {@link IAExperimentalFrame}
	 */
	public AExperimentJob(IAModel model, IAExperimentalFrame frame) {
		this(model, frame, model.createExperimentResult());
	}
	
	/**
	 * Creates a experiment job with default experimental frame und result. Can be executed by a ExperiementService
	 * @param model {@link IAModel}
	 */
	public AExperimentJob(IAModel model) {
		this(model, model.createExperimentalFrame(new AParameterCollection()), model.createExperimentResult());
	}
	
	@Override
	public IAExperimentalFrame getExperimentalFrame() {
		return frame;
	}

	@Override
	public IAModel getModel() {
		return model;
	}

	@Override
	public IAExperimentResult getExperimentResult() {
		return result;
	}

	@Override
	public void setExperimentResultValue(String name, Object value) {
		result.getResultParameter(name).setValue(value);
	}

	@Override
	public void setExperimentResultValues(Map<String, Object> values) {
		for (Map.Entry<String, Object> value : values.entrySet()) {
			setExperimentResultValue(value.getKey(), value.getValue());
		}
	}

	@Override
	public Integer getID() {
		return id;
	}

}
