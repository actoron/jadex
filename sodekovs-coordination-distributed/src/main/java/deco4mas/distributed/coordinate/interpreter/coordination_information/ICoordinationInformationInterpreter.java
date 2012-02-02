package deco4mas.distributed.coordinate.interpreter.coordination_information;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.IPerceptProcessor;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * This interface specifies the "Coordination Information Interpreter" component.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 * 
 *         Interface for CoordinationInformationInterpreter, i.e. a "V2 Percept Processor".
 */
public interface ICoordinationInformationInterpreter extends IPerceptProcessor {

	public void processPercept(IEnvironmentSpace space, String type, Object percept, IComponentDescription component, ISpaceObject avatar);
}