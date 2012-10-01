package jadex.wfms.service;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceIdentifier;

/**
 *  Contains information about the location of a process model.
 *
 */
public class ProcessResourceInfo
{
	/** The model repository service providing the process model. */
	protected IServiceIdentifier repositoryid;
	
	/** The execution service supporting the process model. */
	protected IServiceIdentifier executionid;
	
	/** The resource identifier for the resource containing the model. */
	protected IResourceIdentifier rid;
	
	/** The local path of the model. */
	protected String path;
	
	/** 
	 *  Creates a new process model info.
	 *  
	 *  @param repid The repository service providing the model.
	 *  @param exid The execution service supporting the model.
	 *  @param rid The resource identifier for the resource containing the model.
	 *  @param path The model path.
	 */
	public ProcessResourceInfo(IServiceIdentifier repid, IServiceIdentifier exid, IResourceIdentifier rid, String path)
	{
		this.repositoryid = repid;
		this.executionid = exid;
		this.rid = rid;
		this.path = path;
	}

	/**
	 *  Gets the id of the model repository service.
	 *
	 *  @return The repository id.
	 */
	public IServiceIdentifier getRepositoryId()
	{
		return repositoryid;
	}
	
	/**
	 *  Gets the id of the model execution service.
	 *
	 *  @return The execution service id.
	 */
	public IServiceIdentifier getExecutionServiceId()
	{
		return executionid;
	}

	/**
	 *  Gets the ID of the resource containing the model.
	 *
	 *  @return The ID.
	 */
	public IResourceIdentifier getResourceId()
	{
		return rid;
	}

	/**
	 *  Gets the path of the model.
	 *
	 *  @return The path of the model.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 *  Sets the ID of the resource containing the model.
	 *
	 *  @param repositoryid The repository id.
	 */
	public void setRepositoryId(IServiceIdentifier repositoryid)
	{
		this.repositoryid = repositoryid;
	}
	
	/**
	 *  Sets the ID of the execution service supporting the model.
	 *
	 *  @param executionid The execution service id.
	 */
	public void setExecutionServiceId(IServiceIdentifier executionid)
	{
		this.executionid = executionid;
	}

	/**
	 *  Sets the rid.
	 *
	 *  @param rid The rid.
	 */
	public void setResourceId(IResourceIdentifier rid)
	{
		this.rid = rid;
	}

	/**
	 *  Sets the path of the model.
	 *
	 *  @param path The path.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	
}
