package jadex.bpmn.model.io;

/**
 *  Interface for visual readers with post-processing.
 *
 */
public interface IPostProcessingVisualModelReader extends IBpmnVisualModelReader
{
	/**
	 *  Performs the post-process.
	 */
	public void postProcess();
}
