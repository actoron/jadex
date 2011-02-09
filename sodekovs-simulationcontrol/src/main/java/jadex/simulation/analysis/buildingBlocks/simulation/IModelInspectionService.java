package jadex.simulation.analysis.buildingBlocks.simulation;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

import java.util.Set;

/**
 *  The simulation interface for define a model
 */
public interface IModelInspectionService	extends IService
{

	/**
	 *  get ModelParamter for Model
	 *  @param name Name of model
	 */
	public IFuture inputParamter(String name);
	
	/**
	 *  get ResultParamter for Model
	 *  @param name Name of model
	 */
	public IFuture outputParamter(String name);
	
	/**
	 * Return the model which the service support
	 * @return modeltypes as Set
	 */
	public Set<String> supportedModels();
}
