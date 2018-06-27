package jadex.gpmn.editor.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxPanningHandler;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

/**
 *  Graph component for editing GPMN models.
 *
 */
public class GpmnGraphComponent extends mxGraphComponent
{
	/** Throw slow-down */
	protected static final double THROW_SLOWDOWN = 1.0 - GuiConstants.THROW_ANIMATION_RESISTANCE;
	
	/** Throw frames that are unslowed */
	protected static final int UNSLOWED_FRAMES = (int) Math.round(GuiConstants.THROW_ANIMATION_UNSLOWED_TIME /
																  GuiConstants.ANIMATION_FRAME_TIME);
	
	/** Cell addition handler. */
	protected mxIEventListener cellsaddedhandler;
	
	/**
	 *  Creates a new graph component.
	 *  
	 *  @param graph The graph.
	 */
	public GpmnGraphComponent(mxGraph graph)
	{
		super(graph);
	}
	
	public boolean isPanningEvent(MouseEvent event)
	{
		return (event != null && MouseEvent.BUTTON3 == event.getButton());
	}
	
	protected mxPanningHandler createPanningHandler()
	{
		return new GpmnPanningHandler();
	}
	
	protected class GpmnPanningHandler extends mxPanningHandler
	{
		protected long starttime;
		
		protected Point disttraveled;
		
		protected Timer throwtimer;
		
		public GpmnPanningHandler()
		{
			super(GpmnGraphComponent.this);
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (isEnabled() && !e.isConsumed() && graphComponent.isPanningEvent(e))
			{
				start = e.getPoint();
				disttraveled = new Point(0, 0);
				starttime = e.getWhen();
				
				if (throwtimer != null)
				{
					throwtimer.stop();
					throwtimer = null;
				}
			}
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if (!e.isConsumed() && start != null)
			{
				int dx = e.getX() - start.x;
				int dy = e.getY() - start.y;
				
				disttraveled.x += dx;
				disttraveled.y += dy;

				Rectangle r = graphComponent.getViewport().getViewRect();

				int right = r.x + ((dx > 0) ? 0 : r.width) - dx;
				int bottom = r.y + ((dy > 0) ? 0 : r.height) - dy;
				
				boolean extend = ((right > 0) && (bottom > 0));

				graphComponent.getGraphControl().scrollRectToVisible(
						new Rectangle(right, bottom, 0, 0), extend);

				e.consume();
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			if (!e.isConsumed() && start != null)
			{
				int dx = Math.abs(start.x - e.getX());
				int dy = Math.abs(start.y - e.getY());
				
				long dt = e.getWhen() - starttime;
				final mxPoint velocity = new mxPoint((double) disttraveled.x / dt, (double) disttraveled.y / dt);
				velocity.setX(velocity.getX() * Math.abs(velocity.getX()));
				velocity.setY(velocity.getY() * Math.abs(velocity.getY()));

				if (graphComponent.isSignificant(dx, dy))
				{
					e.consume();
				}
				
//				if (Math.max(Math.abs(velocity.getX()), Math.abs(velocity.getY())) > GuiConstants.THROW_ANIMATION_VELOCITY_CUTOFF)
//				{
//					throwtimer = new Timer(GuiConstants.ANIMATION_FRAME_TIME, new AbstractAction()
//					{
//						protected mxPoint vispos;
//						
//						protected int unslowedframes = UNSLOWED_FRAMES;
//						
//						public void actionPerformed(ActionEvent e)
//						{
//							double dx = velocity.getX() * GuiConstants.THROW_ANIMATION_VELOCITY_SPEEDUP;
//							double dy = velocity.getY() * GuiConstants.THROW_ANIMATION_VELOCITY_SPEEDUP;
//							
//							JScrollBar hbar = graphComponent.getHorizontalScrollBar();
//							JScrollBar vbar = graphComponent.getVerticalScrollBar();
//							
//							if (vispos == null)
//							{
//								vispos = new mxPoint(hbar.getModel().getValue(), vbar.getModel().getValue());
//							}
//							
//							vispos.setX(vispos.getX() - dx);
//							vispos.setY(vispos.getY() - dy);
//							
//							hbar.getModel().setValue((int) Math.round(vispos.getX()));
//							vbar.getModel().setValue((int) Math.round(vispos.getY()));
//							
//							if (unslowedframes == 0)
//							{
//								velocity.setX(velocity.getX() * THROW_SLOWDOWN);
//								velocity.setY(velocity.getY() * THROW_SLOWDOWN);
//							}
//							else
//							{
//								--unslowedframes;
//							}
//							
//							if (Math.abs(velocity.getX()) < GuiConstants.THROW_ANIMATION_VELOCITY_CUTOFF &&
//								Math.abs(velocity.getY()) < GuiConstants.THROW_ANIMATION_VELOCITY_CUTOFF)
//							{
//								throwtimer.stop();
//								throwtimer = null;
//							}
//						}
//					});
//					throwtimer.start();
//				}
			}

			start = null;
		}
	}
}
