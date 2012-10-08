package jadex.backup.resource;

import jadex.bridge.service.types.deployment.FileData;

/**
 *  Struct for posting information about the progress of
 *  ongoing backup processes.
 */
public class BackupEvent
{
	//-------- attributes --------
	
	/** The event type. */
	protected String	type;
	
	/** The corresponding file. */
	protected FileData	file;
	
	/** The current progress state of the file (0..1) or -1 if atomic event. */
	protected double	progress;
	
	//-------- constructors --------
	
	/**
	 *  Create a new backup event.
	 */
	public BackupEvent()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a new backup event.
	 */
	public BackupEvent(String type, FileData file, double progress)
	{
		this.type	= type;
		this.file	= file;
		this.progress	= progress;
	}

	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Get the file.
	 */
	public FileData getFile()
	{
		return file;
	}
	
	/**
	 *  Get the progress value.
	 */
	public double getProgress()
	{
		return progress;
	}

	/**
	 *  Set the type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Set the file.
	 */
	public void setFile(FileData file)
	{
		this.file = file;
	}

	/**
	 *  Set the progress value.
	 */
	public void setProgress(double progress)
	{
		this.progress = progress;
	}
	
	public String toString()
	{
		return type + ": " + file.getPath() + (progress>=0 ? progress<1 ? " ("+(int)(progress*100)+"%)" : " (done)" : ""); 
	}
}
