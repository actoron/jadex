package jadex.bridge.service.types.marshal;

import java.util.List;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Interface for marshalling functionalities.
 */
public interface IMarshalService
{
	//-------- class reference management --------

	/**
	 *  Test if is local reference.
	 */
	@Excluded
	public boolean isLocalReference(Object object);
	
	/**
	 *  Test if is remote reference.
	 */
	@Excluded
	public boolean isRemoteReference(Object object);
	
	/**
	 *  Register a class with reference values for local and remote.
	 */
	@Excluded
	public void setReferenceProperties(Class<?> clazz, boolean localref, boolean remoteref);
	
	/**
	 *  Test if an object is a remote object.
	 */
	@Excluded
	public boolean isRemoteObject(Object object);
	
	/**
	 *  Get the proxy interfaces (empty list if none).
	 */
	@Excluded
	public Class<?>[] getRemoteInterfaces(Object object, ClassLoader cl);
	
	//-------- local clone processors --------
	
	/**
	 *  Get the clone processors.
	 */
	@Excluded
	public List<ITraverseProcessor> getCloneProcessors();
	
	/**
	 *  Add a clone processor.
	 */
	@Excluded
	public void addCloneProcessor(@Reference ITraverseProcessor proc);
		
	/**
	 *  Remove a clone processor.
	 */
	@Excluded
	public void removeCloneProcessor(@Reference ITraverseProcessor proc);

	//-------- remote clone processors --------

//	/**
//	 *  Add a rmi preprocessor.
//	 */
//	public IFuture<Void> addRMIPreProcessor(@Reference IRMIPreprocessor proc);
//		
//	/**
//	 *  Remove a rmi postprocessor.
//	 */
//	public IFuture<Void> removeRMIPreProcessor(@Reference IRMIPreprocessor proc);
//	
//	/**
//	 *  Add a rmi postprocessor.
//	 */
//	public IFuture<Void> addRMIPostProcessor(@Reference IRMIPostprocessor proc);
//		
//	/**
//	 *  Remove a rmi postprocessor.
//	 */
//	public IFuture<Void> removeRMIPostProcessor(@Reference IRMIPostprocessor proc);
//	
//	/**
//	 *  Get the rmi preprocessors.
//	 */
//	public IIntermediateFuture<IRMIPreProcessor> getRMIPreProcessors();
//	
//	/**
//	 *  Get the rmi postprocessors.
//	 */
//	public IIntermediateFuture<IRMIPostProcessor> getRMIPostProcessors();
}
