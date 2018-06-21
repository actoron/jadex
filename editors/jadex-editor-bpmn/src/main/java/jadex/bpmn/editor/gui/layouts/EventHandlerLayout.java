package jadex.bpmn.editor.gui.layouts;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.model.MActivity;

/**
 *  Layout for ordering event handlers.
 *
 */
public class EventHandlerLayout extends mxGraphLayout
{
	/**
	 * Creates a new event handler layout.
	 * @param graph The graph.
	 */
	public EventHandlerLayout(mxGraph graph)
	{
		super(graph);
	}
	
	/**
	 *  Called when a cell is moved.
	 */
	public void moveCell(Object cell, double x, double y)
	{
		super.moveCell(cell, x, y);
		execute(cell);
	}
	
	/**
	 *  Executes the layout.
	 */
	public void execute(Object parent)
	{
		VElement vparent = (VElement) parent;
		List<VActivity> evthandlers = null;
		for (int i = 0; i < vparent.getChildCount(); ++i)
		{
			mxICell cobj = vparent.getChildAt(i);
			if (cobj instanceof VActivity)
			{
				VActivity vactivity = (VActivity) cobj;
				MActivity mactivity = (MActivity) vactivity.getBpmnElement();
				if (mactivity != null && mactivity.isEventHandler())
				{
					if (evthandlers == null)
					{
						evthandlers = new ArrayList<VActivity>();
					}
					
					evthandlers.add(vactivity);
				}
			}
		}
		
		if (evthandlers != null && evthandlers.size() > 0)
		{
			//double pw = graph.getBoundingBox(vparent).getWidth();
			double pw = vparent.getGeometry().getWidth();
			Dimension evtsize = new Dimension(BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY"));
//			double vpos = graph.getBoundingBox(vparent).getHeight() - 0.5 * evtsize.height;
			double vpos = vparent.getGeometry().getHeight() - 0.5 * evtsize.height - 1;
			double dist = evtsize.width * 1.15;// 1.5;
//			System.out.println(dist);
			if (pw > 0.0)
			{
				double fulldist = dist * evthandlers.size() + dist;
				if (fulldist > pw)
				{
					dist = dist * (pw / fulldist);
				}
			}
			else
			{
				dist = 0.0;
			}
			
//			System.out.println(dist);
			int count = 0;
			for (VActivity vactivity : evthandlers)
			{
				mxGeometry geo = vactivity.getGeometry();
				geo.setWidth(evtsize.width);
				geo.setHeight(evtsize.height);
				geo.setX(dist * 0.2 + dist * count++);
				geo.setY(vpos);
				graph.getView().invalidate(vactivity);
			}
		}
	}
}
