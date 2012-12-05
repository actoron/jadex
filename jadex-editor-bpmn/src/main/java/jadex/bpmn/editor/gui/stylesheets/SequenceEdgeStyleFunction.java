package jadex.bpmn.editor.gui.stylesheets;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;

import java.util.List;

import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle.mxEdgeStyleFunction;
import com.mxgraph.view.mxGraphView;

/**
 *  Style function for sequence edges.
 *
 */
public class SequenceEdgeStyleFunction implements mxEdgeStyleFunction
{
	/**
	 *  Applies the style.
	 */
	public void apply(mxCellState state, mxCellState source,
			mxCellState target, List<mxPoint> points, List<mxPoint> result)
	{
		mxGraphView view = state.getView();
		mxPoint pt = ((points != null && points.size() > 0) ? points.get(0)
				: null);
		mxPoint p0 = state.getAbsolutePoint(0);
		mxPoint pe = state
				.getAbsolutePoint(state.getAbsolutePointCount() - 1);

		if (pt != null)
		{
			pt = view.transformControlPoint(state, pt);
		}

		if (p0 != null)
		{
			source = new mxCellState();
			source.setX(p0.getX());
			source.setY(p0.getY());
		}

		if (pe != null)
		{
			target = new mxCellState();
			target.setX(pe.getX());
			target.setY(pe.getY());
		}

		if (source != null && target != null)
		{
			double l = Math.max(source.getX(), target.getX());
			double r = Math.min(source.getX() + source.getWidth(),
					target.getX() + target.getWidth());

			double x = (pt != null) ? pt.getX() : r + (l - r) / 2;

			double y1 = view.getRoutingCenterY(source);
			double y2 = view.getRoutingCenterY(target);
			
			VActivity sactivity = null;
			MActivity smactivity = null;
			if (source.getCell() instanceof VActivity)
			{
				sactivity = (VActivity) source.getCell();
				if (sactivity.getBpmnElement() != null)
				{
					smactivity = (MActivity) sactivity.getBpmnElement();
				}
				else
				{
					sactivity = null;
				}
			}

			if (pt != null)
			{
				if (pt.getY() >= source.getY()
						&& pt.getY() <= source.getY() + source.getHeight())
				{
					y1 = pt.getY();
				}

				if (pt.getY() >= target.getY()
						&& pt.getY() <= target.getY() + target.getHeight())
				{
					y2 = pt.getY();
				}
			}
			
			
			if (!target.contains(x, y1) && !source.contains(x, y1))
			{
				if (sactivity != null && smactivity.isEventHandler())
				{
					result.add(new mxPoint(source.getCenterX(), source.getY() + source.getHeight()));
					result.add(new mxPoint(source.getCenterX(), y2));
				}
//				else if (sactivity != null && smactivity.getActivityType().contains("Gateway"))
//				{
//					int size = smactivity.getOutgoingSequenceEdges().size();
//					VSequenceEdge vedge = (VSequenceEdge) state.getCell();
//					MSequenceEdge medge = (MSequenceEdge) vedge.getBpmnElement();
//					int pos = smactivity.getOutgoingSequenceEdges().indexOf(medge);
//					
//					if (size == 2)
//					{
//						++pos;
//					}
//					
//					switch(pos % 3)
//					{
//						case 1:
//							result.add(new mxPoint(source.getCenterX(), source.getY()));
//							break;
//						case 2:
//							result.add(new mxPoint(source.getCenterX(), source.getY() + source.getHeight()));
//							break;
//						case 0:
//						default:
//							result.add(new mxPoint(source.getCenterX(), source.getCenterY()));
//							
//					}
//					result.add(new mxPoint(source.getCenterX(), y2));
//					
//				}
				else
				{
					result.add(new mxPoint(x, y1));
				}
			}

			if (!target.contains(x, y2) && !source.contains(x, y2))
			{
				result.add(new mxPoint(x, y2));
			}

			if (result.size() == 1)
			{
				if (pt != null)
				{
					result.add(new mxPoint(x, pt.getY()));
				}
				else
				{
					double t = Math.max(source.getY(), target.getY());
					double b = Math.min(source.getY() + source.getHeight(),
							target.getY() + target.getHeight());

					result.add(new mxPoint(x, t + (b - t) / 2));
				}
			}
		}
	}
}
