package jadex.simulation.analysis.service.simulation.inspection;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;

import java.util.Set;

/**
 *  The simulation interface for define a model
 */
public interface IModellInspezierenService	extends IService
{

	/**
	 *  Get the Inputparamter for the Model
	 *  @param name Name of model
	 *  @result IAParameterEnsemble
	 */
	public IFuture getInputParamter(String name);
	
	/**
	 *  Get the Outputparamter for the Model
	 *  @param IAParameterEnsemble
	 */
	public IFuture getOutputParamter(String name);
}
