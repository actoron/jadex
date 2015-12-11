package jadex.bpmn.editor.gui.stylesheets;

import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle.mxEdgeStyleFunction;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.model.MActivity;

/**
 *  Style function for sequence edges.
 *
 */
public class SequenceEdgeStyleFunction implements mxEdgeStyleFunction
{
	/** Offset adjustment for non-aligned axis */
	protected static final double NON_ALIGNED_AXIS_OFFSET_ADJUSTMENT = 0.2;
	
	/**
	 *  Applies the style.
	 */
	public void apply(mxCellState state, mxCellState source, mxCellState target, List<mxPoint> points, List<mxPoint> result)
	{
		VActivity sourcenode = source != null? getVActivity(source.getCell()) : null;
		VActivity targetnode = target != null? getVActivity(target.getCell()) : null;
		
		if (sourcenode == null || targetnode == null)
		{
			return;
		}
		
		double scale = state.getView().getScale();
		
		double adjedgedist = GuiConstants.MIN_EDGE_DIST * scale;
		mxGeometry sgeo = sourcenode.getGeometry();
		mxPoint spos = new mxPoint(sgeo.getX() * scale, sgeo.getY() * scale);
		if (sourcenode.getParent() != null)
		{
			spos = adjustPoint(state.getView().getGraph(), sourcenode.getParent(), spos);
		}
		sgeo = new mxGeometry(spos.getX(), spos.getY(), sgeo.getWidth() * scale, sgeo.getHeight() * scale);
		mxGeometry tgeo = targetnode.getGeometry();
		mxPoint tpos = new mxPoint(tgeo.getX() * scale, tgeo.getY() * scale);
		if (targetnode.getParent() != null)
		{
			tpos = adjustPoint(state.getView().getGraph(), targetnode.getParent(), tpos);
		}
//		tpos.setX(tpos.getX() * scale);
//		tpos.setY(tpos.getY() * scale);
		tgeo = new mxGeometry(tpos.getX(), tpos.getY(), tgeo.getWidth() * scale, tgeo.getHeight() * scale);
		
		double offsets = 0.0;
		if (source.getCell() instanceof VOutParameter)
		{
//			offsets = -((VOutParameter) source.getCell()).getGeometry().getY() + sourcenode.getGeometry().getHeight() * 0.5;
//			mxPoint opos = new mxPoint(((VOutParameter) source.getCell()).getGeometry().getX() * scale, ((VOutParameter) source.getCell()).getGeometry().getY() * scale);
			offsets = -sgeo.getHeight() * 0.5 + ((VOutParameter) source.getCell()).getGeometry().getCenterY() * scale;
		}
		double offsett = 0.0;
		if (target.getCell() instanceof VInParameter)
		{
			offsett = -tgeo.getHeight() * 0.5 + ((VInParameter) target.getCell()).getGeometry().getCenterY() * scale;
//			offsett = -tgeo.getHeight() * 0.5 + ((VInParameter) target.getCell()).getGeometry().getY();
		}
		
		if (points == null || points.size() == 0)
		{
			//int gwoutedgecount = 0;
			boolean gw = false;
			
			if (sourcenode.getBpmnElement() != null &&
				((MActivity) sourcenode.getBpmnElement()).getActivityType().startsWith("Gateway"))
			{
				gw = true;
//				VActivity vact = (VActivity) source.getCell();
//				MActivity mact = (MActivity) vact.getBpmnElement();
//				gwoutedgecount += mact.getOutgoingSequenceEdges() != null? mact.getOutgoingSequenceEdges().size() : 0;
//				gwoutedgecount += mact.getOutgoingMessagingEdges() != null? mact.getOutgoingMessagingEdges().size() : 0;
			}
			
			if (gw &&
				(tgeo.getCenterY() > sgeo.getY() + sgeo.getHeight() ||
				 tgeo.getCenterY() < sgeo.getY()))
			{
				if (tgeo.getX() > sgeo.getCenterX())
				{
					result.add(new mxPoint(sgeo.getCenterX(), tgeo.getCenterY()));
				}
				else
				{
					double ydist = tgeo.getY() - (sgeo.getY() + sgeo.getHeight());
					if (ydist > adjedgedist)
					{
						double cy = sgeo.getY() + sgeo.getHeight() + ydist * 0.5;
						result.add(new mxPoint(sgeo.getCenterX(), cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
					}
					else
					{
						ydist = sgeo.getY() - (tgeo.getY() + tgeo.getHeight());
						if (ydist > adjedgedist)
						{
							double cy = sgeo.getY() - ydist * 0.5;
							result.add(new mxPoint(sgeo.getCenterX(), cy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, cy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
						}
						else
						{
							// implement
						}
					}
				}
			}
			else if (sourcenode.getBpmnElement() != null &&
					 ((MActivity) sourcenode.getBpmnElement()).isEventHandler() &&
					 sourcenode.getParent() != null)
			{
				mxGeometry pgeo = sourcenode.getParent().getGeometry();
				mxPoint ppos = new mxPoint(pgeo.getX() * scale, pgeo.getY() * scale);
				if (sourcenode.getParent().getParent() != null)
				{
					ppos = adjustPoint(state.getView().getGraph(), sourcenode.getParent().getParent(), ppos);
				}
				pgeo = new mxGeometry(ppos.getX(), ppos.getY(), pgeo.getWidth() * scale, pgeo.getHeight() * scale);
				
				if (tgeo.getCenterY() > sgeo.getY() + sgeo.getHeight())
				{
					if (tgeo.getX() > sgeo.getX() + sgeo.getWidth())
					{
						result.add(new mxPoint(sgeo.getCenterX(), tgeo.getCenterY()));
					}
					else
					{
						if (tgeo.getY() - adjedgedist > sgeo.getY() + sgeo.getHeight())
						{
							double ydist = tgeo.getY() - (sgeo.getY() + sgeo.getHeight());
							double cy = sgeo.getY() + sgeo.getHeight() + ydist * 0.5;
							result.add(new mxPoint(sgeo.getCenterX(), cy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, cy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
						}
						else
						{
							double maxy = tgeo.getY() + tgeo.getHeight() + adjedgedist;
							result.add(new mxPoint(sgeo.getCenterX(), maxy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, maxy));
							result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
						}
					}
				}
				else
				{
					if (tgeo.getX() - adjedgedist > pgeo.getX() + pgeo.getWidth())
					{
						double maxy = sgeo.getY() + sgeo.getHeight() + adjedgedist;
						double cx = tgeo.getX() - (tgeo.getX() - pgeo.getX() - pgeo.getWidth()) * 0.5;
						result.add(new mxPoint(sgeo.getCenterX(), maxy));
						result.add(new mxPoint(cx, maxy));
						result.add(new mxPoint(cx, tgeo.getCenterY()));
					}
					else
					{
						double maxy = sgeo.getY() + sgeo.getHeight() + adjedgedist;
						double minx = Math.min(pgeo.getX() - adjedgedist,
											   tgeo.getX() - adjedgedist);
						result.add(new mxPoint(sgeo.getCenterX(), maxy));
						result.add(new mxPoint(minx, maxy));
						result.add(new mxPoint(minx, tgeo.getCenterY()));
					}
				}
			}
			else if (tgeo.getX() > sgeo.getX() + sgeo.getWidth())
			{
				double cx = sgeo.getX() +  sgeo.getWidth();
				cx += (tgeo.getX() - cx) * 0.5;
//				double xshift = NON_ALIGNED_AXIS_OFFSET_ADJUSTMENT * tgeo.getY() > sgeo.getY()? offsets : -offsets;
				result.add(new mxPoint(cx, sgeo.getCenterY() + offsets));
				result.add(new mxPoint(cx, tgeo.getCenterY() + offsett));
			}
			else
			{
				if (sgeo.getY() > tgeo.getY())
				{
					double ydist = sgeo.getY() - (tgeo.getY() + tgeo.getHeight());
					if (ydist > adjedgedist)
					{
						double cy = sgeo.getY() - ydist * 0.5;
						result.add(new mxPoint(sgeo.getX() + sgeo.getWidth() + adjedgedist, sgeo.getCenterY()));
						result.add(new mxPoint(sgeo.getX() + sgeo.getWidth() + adjedgedist, cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
					}
					else
					{
						double maxx = Math.max(sgeo.getX() + sgeo.getWidth() + adjedgedist,
								   			   tgeo.getX() + tgeo.getWidth() + adjedgedist);
						double miny = Math.min(sgeo.getY() - adjedgedist,
								   			   tgeo.getY() - adjedgedist);
						result.add(new mxPoint(maxx, sgeo.getCenterY()));
						result.add(new mxPoint(maxx, miny));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, miny));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
					}
				}
				else
				{
					double ydist = tgeo.getY() - (sgeo.getY() + sgeo.getHeight());
					if (ydist > adjedgedist)
					{
						double cy = tgeo.getY() - ydist * 0.5;
						result.add(new mxPoint(sgeo.getX() + sgeo.getWidth() + adjedgedist, sgeo.getCenterY()));
						result.add(new mxPoint(sgeo.getX() + sgeo.getWidth() + adjedgedist, cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, cy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
					}
					else
					{
						double maxx = Math.max(sgeo.getX() + sgeo.getWidth() + adjedgedist,
											   tgeo.getX() + tgeo.getWidth() + adjedgedist);
						double maxy = Math.max(sgeo.getY() + sgeo.getHeight() + adjedgedist,
											   tgeo.getY() + tgeo.getHeight() + adjedgedist);
						result.add(new mxPoint(maxx, sgeo.getCenterY()));
						result.add(new mxPoint(maxx, maxy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, maxy));
						result.add(new mxPoint(tgeo.getX() - adjedgedist, tgeo.getCenterY()));
					}
				}
			}
		}
		else
		{
			for (mxPoint point : points)
			{
				mxPoint res = new mxPoint(point);
				res.setX(res.getX() * scale);
				res.setY(res.getY() * scale);
				VEdge vedge = (VEdge) state.getCell();
				if (vedge.getSource() != null && vedge.getEdgeParent() != null)
				{
					res = adjustPoint(state.getView().getGraph(), vedge.getEdgeParent(), res);
				}
				result.add(res);
			}
		}
	}
	
	/**
	 *  Adjusts a point for relative positioning.
	 *  
	 *  @param modelcontainer The model container.
	 *  @param parent The parent cell.
	 *  @param point The unadjusted targeted point.
	 *  @return The adjusted point.
	 */
	public static final mxPoint adjustPoint(mxGraph graph, Object parent, mxPoint point)
	{
		mxPoint p = point;
		double scale = graph.getView().getScale();
		
		mxCellState pstate = graph.getView().getState(parent);
		if (pstate != null)
		{
			p.setX(p.getX() + pstate.getOrigin().getX() * scale);
			p.setY(p.getY() + pstate.getOrigin().getY() * scale);
		}
		
		return p;
	}
	
	public static final VActivity getVActivity(Object cell)
	{
		VActivity ret = null;
		
		if (cell instanceof VActivity)
		{
			ret = (VActivity) cell;
		}
		else if (cell instanceof VInParameter)
		{
			ret = (VActivity) ((VInParameter) cell).getParent();
		}
		else if (cell instanceof VOutParameter)
		{
			ret = (VActivity) ((VOutParameter) cell).getParent();
		}
		
		return ret;
	}
	
	/**
	 *  Applies the style.
	 */
//	public void apply(mxCellState state, mxCellState source,
//			mxCellState target, List<mxPoint> points, List<mxPoint> result)
//	{
//		result.addAll(((mxICell) state.getCell()).getGeometry().getPoints());
//		mxGraphView view = state.getView();
//		mxPoint pt = ((points != null && points.size() > 0) ? points.get(0)
//				: null);
//		mxPoint p0 = state.getAbsolutePoint(0);
//		mxPoint pe = state
//				.getAbsolutePoint(state.getAbsolutePointCount() - 1);
//
//		if (pt != null)
//		{
//			pt = view.transformControlPoint(state, pt);
//		}
//
//		if (p0 != null)
//		{
//			source = new mxCellState();
//			source.setX(p0.getX());
//			source.setY(p0.getY());
//		}
//
//		if (pe != null)
//		{
//			target = new mxCellState();
//			target.setX(pe.getX());
//			target.setY(pe.getY());
//		}
//
//		if (source != null && target != null)
//		{
//			double l = Math.max(source.getX(), target.getX());
//			double r = Math.min(source.getX() + source.getWidth(),
//					target.getX() + target.getWidth());
//
//			double x = (pt != null) ? pt.getX() : r + (l - r) / 2;
//
//			double y1 = view.getRoutingCenterY(source);
//			double y2 = view.getRoutingCenterY(target);
//			
//			VActivity sactivity = null;
//			MActivity smactivity = null;
//			if (source.getCell() instanceof VActivity)
//			{
//				sactivity = (VActivity) source.getCell();
//				if (sactivity.getBpmnElement() != null)
//				{
//					smactivity = (MActivity) sactivity.getBpmnElement();
//				}
//				else
//				{
//					sactivity = null;
//				}
//			}
//
//			if (pt != null)
//			{
//				if (pt.getY() >= source.getY()
//						&& pt.getY() <= source.getY() + source.getHeight())
//				{
//					y1 = pt.getY();
//				}
//
//				if (pt.getY() >= target.getY()
//						&& pt.getY() <= target.getY() + target.getHeight())
//				{
//					y2 = pt.getY();
//				}
//			}
//			
//			
//			if (!target.contains(x, y1) && !source.contains(x, y1))
//			{
//				if (sactivity != null && smactivity.isEventHandler())
//				{
//					result.add(new mxPoint(source.getCenterX(), source.getY() + source.getHeight()));
//					result.add(new mxPoint(source.getCenterX(), y2));
//				}
////				else if (sactivity != null && smactivity.getActivityType().contains("Gateway"))
////				{
////					int size = smactivity.getOutgoingSequenceEdges().size();
////					VSequenceEdge vedge = (VSequenceEdge) state.getCell();
////					MSequenceEdge medge = (MSequenceEdge) vedge.getBpmnElement();
////					int pos = smactivity.getOutgoingSequenceEdges().indexOf(medge);
////					
////					if (size == 2)
////					{
////						++pos;
////					}
////					
////					switch(pos % 3)
////					{
////						case 1:
////							result.add(new mxPoint(source.getCenterX(), source.getY()));
////							break;
////						case 2:
////							result.add(new mxPoint(source.getCenterX(), source.getY() + source.getHeight()));
////							break;
////						case 0:
////						default:
////							result.add(new mxPoint(source.getCenterX(), source.getCenterY()));
////							
////					}
////					result.add(new mxPoint(source.getCenterX(), y2));
////					
////				}
//				else
//				{
//					result.add(new mxPoint(x, y1));
//				}
//			}
//
//			if (!target.contains(x, y2) && !source.contains(x, y2))
//			{
//				result.add(new mxPoint(x, y2));
//			}
//
//			if (result.size() == 1)
//			{
//				if (pt != null)
//				{
//					result.add(new mxPoint(x, pt.getY()));
//				}
//				else
//				{
//					double t = Math.max(source.getY(), target.getY());
//					double b = Math.min(source.getY() + source.getHeight(),
//							target.getY() + target.getHeight());
//
//					result.add(new mxPoint(x, t + (b - t) / 2));
//				}
//			}
//		}
//	}
}
