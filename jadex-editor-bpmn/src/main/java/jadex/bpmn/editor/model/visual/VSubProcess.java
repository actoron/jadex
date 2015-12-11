package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

import jadex.bpmn.model.MSubProcess;

/**
 *  Visual representation of a subprocess.
 *
 */
public class VSubProcess extends VActivity
{
	/** Pseudo folding flag. */
	protected boolean pseudofolded = false;
	
	public VSubProcess(mxGraph graph)
	{
		super(graph);
	}
	
	/**
	 *  Get the style.
	 */
	public String getStyle()
	{
		String ret = super.getStyle();
		if(MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)getBpmnElement()).getSubprocessType()))
		{
			ret += "_Event";
		}
		else
		{
			if (!isPseudoFolded())
			{
				ret += "_Unfolded";
			}
		}
		
		return ret;
	}

	/**
	 * @return the pseudofolded
	 */
	public boolean isPseudoFolded()
	{
		return pseudofolded;
	}

	/**
	 * @param pseudofolded the pseudofolded to set
	 */
	public void setPseudoFolded(boolean pseudofolded)
	{
		this.pseudofolded = pseudofolded;
	}
	
	
	
	/**
	 *  Checks if connectable.
	 */
//	public boolean isConnectable()
//	{
//		if (SHelper.isEventSubProcess(getBpmnElement()))
//		{
//			return false;
//		}
//		return super.isConnectable();
//	}
}
