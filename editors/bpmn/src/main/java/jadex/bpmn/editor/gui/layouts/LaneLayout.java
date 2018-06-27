package jadex.bpmn.editor.gui.layouts;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.view.mxGraph;

public class LaneLayout extends mxStackLayout
{
	public LaneLayout(mxGraph graph)
	{
		super(graph, false);
		fill = true;
		resizeParent = true;
		
	}
}
