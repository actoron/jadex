package jadex.bpmn.editor.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.ImageIcon;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxPanningHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.EdgeController;
import jadex.bpmn.editor.gui.controllers.GraphOperationsController;
import jadex.bpmn.editor.gui.controllers.KeyboardController;
import jadex.bpmn.editor.gui.controllers.MouseController;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;

/**
 *  Graph component for editing GPMN models.
 *
 */
public class BpmnGraphComponent extends mxGraphComponent
{
	/** Cell addition handler. */
	protected mxIEventListener cellsaddedhandler;
	
	/** Collapse Image Icon. */
	protected ImageIcon collapseimageicon;
	
	/** Uncollapse Image Icon. */
	protected ImageIcon uncollapseimageicon;
	
	/**
	 *  Creates a new graph component.
	 *  
	 *  @param graph The graph.
	 */
	public BpmnGraphComponent(BpmnGraph graph)
	{
		super(graph);
		
		((mxCellEditor) getCellEditor()).setMinimumEditorScale(0.0);
		double fac1 = 0.5;
		double fac2 = 3.0;
		int width = 16;
		int height = 16;
		double dx = width / 16.0;
		double dy = width / 16.0;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = img.createGraphics();
		GeneralPath gp = new GeneralPath();
		gp.moveTo(dx * fac2, height * fac1 - dy);
		gp.lineTo(width * fac1 - dx, height * fac1 - dy);
		gp.lineTo(width * fac1 - dx, dy * fac2);
		gp.lineTo(width * fac1 + dx, dy * fac2);
		gp.lineTo(width * fac1 + dx, height * fac1 - dy);
		gp.lineTo(width - dx * fac2, height * fac1 - dy);
		gp.lineTo(width - dx * fac2, height * fac1 + dy);
		gp.lineTo(width * fac1 + dx, height * fac1 + dy);
		gp.lineTo(width * fac1 + dx, height - dy * fac2);
		gp.lineTo(width * fac1 - dx, height - dy * fac2);
		gp.lineTo(width * fac1 - dx, height * fac1 + dy);
		gp.lineTo(dx * fac2, height * fac1 + dy);
		gp.closePath();
		g.setColor(Color.BLACK);
		g.fill(gp);
		Area frame = new Area(new Rectangle2D.Double(0, 0, width, height));
		frame.subtract(new Area(new Rectangle2D.Double(dx, dy , width - dx - dx, height - dy - dy)));
		g.fill(frame);
		g.dispose();
		uncollapseimageicon = new ImageIcon(img);
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		g = img.createGraphics();
		gp = new GeneralPath();
		gp.moveTo(dx * fac2, height * fac1 - dy);
		gp.lineTo(width - dx * fac2, height * fac1 - dy);
		gp.lineTo(width - dx * fac2, height * fac1 + dy);
		gp.lineTo(dx * fac2, height * fac1 + dy);
		gp.closePath();
		g.setColor(Color.BLACK);
		g.fill(gp);
		g.fill(frame);
		g.dispose();
		collapseimageicon = new ImageIcon(img);
		
		setToolTips(true);
	}
	
	/**
	 *  Perform default init.
	 */
	public void init(ModelContainer modelcontainer)
	{
		setDragEnabled(false);
		setPanning(true);
		setCenterZoom(false);
		setAutoscrolls(true);
		setAutoExtend(true);
		getViewport().setOpaque(false);
		setBackground(Color.WHITE);
		setOpaque(true);
		setTextAntiAlias(true);
		modelcontainer.setGraphComponent(this);
		
		MouseController mc = new MouseController(modelcontainer);
		getGraphControl().addMouseListener(mc);
		getGraphControl().addMouseWheelListener(mc);
		
		new DeletionController(modelcontainer);
		
		new KeyboardController(this, modelcontainer);
		
		new mxRubberband(this);
	}
	
	/**
	 * Returns the icon used to display the collapsed state of the specified
	 * cell state. This returns null for all edges.
	 */
	public ImageIcon getFoldingIcon(mxCellState state)
	{
		if (state.getCell() instanceof VSubProcess)
		{
//			if (graph.isCellCollapsed(state.getCell()))
//			{
//				return uncollapseimageicon;
//			}
//			else
//			{
//				return collapseimageicon;
//			}
			if (((VSubProcess) state.getCell()).isPseudoFolded())
			{
				return uncollapseimageicon;
			}
			else
			{
				return collapseimageicon;
			}
		}
		else
		{
			return super.getFoldingIcon(state);
		}
	}
	
	/**
	 *  Returns the folding icon bounds.
	 */
	public Rectangle getFoldingIconBounds(mxCellState state, ImageIcon icon)
	{
		if (state.getCell() instanceof VSubProcess)
		{
			double scale = getGraph().getView().getScale();
			
			int w = (int) Math.max(8, icon.getIconWidth() * scale);
			int h = (int) Math.max(8, icon.getIconHeight() * scale);
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
	 *  Returns true if the given event is an edit event.
	 */
	public boolean isEditEvent(MouseEvent e)
	{
		return (e != null) ? e.getClickCount() == 2 && MouseEvent.BUTTON1 == e.getButton() : false;
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
		return new EdgeController(this, ((BpmnGraph) getGraph()).getModelContainer());
	}
	
	protected mxGraphControl createGraphControl()
	{
		return new BpmnGraphControl();
	}
	
	public mxInteractiveCanvas createCanvas()
	{
		return new mxInteractiveCanvas()
		{

			public void setScale(double scale)
			{
				super.setScale(scale);
			}
			
			/**
			 *  Bug-fixed method.
			 */
			public Stroke createStroke(Map<String, Object> style)
			{
				float strokewidth = mxUtils.getFloat(style, mxConstants.STYLE_STROKEWIDTH, 1);
				if (mxUtils.isTrue(style, mxConstants.STYLE_DASHED))
				{
					float[] dashpattern = null;
					if (style.get(mxConstants.STYLE_DASH_PATTERN) != null &&
						float[].class.isInstance(style.get(mxConstants.STYLE_DASH_PATTERN)))
					{
						dashpattern = (float[]) style.get(mxConstants.STYLE_DASH_PATTERN);
					}
					else
					{
						dashpattern = mxUtils.getFloatArray(style,
							mxConstants.STYLE_DASH_PATTERN,
							mxConstants.DEFAULT_DASHED_PATTERN, " ");
					}
					float[] scaleddashpattern = new float[dashpattern.length];

					for (int i = 0; i < dashpattern.length; i++)
					{
						scaleddashpattern[i] = (float) Math.max(1.0f, (dashpattern[i]* strokewidth * scale));
					}
					
					return new BasicStroke(strokewidth, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND, 10.0f, scaleddashpattern, 0.0f);
//					return new BasicStroke(strokewidth, BasicStroke.CAP_BUTT,
//							BasicStroke.JOIN_BEVEL, 0.0f, scaleddashpattern, 0.0f);
				}
				else
				{
					return new BasicStroke(strokewidth);
				}
			}
			
			/**
			 *  Bug-fixed/enhanced method.
			 */
			public Paint createFillPaint(mxRectangle bounds,
					Map<String, Object> style)
			{
				Color fillColor = mxUtils.getColor(style, mxConstants.STYLE_FILLCOLOR);
				Paint fillPaint = null;

				if (fillColor != null)
				{
					Color gradientColor = mxUtils.getColor(style,
							mxConstants.STYLE_GRADIENTCOLOR);

					if (gradientColor != null)
					{
						String gradientDirection = mxUtils.getString(style,
								mxConstants.STYLE_GRADIENT_DIRECTION);

						float x1 = (float) bounds.getX();
						float y1 = (float) bounds.getY();
						float x2 = (float) bounds.getX();
						float y2 = (float) bounds.getY();

						if (gradientDirection == null
								|| gradientDirection
										.equals(mxConstants.DIRECTION_SOUTH))
						{
							y2 = (float) (bounds.getY() + bounds.getHeight());
						}
						else if (gradientDirection.equals(mxConstants.DIRECTION_EAST))
						{
							x2 = (float) (bounds.getX() + bounds.getWidth());
						}
						else if (gradientDirection.equals(mxConstants.DIRECTION_NORTH))
						{
							y1 = (float) (bounds.getY() + bounds.getHeight());
						}
						else if (gradientDirection.equals(mxConstants.DIRECTION_WEST))
						{
							x1 = (float) (bounds.getX() + bounds.getWidth());
						}
						else if (gradientDirection.equals("northeast"))
						{
							x2 = (float) (bounds.getX() + bounds.getWidth());
							y1 = (float) (bounds.getY() + bounds.getHeight());
						}
						else if (gradientDirection.equals("southwest"))
						{
							y2 = (float) (bounds.getY() + bounds.getHeight());
							x1 = (float) (bounds.getX() + bounds.getWidth());
						}
						else if (gradientDirection.equals("northwest"))
						{
							double min = Math.min(bounds.getWidth(), bounds.getHeight());
							double max = Math.max(bounds.getWidth(), bounds.getHeight());
							double length = (max - min) * 0.5 + min;
							x1 = (float) (bounds.getX() + length);
							y1 = (float) (bounds.getY() + length);
						}
						
						if (fillPaint == null)
						{
							fillPaint = new GradientPaint(x1, y1, fillColor, x2, y2, gradientColor, false);
						}
					}
				}

				return fillPaint;
			}
		};
	}
	
	public void extendComponent(Rectangle rect)
	{
		((BpmnGraphControl) getGraphControl()).doExtendComponent(rect);
	}
	
	protected class BpmnPanningHandler extends mxPanningHandler
	{
		protected int mdx;
		protected int mdy;
		
		public BpmnPanningHandler()
		{
			super(BpmnGraphComponent.this);
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (isEnabled() && !e.isConsumed() && graphComponent.isPanningEvent(e))
			{
				mdx = 0;
				mdy = 0;
				start = e.getPoint();
			}
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if (!e.isConsumed() && start != null)
			{
				int dx = e.getX() - start.x;
				int dy = e.getY() - start.y;
				int incx = dx - mdx;
				int incy = dy - mdy;

				Rectangle r = graphComponent.getViewport().getViewRect();

				int right = r.x + ((dx > 0) ? 0 : r.width) - dx;
				int bottom = r.y + ((dy > 0) ? 0 : r.height) - dy;
				
				boolean extend = ((right > 0) && (bottom > 0));
				
				boolean refresh = false;
				if (r.x == 0)
				{
					if (incx > 0)
					{
						Object[] cells = graph.getChildCells(graph.getDefaultParent());
						for (Object cell : cells)
						{
							if (cell instanceof VPool)
							{
								mxGeometry geo = ((VPool) cell).getGeometry();
								geo.setX(geo.getX() + incx / graph.getView().getScale());
								mdx = dx;
							}
						}
						refresh = true;
					}
					else
					{
						mdx = 0;
						start.x = e.getPoint().x;
					}
				}
				
				if (r.y == 0)
				{
				if (incy > 0)
					{
						Object[] cells = graph.getChildCells(graph.getDefaultParent());
						for (Object cell : cells)
						{
							if (cell instanceof VPool)
							{
								mxGeometry geo = ((VPool) cell).getGeometry();
								geo.setY(geo.getY() + incy / graph.getView().getScale());
								mdy = dy;
							}
						}
						refresh = true;
					}
					else
					{
						mdy = 0;
						start.y = e.getPoint().y;
					}
				}
				
				if (refresh)
				{
					graph.refresh();
				}
				
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
