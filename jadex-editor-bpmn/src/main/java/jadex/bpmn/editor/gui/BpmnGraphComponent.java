package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.EdgeController;
import jadex.bpmn.editor.gui.controllers.GraphOperationsController;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxPanningHandler;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;

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
	public BpmnGraphComponent(BpmnGraph graph)
	{
		super(graph);
	}
	
	/**
	 *  Returns the folding icon bounds.
	 */
	public Rectangle getFoldingIconBounds(mxCellState state, ImageIcon icon)
	{
		if (state.getCell() instanceof VSubProcess)
		{
			double scale = getGraph().getView().getScale();
			
			int w = (int) Math.max(8, icon.getIconWidth() * scale * 2);
			int h = (int) Math.max(8, icon.getIconHeight() * scale * 2);
			int x = (int) Math.round(state.getX() + state.getWidth() * 0.5 - w * 0.5);
			int y = (int) Math.round(state.getY() + state.getHeight() - h * 1.25);
			
			
			return new Rectangle(x, y, w, h);
		}

		return super.getFoldingIconBounds(state, icon);
	}
	
	/**
	 * Returns true if the given event is a panning event.
	 */
	public boolean isPanningEvent(MouseEvent event)
	{
		return (event != null && MouseEvent.BUTTON3 == event.getButton());
	}
	
	/**
	 *  Creates the panning handler.
	 */
	protected mxPanningHandler createPanningHandler()
	{
		return new BpmnPanningHandler();
	}
	
	/**
	 *  Creates the graph controller.
	 */
	protected mxGraphHandler createGraphHandler()
	{
		return new GraphOperationsController(this);
	}
	
	/**
	 *  Creates the cell editor.
	 */
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
		return new EdgeController(this);
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
			if (preferredSize.width > getPreferredSize().width ||
				preferredSize.height > getPreferredSize().height)
			{
				super.setPreferredSize(new Dimension(Math.max(preferredSize.width, getPreferredSize().width),
													 Math.max(preferredSize.height, getPreferredSize().height)));
			}
		}
		
		public void doExtendComponent(Rectangle rect)
		{
			super.extendComponent(rect);
		}
	}
}
