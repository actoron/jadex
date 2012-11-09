package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MActivity;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxPanningHandler;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 *  Graph component for editing GPMN models.
 *
 */
public class BpmnGraphComponent extends mxGraphComponent
{
	/** Cell addition handler. */
	protected mxIEventListener cellsaddedhandler;
	
	/**
	 *  Creates a new graph component.
	 *  
	 *  @param graph The graph.
	 */
	public BpmnGraphComponent(mxGraph graph)
	{
		super(graph);
	}
	
	public boolean isPanningEvent(MouseEvent event)
	{
		return (event != null && MouseEvent.BUTTON3 == event.getButton());
	}
	
	protected mxPanningHandler createPanningHandler()
	{
		return new BpmnPanningHandler();
	}
	
	protected mxGraphHandler createGraphHandler()
	{
		return new BpmnGraphHandler(this);
	}
	
	protected mxICellEditor createCellEditor()
	{
		mxCellEditor ret = new mxCellEditor(this)
		{
			protected boolean useLabelBounds(mxCellState state)
			{
				boolean ret = true;
				if (state.getCell() instanceof VActivity &&
					((MActivity) ((VActivity) state.getCell()).getBpmnElement()).getActivityType().startsWith("Gateway"))
				{
					ret = false;
				}
				return ret;
			}
		};
		
		return ret;
	}
	
	protected mxConnectionHandler createConnectionHandler()
	{
		return super.createConnectionHandler();
	}
	
	protected mxGraphControl createGraphControl()
	{
		return new BpmnGraphControl();
	}
	
	public void extendComponent(Rectangle rect)
	{
		((BpmnGraphControl) getGraphControl()).doExtendComponent(rect);
	}
	
	protected class BpmnPanningHandler extends mxPanningHandler
	{
		public BpmnPanningHandler()
		{
			super(BpmnGraphComponent.this);
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (isEnabled() && !e.isConsumed() && graphComponent.isPanningEvent(e))
			{
				start = e.getPoint();
			}
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if (!e.isConsumed() && start != null)
			{
				int dx = e.getX() - start.x;
				int dy = e.getY() - start.y;

				Rectangle r = graphComponent.getViewport().getViewRect();

				int right = r.x + ((dx > 0) ? 0 : r.width) - dx;
				int bottom = r.y + ((dy > 0) ? 0 : r.height) - dy;
				
				boolean extend = ((right > 0) && (bottom > 0));

				graphComponent.getGraphControl().scrollRectToVisible(
						new Rectangle(right, bottom, 0, 0), extend);

				e.consume();
			}
		}
	}
	
	public class BpmnGraphControl extends mxGraphControl
	{
		public void doSetMinimumSize(Dimension minimumSize)
		{
			super.setMinimumSize(minimumSize);
		}
		
		public void setMinimumSize(Dimension minimumSize)
		{
			if (minimumSize.width > getMinimumSize().width &&
				minimumSize.height > getMinimumSize().height)
			{
				super.setMinimumSize(minimumSize);
			}
		}
		
		public void doSetPreferredSize(Dimension preferredSize)
		{
			super.setPreferredSize(preferredSize);
		}
		
		public void setPreferredSize(Dimension preferredSize)
		{
			if (preferredSize.width > getPreferredSize().width &&
				preferredSize.height > getPreferredSize().height)
			{
				super.setPreferredSize(preferredSize);
			}
		}
		
		public void doExtendComponent(Rectangle rect)
		{
			super.extendComponent(rect);
		}
	}
	
	protected class BpmnGraphHandler extends mxGraphHandler
	{
		public BpmnGraphHandler(mxGraphComponent graphComponent)
		{
			super(graphComponent);
		}
		
		protected void moveCells(Object[] cells, double dx, double dy,
				Object target, MouseEvent e)
		{
			boolean movecells = true;
			for (int i = 0; i < cells.length; ++i)
			{
				if (cells[i] instanceof VActivity)
				{
					if (!(target instanceof VLane || target instanceof VPool))
					{
						movecells = false;
						break;
					}
					else
					{
						VPool targetpool = null;
						if (target instanceof VLane)
						{
							targetpool = ((VLane) target).getPool();
						}
						else
						{
							targetpool = (VPool) target;
							if (targetpool.hasLanes())
							{
								movecells = false;
								break;
							}
						}
						MActivity mactivity = (MActivity) ((VActivity) cells[i]).getBpmnElement();
						
						// Only allow cross-pool activity transfers if no sequence edges are connected
						if (!targetpool.getBpmnElement().equals(mactivity.getPool()) &&
							((mactivity.getOutgoingSequenceEdges() != null && mactivity.getOutgoingSequenceEdges().size() > 0) ||
							(mactivity.getIncomingSequenceEdges() != null && mactivity.getIncomingSequenceEdges().size() > 0)))
						{
							//TODO: Add message?
							movecells = false;
							break;
						}
					}
				}
				else if (cells[i] instanceof VLane)
				{
					VLane vlane = (VLane) cells[i];
					if (vlane.getPool() != target)
					{
						movecells = false;
						break;
					}
				}
				else if (cells[i] instanceof VPool)
				{
					if (target != cells[i] && target != null)
					{
						movecells = false;
						break;
					}
				}
			}
			
			if (movecells)
			{
				super.moveCells(cells, dx, dy, target, e);
			}
		}
	}
}
