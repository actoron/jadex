package jadex.bpmn.model.io;

import java.io.PrintStream;

/**
 *  Interface for reader of the visual part of BPMN models.
 *
 */
public interface IBpmnVisualModelWriter
{
	/**
	 *  Reads the visual model.
	 *  
	 *  @param out The output.
	 *  @param vmodel The visual model.
	 */
	public abstract void writeVisualModel(PrintStream out);
}
