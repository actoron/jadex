package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxGeometry;

/**
 * 
 *  Interface for objects providing a plan mode (sequential/parallel)
 *
 */
public interface IPlanModeProvider
{
	/**
	 *  Provides the mode of the associated plan.
	 *  @return Sequential, Parallel or null if undetermined.
	 */
	public String getPlanMode();
	
	/**
	 *  Provides the parent objects geometry.
	 *  @return The geometry.
	 */
	public mxGeometry getGeometry();
}
