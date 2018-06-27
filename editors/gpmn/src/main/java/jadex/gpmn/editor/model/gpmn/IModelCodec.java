package jadex.gpmn.editor.model.gpmn;

import java.io.File;
import java.io.IOException;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

/**
 * 
 * Codec for reading/writing model files.
 *
 */
public interface IModelCodec
{
	/** GPMN intermediate model codec. */
	public static final String CODEC_TYPE_GPMN = "gpmn";
	
	/** BDI agent model codec. */
	public static final String CODEC_TYPE_BDI = "bdi";
	
	/**
	 *  Writes the model to a file.
	 * 
	 *  @param file The target file.
	 *  @param graph The visual graph.
	 *  @param model The GPMN intermediate model.
	 */
	public void writeModel(File file, mxGraph graph) throws IOException;
	
	/**
	 *  Loads the model from a file.
	 *  
	 *  @param file The model file.
	 *  @param graph The visual graph.
	 */
	public mxIGraphModel readModel(File file) throws Exception;
}
