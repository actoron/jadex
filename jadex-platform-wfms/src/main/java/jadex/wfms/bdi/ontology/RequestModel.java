package jadex.wfms.bdi.ontology;

import java.io.UnsupportedEncodingException;

import jadex.base.fipa.IComponentAction;
import jadex.commons.Base64;

public class RequestModel implements IComponentAction
{
	private String modelName;
	
	private boolean modelNamePath;
	
	/** The encoded model content */
	private String encodedModelContent;
	
	/** The file name */
	private String fileName;
	
	/**
	 *  Get the modelName.
	 *  @return The modelName.
	 */
	public String getModelName()
	{
		return modelName;
	}

	/**
	 *  Set the modelName.
	 *  @param modelName The modelName to set.
	 */
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}
	
	/**
	 *  Get the modelNamePath.
	 *  @return The modelNamePath.
	 */
	public boolean isModelNamePath()
	{
		return modelNamePath;
	}

	/**
	 *  Set the modelNamePath.
	 *  @param modelNamePath The modelNamePath to set.
	 */
	public void setModelNamePath(boolean modelNamePath)
	{
		this.modelNamePath = modelNamePath;
	}

	/**
	 *  Get the modelContent.
	 *  @return The modelContent.
	 */
	public byte[] decodeModelContent()
	{
		try
		{
			return Base64.decode(encodedModelContent.getBytes("US-ASCII"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Set the modelContent.
	 *  @param modelContent The modelContent to set.
	 */
	public void encodeModelContent(byte[] modelContent)
	{
		try
		{
			encodedModelContent = new String(Base64.encode(modelContent), "US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the fileName.
	 *  @return The fileName.
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 *  Set the fileName.
	 *  @param fileName The fileName to set.
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 *  Get the encodedModelContent.
	 *  @return The encodedModelContent.
	 */
	public String getEncodedModelContent()
	{
		return encodedModelContent;
	}

	/**
	 *  Set the encodedModelContent.
	 *  @param encodedModelContent The encodedModelContent to set.
	 */
	public void setEncodedModelContent(String encodedModelContent)
	{
		this.encodedModelContent = encodedModelContent;
	}
}
