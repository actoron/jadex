package jadex.bpmn.editor.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxLayoutManager;
import com.mxgraph.view.mxStylesheet;

import jadex.bpmn.editor.gui.controllers.SValidation;
import jadex.bpmn.editor.gui.layouts.EventHandlerLayout;
import jadex.bpmn.editor.gui.layouts.LaneLayout;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.editor.model.visual.VNode;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;
import jadex.commons.collection.LRU;

/**
 *  Graph for BPMN models.
 */
public class BpmnGraph extends mxGraph
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** The layout manager. */
	protected BpmnLayoutManager layoutmanager;
	
	/** Element ID cache. */
	protected LRU<String, VElement> elementidcache;
	
	/**
	 *  Creates the graph.
	 */
	public BpmnGraph(ModelContainer container, mxStylesheet sheet)
	{
		this.modelcontainer = container;
		this.elementidcache = new LRU<String, VElement>(100);
		setAllowDanglingEdges(false);
		setAllowLoops(true);
		setVertexLabelsMovable(false);
		setEdgeLabelsMovable(true);
		setCellsCloneable(false);
		setAllowNegativeCoordinates(true);
		setGridEnabled(true);
		setGridSize(10);
		/*getModel().addListener(mxEvent.EXECUTE, access.getValueChangeController());
		getSelectionModel().addListener(mxEvent.CHANGE, access.getSelectionController());
		
		addListener(mxEvent.CONNECT_CELL, access.getEdgeReconnectController());
		
		addListener(mxEvent.CELLS_FOLDED, access.getFoldController());*/
		
		setKeepEdgesInForeground(true);
//		setKeepEdgesInBackground(true);
		
//		addListener(mxEvent.CELLS_ADDED, new mxIEventListener()
//		{
//			public void invoke(Object sender, mxEventObject evt)
//			{
//				Object[] cells = (Object[]) evt.getProperty("cells");
//				for (Object cell : cells)
//				{
//					if (cell instanceof mxICell)
//					{
//						mxICell parent = ((mxICell) cell).getParent();
//						if (parent != null)
//						{
//							List<VEdge> edges = new ArrayList<VEdge>();
//							for (int i = 0; i < parent.getChildCount(); ++i)
//							{
//								if (parent.getChildAt(i) instanceof VEdge)
//								{
//									edges.add((VEdge) parent.getChildAt(i));
//								}
//							}
//							Object[] aedges = edges.toArray();
//							orderCells(false, aedges);
//						}
//					}
//				}
//				
//				Object[] cells = getChildCells(((mxICell) getModel().getRoot()).getChildAt(0));
//				System.out.println(getChildCells(((mxICell) getModel().getRoot()).getChildAt(0)).length);
//				orderCells(true, cells);
//			}
//		});
		
		addListener(mxEvent.CELLS_RESIZED, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				final Object[] cells = (Object[]) evt.getProperty("cells");
				for (int i = 0; i < cells.length; ++i)
				{
					if (cells[i] instanceof VPool)
					{
						VPool vpool = (VPool) cells[i];
//						System.out.println(vpool.getGeometry() + " " + vpool.getPreviousGeometry());
						if (vpool.getGeometry().getX() == vpool.getPreviousGeometry().getX() + vpool.getPreviousGeometry().getWidth() ||
							vpool.getGeometry().getX() + vpool.getGeometry().getWidth() == vpool.getPreviousGeometry().getX())
						{
							vpool.getGeometry().setWidth(vpool.getGeometry().getWidth() + vpool.getPreviousGeometry().getWidth());
							vpool.getGeometry().setX(Math.min(vpool.getPreviousGeometry().getX(), vpool.getGeometry().getX()));
						}
						if (vpool.getGeometry().getY() == vpool.getPreviousGeometry().getY() + vpool.getPreviousGeometry().getHeight() ||
							vpool.getGeometry().getY() + vpool.getGeometry().getHeight() == vpool.getPreviousGeometry().getY())
						{
							vpool.getGeometry().setHeight(vpool.getGeometry().getHeight() + vpool.getPreviousGeometry().getHeight());
							vpool.getGeometry().setY(Math.min(vpool.getPreviousGeometry().getY(), vpool.getGeometry().getY()));
						}
						Integer startsize = (Integer) getStylesheet().getCellStyle(vpool.getStyle(), null).get(mxConstants.STYLE_STARTSIZE);
						startsize = startsize != null? startsize : mxConstants.DEFAULT_STARTSIZE;
						double minx = Double.MAX_VALUE;
						double miny = Double.MAX_VALUE;
						double maxx = 0.0;
						double maxy = 0.0;
						boolean leftresize = vpool.getPreviousGeometry().getX() != vpool.getGeometry().getX();
						boolean topresize = vpool.getPreviousGeometry().getY() != vpool.getGeometry().getY();
						double xdiff = vpool.getPreviousGeometry().getX() - vpool.getGeometry().getX();
						double ydiff = vpool.getPreviousGeometry().getY() - vpool.getGeometry().getY();
						List<VNode> innercells = new ArrayList<VNode>();
						for (int j = 0; j < vpool.getChildCount(); ++j)
						{
							Object obj = vpool.getChildAt(j);
							if (obj instanceof VLane)
							{
								VLane lane = (VLane) obj;
								for (int k = 0; k < lane.getChildCount(); ++k)
								{
									Object lobj = lane.getChildAt(k);
									if (lobj instanceof VNode)
									{
										VNode node = (VNode) lobj;
										mxGeometry geo = node.getGeometry();
										if (minx > geo.getX())
										{
											minx = geo.getX();
										}
										if (miny > geo.getY())
										{
											miny = geo.getY();
										}
										if (maxx < geo.getWidth() + geo.getX())
										{
											maxx = geo.getWidth() + geo.getX();
										}
										if (maxy < (mxConstants.SHADOW_OFFSETY + geo.getHeight() + geo.getY()))
										{
											maxy = mxConstants.SHADOW_OFFSETY + geo.getHeight() + geo.getY();
										}
										
										innercells.add(node);
									}
								}
							}
							else if (obj instanceof VNode)
							{
								VNode node = (VNode) obj;
								mxGeometry geo = node.getGeometry();
								if (minx > geo.getX())
								{
									minx = geo.getX();
								}
								if (miny > geo.getY())
								{
									miny = geo.getY();
								}
								if (maxx < geo.getWidth() + geo.getX())
								{
									maxx = geo.getWidth() + geo.getX();
								}
								if (maxy < (mxConstants.SHADOW_OFFSETY + geo.getHeight() + geo.getY()))
								{
									maxy = mxConstants.SHADOW_OFFSETY + geo.getHeight() + geo.getY();
								}
								
								innercells.add(node);
							}
						}
						
						if (!leftresize)
						{
							if (maxx > vpool.getGeometry().getWidth())
							{
								double diff = maxx - vpool.getGeometry().getWidth();
								xdiff = -diff;
								if ((minx + xdiff) < startsize)
								{
									xdiff += (startsize - (minx + xdiff));
									diff += xdiff;
									vpool.getGeometry().setWidth(vpool.getGeometry().getWidth() + diff);
								}
							}
						}
						else
						{
							if ((minx + xdiff) < startsize)
							{
								xdiff -= (minx + (xdiff - startsize));
							}
							if (vpool.getGeometry().getWidth() < (startsize + maxx - minx))
							{
								double diff = (startsize + maxx - minx) - vpool.getGeometry().getWidth();
								vpool.getGeometry().setWidth(startsize + maxx - minx);
								vpool.getGeometry().setX(vpool.getGeometry().getX() - diff);
							}
						}
						if (!topresize)
						{
							if (maxy > vpool.getGeometry().getHeight())
							{
								double diff = maxy - vpool.getGeometry().getHeight();
								ydiff = -diff;
								if ((miny + ydiff) < 0)
								{
									ydiff += -(miny + ydiff);
									diff += ydiff;
									vpool.getGeometry().setHeight(vpool.getGeometry().getHeight() + diff);
								}
							}
						}
						else
						{
							if ((miny + ydiff) < 0)
							{
								ydiff -= (miny + ydiff);
							}
							if (vpool.getGeometry().getHeight() < (maxy - miny))
							{
								double diff = (maxy - miny) - vpool.getGeometry().getHeight();
								vpool.getGeometry().setHeight(maxy - miny);
								vpool.getGeometry().setY(vpool.getGeometry().getY() - diff);
							}
						}
						
//								System.out.println(minx + " " + miny + " " + maxx + " " + maxy + " " + xdiff + " " + ydiff);
						
						for (VNode node : innercells)
						{
							mxGeometry geo = node.getGeometry();
							geo.setX(geo.getX() + xdiff);
							geo.setY(geo.getY() + ydiff);
						}
						Object[] selcells = getSelectionCells();
						clearSelection();
						refreshCellView(vpool);
						setSelectionCells(selcells);
					}
				}
			}
		});
		
		setStylesheet(sheet);
		activate();
	}
	
	/**
	 * 
	 */
	public void activate()
	{
		layoutmanager = new BpmnLayoutManager(this);
	}
	
	/**
	 * 
	 */
	public void deactivate()
	{
		layoutmanager = null;
	}
	
	/**
	 * Returns true if the given cell is expandable. This implementation
	 * returns true if the cell has at least one child and its style
	 * does not specify mxConstants.STYLE_FOLDABLE to be 0.
	 *
	 * @param cell <mxCell> whose expandable state should be returned.
	 * @return Returns true if the given cell is expandable.
	 */
	public boolean isCellFoldable(Object cell, boolean collapse)
	{
		boolean ret = super.isCellFoldable(cell, collapse);
		if (cell instanceof VSubProcess)
//			|| cell instanceof VExternalSubProcess)
		{
			ret = true;
		}
		return ret;
	}
	
	/**
	 *  Gets the tool tip for a cell.
	 *  @param cell The cell.
	 *  @return The tool tip.
	 */
	public String getToolTipForCell(Object cell)
	{
		if (cell instanceof VOutParameter ||
			cell instanceof VInParameter)
		{
			return super.getToolTipForCell(cell);
		}
		return null;
	}
	
	/*protected mxGraphView createGraphView()
	{
		return new BpmnGraphView(this);
	}*/
	
	/**
	 *  Gets the model container.
	 *
	 *  @return The model container.
	 */
	public ModelContainer getModelContainer()
	{
		return modelcontainer;
	}
	
	/**
	 * 
	 */
	public boolean isCellVisible(Object cell)
	{
		boolean ret = super.isCellVisible(cell);
		
		if (cell instanceof VInParameter ||
			cell instanceof VOutParameter ||
			cell instanceof VDataEdge)
		{
			if(modelcontainer.getSettings()!=null)
			{
				ret &= modelcontainer.getSettings().isDataEdges();
			}
		}
		else if (cell instanceof VSequenceEdge)
		{
			if(modelcontainer.getSettings()!=null)
			{
				ret &= modelcontainer.getSettings().isSequenceEdges();
			}
		}
		
//		System.out.println("IsVisible: " + cell + " " + ret);
		return ret;
	}

	/**
	 * Returns true if the given cell is a valid drop target for the specified
	 * cells. This returns true if the cell is a swimlane, has children and is
	 * not collapsed, or if splitEnabled is true and isSplitTarget returns
	 * true for the given arguments
	 * 
	 * @param cell Object that represents the possible drop target.
	 * @param cells Objects that are going to be dropped.
	 * @return Returns true if the cell is a valid drop target for the given
	 * cells.
	 */
	public boolean isValidDropTarget(Object cell, Object[] cells)
	{
		boolean ret = false;
		
		/* Special case for internal subprocesses,
		 * they are a potential parent but not for themselves. */
		if (cell instanceof VSubProcess && cells != null)
		{
			
			boolean nomatch = true;
			for (int i = 0; i < cells.length; ++i)
			{
				if (cells[i].equals(cell))
				{
					nomatch = false;
					break;
				}
			}
			
			if (nomatch)
			{
				ret = (isSplitEnabled() && isSplitTarget(cell, cells)) ||
						(!model.isEdge(cell) && !isCellCollapsed(cell));
			}
		}
//		else if (cell instanceof VActivity &&
//				 ((VActivity) cell).getBpmnElement() != null &&
//				 (cell instanceof VExternalSubProcess ||
//				 MBpmnModel.TASK.equals(((MActivity) ((VActivity) cell).getBpmnElement()).getActivityType())))
		else if (cell instanceof VActivity &&
				 ((VActivity) cell).getBpmnElement() != null &&
				 (cell instanceof VExternalSubProcess ||
				 ((VActivity) cell).getBpmnElement() instanceof MTask))
		{
			/* Tasks are never drop targets, even if they contain children, they will be all event handlers. */
			ret = false;
		}
		else
		{
			ret = cell != null
					&& ((isSplitEnabled() && isSplitTarget(cell, cells)) ||
							(!model.isEdge(cell) && (isSwimlane(cell) ||
							(model.getChildCount(cell) > 0 && !isCellCollapsed(cell)))));
		}
		
		return ret;
	}
	
	/**
	 *  Refreshes the view for a cell later.
	 *  
	 *  @param cell The cell.
	 */
	public void delayedRefreshCellView(final mxICell cell)
	{
		if (layoutmanager != null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					refreshCellView(cell);
				}
			});
		}
	}
	
	/**
	 *  Gets a visual element by BPMN id.
	 *  
	 *  @param id The ID.
	 *  @return The element, null if not found.
	 */
	public VElement getVisualElementById(String id)
	{
		return getVisualElementById((mxICell) getModel().getRoot(), id);
	}
	
	/**
	 *  Gets a visual element by BPMN id.
	 *  
	 *  @param id The ID.
	 *  @return The element, null if not found.
	 */
	public VElement getVisualElementById(mxICell startelement, String id)
	{
		VElement ret = elementidcache.get(id);
		
		if (ret == null)
		{
			ret = findElementById(startelement, id);
			elementidcache.put(id, ret);
		}
		
		return ret;
	}

	/**
	 *  Refreshes the view for a cell.
	 *  
	 *  @param cell The cell.
	 */
	public void refreshCellView(mxICell cell)
	{
		if (layoutmanager != null)
		{
			mxIGraphLayout layout = layoutmanager.getLayout(cell);
			if (layout != null)
			{
				layout.execute(cell);
			}
			
			getView().clear(cell, true, false);
			getView().invalidate(cell);
			getView().validate();
			
			refresh();
		}
	}
	
	/**
	 * Returns the validation error message to be displayed when inserting or
	 * changing an edges' connectivity. A return value of null means the edge
	 * is valid, a return value of '' means it's not valid, but do not display
	 * an error message. Any other (non-empty) string returned from this method
	 * is displayed as an error message when trying to connect an edge to a
	 * source and target. This implementation uses the multiplicities, as
	 * well as multigraph and allowDanglingEdges to generate validation
	 * errors.
	 * 
	 * @param edge Cell that represents the edge to validate.
	 * @param source Cell that represents the source terminal.
	 * @param target Cell that represents the target terminal.
	 */
	public String getEdgeValidationError(Object edge, Object source,
			Object target)
	{
		String error = super.getEdgeValidationError(edge, source, target);
		if (error == null)
		{
			if (edge instanceof VSequenceEdge)
			{
				error = SValidation.getSequenceEdgeValidationError(source, target);
			}
			else if (edge instanceof VDataEdge)
			{
				error = SValidation.getDataEdgeValidationError(source, target);
			}
			else if (edge instanceof VMessagingEdge)
			{
				error = SValidation.getMessagingEdgeValidationError(source, target);
			}
		}
		
		return error;
	}
	
	protected VElement findElementById(mxICell startelement, String id)
	{
		if(startelement instanceof VElement &&
		   !(startelement instanceof VInParameter || startelement instanceof VOutParameter) &&
		   id.equals(((VElement)startelement).getBpmnElement().getId()))
		{
			return (VElement) startelement;
		}
		
		VElement ret = null;
		for (int i = 0; i < startelement.getChildCount() && ret == null; ++i)
		{
			ret = findElementById(startelement.getChildAt(i), id);
		}
		
		return ret;
	}
	
	/**
	 *  The layout manager.
	 *
	 */
	protected static class BpmnLayoutManager extends mxLayoutManager
	{
		/** Layout for lanes. */
		protected mxStackLayout lanelayout;
		
		/** Layout for event handlers. */
		protected EventHandlerLayout evtlayout; 
		
		/**
		 *  Creates new layout manager.
		 *  @param graph The graph.
		 */
		public BpmnLayoutManager(mxGraph graph)
		{
			super(graph);
			this.lanelayout = new LaneLayout(graph);
			this.evtlayout = new EventHandlerLayout(graph);
		}
		
		/**
		 *  Gets the layout.
		 */
		protected mxIGraphLayout getLayout(Object parent)
		{
			if (parent instanceof VPool &&
				graph.getModel().getChildCount(parent) > 0 &&
				graph.getModel().getChildAt(parent, 0) instanceof VLane)
			{
				return lanelayout;
			}
			
			if (parent instanceof VElement &&
				((VElement) parent).getBpmnElement() instanceof MActivity)
			{
				MActivity mparent = (MActivity) ((VElement) parent).getBpmnElement();
//				if (MBpmnModel.TASK.equals(mparent.getActivityType()) ||
//					MBpmnModel.SUBPROCESS.equals(mparent.getActivityType()))
				if (mparent instanceof MTask ||
					MBpmnModel.SUBPROCESS.equals(mparent.getActivityType()))
				{
					return evtlayout;
				}
			}
			
			return null;
		}
	}
}
