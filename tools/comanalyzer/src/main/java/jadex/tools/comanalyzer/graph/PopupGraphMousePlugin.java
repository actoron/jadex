package jadex.tools.comanalyzer.graph;

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.ComponentFilterMenu;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.MessageFilterMenu;
import jadex.tools.comanalyzer.graph.GraphCanvas.AgentGroup;
import jadex.tools.comanalyzer.graph.GraphCanvas.MessageGroup;


/**
 * A GraphMousePlugin that offers popup menu support.
 */
class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements MouseListener, MouseMotionListener
{
	/** The container fo the graph */
	private final GraphCanvas canvas;

	public PopupGraphMousePlugin(GraphCanvas canvas)
	{
		this(canvas, InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
	}

	public PopupGraphMousePlugin(GraphCanvas canvas, int modifiers)
	{
		super(modifiers);
		this.canvas = canvas;
		this.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

	/**
	 * Handle the mouse click for showing agent or message details in the
	 * element viewer.
	 */
	public void mouseClicked(MouseEvent e)
	{
		Point2D p = e.getPoint();// vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
		GraphElementAccessor pickSupport = canvas.vv.getPickSupport();
		if(pickSupport != null)
		{
			AgentGroup agents = (AgentGroup)pickSupport.getVertex(canvas.vv.getGraphLayout(), p.getX(), p.getY());
			MessageGroup edge = (MessageGroup)pickSupport.getEdge(canvas.vv.getGraphLayout(), p.getX(), p.getY());
			if(edge != null && edge.isSingelton())
			{
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					canvas.getToolTab().getToolPanel().showElementDetails(((Message)edge.getSingelton()).getParameters());
				}
			}
			if(agents != null && agents.isSingelton())
			{
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					canvas.getToolTab().getToolPanel().showElementDetails(((Component)agents.getSingelton()).getParameters());
				}
			}
		}

	}

	/**
	 * Handle popup trigger to show agnet and message filter menus
	 */
	protected void handlePopup(MouseEvent e)
	{
		final VisualizationViewer vv = (VisualizationViewer) e.getSource();
		Point2D p = e.getPoint();// vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());

		GraphElementAccessor pickSupport = vv.getPickSupport();
		if (pickSupport != null) {
			AgentGroup v = (AgentGroup)pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
			if (v != null) {
				ComponentFilterMenu apopup;
				Set picked = vv.getPickedVertexState().getPicked();
				if (picked.contains(v)) {
					List agents = new ArrayList();
					for(Iterator it=picked.iterator(); it.hasNext(); ) 
					{
						agents.addAll(((AgentGroup)it.next()).getElements());
					}
					apopup = new ComponentFilterMenu(canvas.getToolTab().getPlugin(), (Component[]) agents.toArray(new Component[agents.size()]));
				} 
				else 
				{
					vv.getPickedVertexState().clear();
					vv.getPickedVertexState().pick(v, true);
					vv.repaint();
					List agents = v.getElements();
					apopup = new ComponentFilterMenu(canvas.getToolTab().getPlugin(), (Component[]) agents.toArray(new Component[agents.size()]));
				}

				apopup.show(vv, e.getX(), e.getY());
			} 
			else 
			{
				MessageGroup edge = (MessageGroup)pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
				if (edge != null) {
					MessageFilterMenu mpopup;
					Set picked = vv.getPickedEdgeState().getPicked();
					if(picked.contains(edge)) {
						List messages = new ArrayList();
						for(Iterator it=picked.iterator(); it.hasNext(); ) 
						{
							messages.addAll(((MessageGroup)it.next()).getElements());
						}
						mpopup = new MessageFilterMenu(canvas.getToolTab().getPlugin(), (Message[]) messages.toArray(new Message[messages.size()]));

					} else 
					{
						vv.getPickedEdgeState().clear();
						vv.getPickedEdgeState().pick(edge, true);
						vv.repaint();
						List messages = edge.getElements();
						mpopup = new MessageFilterMenu(canvas.getToolTab().getPlugin(), (Message[]) messages.toArray(new Message[messages.size()]));

					}
					mpopup.show(vv, e.getX(), e.getY());

				}

			}
		}
	}

	/**
	 * Handle mouse moved to set the cursor to hand when over an agent (vertex)
	 * or message (edge).
	 */
	public void mouseMoved(MouseEvent e)
	{
		Point2D p = e.getPoint();// vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());

		GraphElementAccessor pickSupport = canvas.vv.getPickSupport();
		if (pickSupport != null) {
			AgentGroup v = (AgentGroup)pickSupport.getVertex(canvas.vv.getGraphLayout(), p.getX(), p.getY());
			MessageGroup ml = (MessageGroup)pickSupport.getEdge(canvas.vv.getGraphLayout(), p.getX(), p.getY());
			if (v == null && ml == null) {
				canvas.vv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else {
				canvas.vv.setCursor(cursor);
			}
		}

	}

	/**
	 * Not used yet...
	 */
	public void mouseDragged(MouseEvent e)
	{
	}

}