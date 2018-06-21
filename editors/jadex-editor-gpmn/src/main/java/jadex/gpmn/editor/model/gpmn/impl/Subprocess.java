package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

public class Subprocess extends AbstractElement
{
	/** The process reference. */
	protected String procref;
	
	/** Flag for internal subprocesses. */
	protected boolean internal;
	
	/**
	 *  Creates a new sub-process.
	 */
	public Subprocess(IGpmnModel model)
	{
		super(model);
	}

	/**
	 *  Gets the process reference.
	 *
	 *  @return The process reference.
	 */
	public String getProcref()
	{
		return procref;
	}

	/**
	 *  Sets the process reference.
	 *
	 *  @param process reference The process reference.
	 */
	public void setProcref(String procref)
	{
		this.procref = procref;
	}

	/**
	 *  Gets the internal flag.
	 *
	 *  @return The internal flag.
	 */
	public boolean isInternal()
	{
		return internal;
	}

	/**
	 *  Sets the internal flag.
	 *
	 *  @param internal The internal flag.
	 */
	public void setInternal(boolean internal)
	{
		this.internal = internal;
	}
}
