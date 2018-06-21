package jadex.gpmn.editor.model.gpmn.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IModelCodec;

public abstract class AbstractModelCodec implements IModelCodec
{
	/** The indentation string for a single indentation. */
	protected static final String INDENTATION_STRING = "  ";
	
	/** The GPMN intermediate model. */
	protected IGpmnModel model;
	
	/**
	 *  Creates a model codec.
	 */
	public AbstractModelCodec(IGpmnModel model)
	{
		this.model = model;
	}
	
	/**
	 *  Writes the model to a file.
	 * 
	 *  @param file The target file.
	 *  @param graph The visual graph.
	 *  @param model The GPMN intermediate model.
	 */
	public abstract void writeModel(File file, mxGraph graph) throws IOException;
	
	/**
	 *  Loads the model from a file.
	 *  
	 *  @param file The model file.
	 *  @param graph The visual graph.
	 */
	public abstract mxIGraphModel readModel(File file) throws Exception;
	
	/**
	 *  Prints a line with the given indentation.
	 *  
	 *  @param ps The print stream.
	 *  @param num The indentation.
	 *  @param line The line of text.
	 */
	protected void printlnIndent(PrintStream ps, int num, String line)
	{
		ps.println(getIndent(num).append(line).toString());
	}
	
	/**
	 *  Prints text with the given indentation.
	 *  
	 *  @param ps The print stream.
	 *  @param num The indentation.
	 *  @param text The text.
	 */
	protected void printIndent(PrintStream ps, int num, String text)
	{
		ps.print(getIndent(num).append(text).toString());
	}
	
	/**
	 *  Returns the indentation string.
	 *  
	 *  @param num The indentation.
	 *  @return The string.
	 */
	protected StringBuilder getIndent(int num)
	{
		StringBuilder sb = new StringBuilder();
		while (num-- > 0)
		{
			sb.append(INDENTATION_STRING);
		}
		return sb;
	}
}
